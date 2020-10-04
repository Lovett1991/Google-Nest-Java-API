package com.alexlovett.nestapi.devices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.time.ZonedDateTime;
import java.util.Collection;

import static com.alexlovett.nestapi.devices.Devices.Device.Type.THRMOSTAT;

public interface Devices {

    @GET("enterprises/{projectId}/devices")
    Call<Collection<Device>> getDevices(@Path("projectId") String projectId);

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
        public static class Fan {

            private Status timerMode;
            private ZonedDateTime timerTimeout;

            enum Status {
                ON,
                OFF
            }
        }

        @Getter
        public static class Humidity {

            @JsonProperty("ambientHumidityPercent")
            private double percent;
        }

        @Getter
        public static class Settings {

            private TemperatureScale temperatureScale;

            enum TemperatureScale {
                CELSIUS,
                FAHRENHEIT
            }
        }

        @Getter
        public static class Temperature {

            @JsonProperty("ambientTemperatureCelsius")
            private double ambient;
        }

        @Getter
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
        public static class Hvac {

            private Status status;

            enum Status {
                HEATING,
                COOLING,
                OFF
            }
        }

        @Getter
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
        public static class TargetTemperature {

            private double heatCelsius;

            private double coolCelsius;
        }
    }
}
