/*******************************************************************************
 * Copyright (C) 2005, 2019 Wolfgang Schramm and Contributors
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 *******************************************************************************/
package net.tourbook.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import net.tourbook.common.UI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WWOHourlyResults {
   private String                 time;

   private String                 windspeedKmph;

   private String                 winddirDegree;

   private List<WWOValuesResults> weatherDesc;

   private String                 weatherCode;

   @JsonProperty("FeelsLikeC")
   private String                 FeelsLikeC;

   @JsonProperty("FeelsLikeF")
   private String                 FeelsLikeF;

   private String                 tempC;
   private String                 pressure;
   private String                 humidity;
   private String                 pressureInches;

   private String                 precipMM;
   private String                 precipInches;

   public String getFeelsLikeC() {
      return FeelsLikeC;
   }

   public String getFeelsLikeF() {
      return FeelsLikeF;
   }

   public String getHumidity() {
      return humidity;
   }

   public String getPrecipInches() {
      return precipInches;
   }

   public String getPrecipMM() {
      return precipMM;
   }

   public String getPressure() {
      return pressure;
   }

   public String getPressureInches() {
      return pressureInches;
   }

   public String getTempC() {
      return tempC;
   }

   public String gettime() {
      return time;
   }

   public String getWeatherCode() {
      return weatherCode;
   }

   public List<WWOValuesResults> getWeatherDesc() {
      return weatherDesc;
   }

   public String getWeatherDescription(final WWOWeatherResults weatherResults) {
      final StringBuilder weatherDescription = new StringBuilder(weatherDesc.get(0).getValue());

      weatherDescription.append(", Wind Chill: ");
      if (UI.UNIT_VALUE_TEMPERATURE == 1) { // Metric
         weatherDescription.append(FeelsLikeC != null ? FeelsLikeC : "--");
         weatherDescription.append(" " + UI.UNIT_TEMPERATURE_C);
      } else // Imperial
      {
         weatherDescription.append(FeelsLikeF != null ? FeelsLikeF : "--");
         weatherDescription.append(" " + UI.UNIT_TEMPERATURE_F);
      }

      //Max temp
      weatherDescription.append(", Max: ");
      if (UI.UNIT_VALUE_TEMPERATURE == 1) { // Metric
         weatherDescription.append(weatherResults.getmaxtempC() != null ? weatherResults.getmaxtempC() : "--");
         weatherDescription.append(" " + UI.UNIT_TEMPERATURE_C);
      } else // Imperial
      {
         weatherDescription.append(weatherResults.getMaxtempF() != null ? weatherResults.getMaxtempF() : "--");
         weatherDescription.append(" " + UI.UNIT_TEMPERATURE_F);
      }

      //Min temp
      weatherDescription.append(", Min: ");
      if (UI.UNIT_VALUE_TEMPERATURE == 1) { // Metric
         weatherDescription.append(weatherResults.getmintempC() != null ? weatherResults.getmintempC() : "--");
         weatherDescription.append(" " + UI.UNIT_TEMPERATURE_C);
      } else // Imperial
      {
         weatherDescription.append(weatherResults.getMintempF() != null ? weatherResults.getMintempF() : "--");
         weatherDescription.append(" " + UI.UNIT_TEMPERATURE_F);
      }

      //Precipitation
      weatherDescription.append(", Precip: ");

      if (UI.UNIT_VALUE_TEMPERATURE == 1) { // Metric
         weatherDescription.append(precipMM != null ? precipMM : "--");
         weatherDescription.append(" " + UI.UNIT_MM);
      } else // Imperial
      {
         weatherDescription.append(precipInches != null ? precipInches : "--");
         weatherDescription.append(" " + UI.UNIT_DISTANCE_INCH);
      }

      //Pressure
      weatherDescription.append(", Pressure: ");
      if (UI.UNIT_VALUE_TEMPERATURE == 1) { // Metric
         weatherDescription.append(pressure != null ? pressure : "--");
         weatherDescription.append(" " + UI.UNIT_PRESSURE_MB);
      } else // Imperial
      {
         weatherDescription.append(pressureInches != null ? pressureInches : "--");
         weatherDescription.append(" " + UI.UNIT_DISTANCE_INCH);
      }

      //Humidity
      weatherDescription.append(", Humidity: ");
      weatherDescription.append(humidity != null ? humidity : "--");
      weatherDescription.append("" + UI.SYMBOL_PERCENTAGE);

      return weatherDescription.toString();
   }

   public String getWinddirDegree() {
      return winddirDegree;
   }

   public String getWindspeedKmph() {
      return windspeedKmph;
   }
}
