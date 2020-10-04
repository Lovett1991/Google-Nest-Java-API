package com.alexlovett.nestapi.devices;

import com.alexlovett.nestapi.auth.OAuth;
import com.alexlovett.nestapi.auth.Token;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.time.ZonedDateTime;
import java.util.Collection;

import static com.alexlovett.nestapi.devices.DeviceAPI.Device.Type.THRMOSTAT;

public interface DeviceAPI {

    @GET("enterprises/{projectId}/devices")
    Call<DevicesResponse> getDevices(@Path("projectId") String projectId);

    @Getter
    class DevicesResponse {
        private Collection<Device> devices;
    }

    static DeviceApiBuilder builder() {
        return new DeviceApiBuilder();
    }

    class DeviceApiBuilder {

        private OkHttpClient client = new OkHttpClient();

        private String baseUrl = "https://smartdevicemanagement.googleapis.com/v1/";

        private Token token;

        private OAuth.TokenRefresher tokenRefresher;

        public DeviceApiBuilder client(OkHttpClient client){
            this.client = client;
            return this;
        }

        public DeviceApiBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public DeviceApiBuilder token(Token token) {
            this.token = token;
            return this;
        }

        public DeviceApiBuilder tokenRefresher(OAuth.TokenRefresher tokenRefresher) {
            this.tokenRefresher = tokenRefresher;
            return this;
        }

        public DeviceAPI build() {

            if (token == null){
                throw new IllegalArgumentException("Token cannot be null");
            }

            if (tokenRefresher == null){
                throw new IllegalArgumentException("Token refresher cannot be null");
            }

            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client.newBuilder()
                            .addInterceptor(chain -> chain.proceed(
                                    chain.request()
                                            .newBuilder()
                                            .addHeader("Authorization", String.format("Bearer %s",token.getCurrent()))
                                            .build()))
                            .authenticator((route, response) -> {
                                token.update(tokenRefresher.apply(token));
                                return response.request().newBuilder().header("Authorization", String.format("Bearer %s", token.getCurrent())).build();
                            })
                            .build())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
                    .create(DeviceAPI.class);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Thermostat.class, name  = "sdm.devices.types.THERMOSTAT")
    })
    @Setter
    abstract class Device {

        private String name;

        public abstract Type getType();

        @JsonProperty("sdm.devices.traits.Connectivity")
        private Thermostat.Connectivity connectivity;

        @JsonProperty("sdm.devices.traits.Info")
        private Thermostat.Info info;

        @Getter
        public static class Connectivity {

            private Status status;

            enum Status {
                ONLINE,
                OFFLINE
            }
        }

        public static class Info {

            private String customName;
        }

        @RequiredArgsConstructor
        enum Type {
            THRMOSTAT("sdm.devices.types.THERMOSTAT"),
            UNKNOWN("");

            @Getter
            private final String identifier;
        }
    }

    @Setter
    class Thermostat extends Device {

        @JsonProperty("sdm.devices.traits.Connectivity")
        private Connectivity connectivity;

        @JsonProperty("sdm.devices.traits.Fan")
        private Fan fan;

        @JsonProperty("sdm.devices.traits.Humidity")
        private Humidity humidity;

        @JsonProperty("sdm.devices.traits.Settings")
        private Settings settings;

        @JsonProperty("sdm.devices.traits.Temperature")
        private Temperature temperature;

        @JsonProperty("sdm.devices.traits.ThermostatEco")
        private EcoMode ecoMode;

        @JsonProperty("sdm.devices.traits.ThermostatHvac")
        private Hvac hvac;

        @JsonProperty("sdm.devices.traits.ThermostatMode")
        private ThermostatMode thermostatMode;

        @Override
        public Type getType() {
            return THRMOSTAT;
        }

        @Getter
        @Setter
        public static class Fan {

            private Status timerMode;
            private ZonedDateTime timerTimeout;

            enum Status {
                ON,
                OFF
            }
        }

        @Getter
        @Setter
        public static class Humidity {

            @JsonProperty("ambientHumidityPercent")
            private double percent;
        }

        @Getter
        @Setter
        public static class Settings {

            private TemperatureScale temperatureScale;

            enum TemperatureScale {
                CELSIUS,
                FAHRENHEIT
            }
        }

        @Getter
        @Setter
        public static class Temperature {

            @JsonProperty("ambientTemperatureCelsius")
            private double ambient;
        }

        @Getter
        @Setter
        public static class EcoMode {

            private Collection<Mode> availableModes;

            private Mode mode;

            private double heatCelsius;

            private double coolCelsius;

            enum Mode {
                MANUAL_ECO,
                OFF
            }
        }

        @Getter
        @Setter
        public static class Hvac {

            private Status status;

            enum Status {
                HEATING,
                COOLING,
                OFF
            }
        }

        @Getter
        @Setter
        public static class ThermostatMode {

            private Collection<Mode> availableModes;

            private Mode mode;

            enum Mode {
                HEAT,
                COOL,
                HEATCOOL,
                OFF
            }
        }

        @Getter
        @Setter
        public static class TargetTemperature {

            private double heatCelsius;

            private double coolCelsius;
        }
    }
}
