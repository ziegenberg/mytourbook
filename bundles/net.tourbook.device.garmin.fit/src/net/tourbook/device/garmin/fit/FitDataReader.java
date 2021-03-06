/*******************************************************************************
 * Copyright (C) 2005, 2017 Wolfgang Schramm and Contributors
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
package net.tourbook.device.garmin.fit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import net.tourbook.common.time.TimeTools;
import net.tourbook.data.TourData;
import net.tourbook.device.garmin.fit.listeners.ActivityMesgListenerImpl;
import net.tourbook.device.garmin.fit.listeners.BikeProfileMesgListenerImpl;
import net.tourbook.device.garmin.fit.listeners.DeviceInfoMesgListenerImpl;
import net.tourbook.device.garmin.fit.listeners.EventMesgListenerImpl;
import net.tourbook.device.garmin.fit.listeners.FileCreatorMesgListenerImpl;
import net.tourbook.device.garmin.fit.listeners.FileIdMesgListenerImpl;
import net.tourbook.device.garmin.fit.listeners.LapMesgListenerImpl;
import net.tourbook.device.garmin.fit.listeners.RecordMesgListenerImpl;
import net.tourbook.device.garmin.fit.listeners.SessionMesgListenerImpl;
import net.tourbook.importdata.DeviceData;
import net.tourbook.importdata.SerialParameters;
import net.tourbook.importdata.TourbookDevice;
import net.tourbook.tour.TourLogManager;

import org.apache.commons.io.IOUtils;

import com.garmin.fit.Decode;
import com.garmin.fit.Field;
import com.garmin.fit.Fit;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.MesgListener;

/**
 * Garmin FIT activity reader based on the official Garmin SDK.
 * 
 * @author Marcin Kuthan <marcin.kuthan@gmail.com>
 * @author Wolfgang Schramm
 */
public class FitDataReader extends TourbookDevice {

	private static boolean	_isLogFitData	= System.getProperty("logFitData") != null;	//$NON-NLS-1$

	private static boolean	_isVersionLogged;

	private void addAllLogListener(final MesgBroadcaster broadcaster) {

		broadcaster.addListener(new MesgListener() {
			@Override
			public void onMesg(final Mesg mesg) {

				long garminTimestamp = 0;

				for (final Field field : mesg.getFields()) {

					final String fieldName = field.getName();

//					if (fieldName.equals("temperature")) { //$NON-NLS-1$
//						int a = 0;
//						a++;
//					}

					if (fieldName.equals("timestamp")) { //$NON-NLS-1$
						garminTimestamp = (Long) field.getValue();
					}

					/*
					 * Set fields which should NOT be displayed in the log
					 */
					if (fieldName.equals("") // //$NON-NLS-1$

//							|| fieldName.equals("name") //$NON-NLS-1$
//							|| fieldName.equals("time") //$NON-NLS-1$
//							|| fieldName.equals("timestamp") //$NON-NLS-1$
//
//					//
//					// record data
//					//
//
//							|| fieldName.equals("activity_type") //$NON-NLS-1$
//							|| fieldName.equals("event") //$NON-NLS-1$
//							|| fieldName.equals("event_type") //$NON-NLS-1$
//							|| fieldName.equals("message_index") //$NON-NLS-1$
//
//							|| fieldName.equals("altitude") //$NON-NLS-1$
//							|| fieldName.equals("cadence") //$NON-NLS-1$
//							|| fieldName.equals("distance") //$NON-NLS-1$
//							|| fieldName.equals("fractional_cadence") //$NON-NLS-1$
//							|| fieldName.equals("grade") //$NON-NLS-1$
//							|| fieldName.equals("heart_rate") //$NON-NLS-1$
//							|| fieldName.equals("position_lat") //$NON-NLS-1$
//							|| fieldName.equals("position_long") //$NON-NLS-1$
//							|| fieldName.equals("speed") //$NON-NLS-1$
//							|| fieldName.equals("compressed_speed_distance") //$NON-NLS-1$
//							|| fieldName.equals("temperature") //$NON-NLS-1$
//
//							|| fieldName.equals("front_gear") //$NON-NLS-1$
//							|| fieldName.equals("front_gear_num") //$NON-NLS-1$
//							|| fieldName.equals("rear_gear") //$NON-NLS-1$
//							|| fieldName.equals("rear_gear_num") //$NON-NLS-1$
//
//							|| fieldName.equals("enhanced_altitude") //$NON-NLS-1$
//							|| fieldName.equals("enhanced_min_altitude") //$NON-NLS-1$
//							|| fieldName.equals("enhanced_max_altitude") //$NON-NLS-1$
//							|| fieldName.equals("enhanced_speed") //$NON-NLS-1$
//							|| fieldName.equals("enhanced_avg_speed") //$NON-NLS-1$
//							|| fieldName.equals("enhanced_max_speed") //$NON-NLS-1$
//
//							|| fieldName.equals("stance_time") //$NON-NLS-1$
//							|| fieldName.equals("stance_time_percent") //$NON-NLS-1$
//							|| fieldName.equals("vertical_oscillation") //$NON-NLS-1$
//
//					//
//					// lap data
//					//
//							|| fieldName.equals("avg_cadence") //$NON-NLS-1$
//							|| fieldName.equals("avg_fractional_cadence") //$NON-NLS-1$
//							|| fieldName.equals("avg_heart_rate") //$NON-NLS-1$
//							|| fieldName.equals("avg_speed") //$NON-NLS-1$
//							|| fieldName.equals("data") //$NON-NLS-1$
//							|| fieldName.equals("event_group") //$NON-NLS-1$
//							|| fieldName.equals("end_position_lat") //$NON-NLS-1$
//							|| fieldName.equals("end_position_long") //$NON-NLS-1$
//							|| fieldName.equals("intensity") //$NON-NLS-1$
//							|| fieldName.equals("lap_trigger") //$NON-NLS-1$
//							|| fieldName.equals("max_cadence") //$NON-NLS-1$
//							|| fieldName.equals("max_fractional_cadence") //$NON-NLS-1$
//							|| fieldName.equals("max_heart_rate") //$NON-NLS-1$
//							|| fieldName.equals("max_speed") //$NON-NLS-1$
//							|| fieldName.equals("total_calories") //$NON-NLS-1$
//							|| fieldName.equals("total_fat_calories") //$NON-NLS-1$
//							|| fieldName.equals("sport") //$NON-NLS-1$
//							|| fieldName.equals("start_position_lat") //$NON-NLS-1$
//							|| fieldName.equals("start_position_long") //$NON-NLS-1$
//							|| fieldName.equals("start_time") //$NON-NLS-1$
//							|| fieldName.equals("total_ascent") //$NON-NLS-1$
//							|| fieldName.equals("total_descent") //$NON-NLS-1$
//							|| fieldName.equals("total_cycles") //$NON-NLS-1$
//							|| fieldName.equals("total_distance") //$NON-NLS-1$
//							|| fieldName.equals("total_elapsed_time") //$NON-NLS-1$
//							|| fieldName.equals("total_timer_time") //$NON-NLS-1$
//
//							|| fieldName.equals("avg_stance_time") //$NON-NLS-1$
//							|| fieldName.equals("avg_stance_time_percent") //$NON-NLS-1$
//							|| fieldName.equals("avg_vertical_oscillation") //$NON-NLS-1$
//
//					// power
//							|| fieldName.equals("power") //$NON-NLS-1$
//							|| fieldName.equals("accumulated_power") //$NON-NLS-1$
//							|| fieldName.equals("left_right_balance") //$NON-NLS-1$
//							|| fieldName.equals("left_torque_effectiveness") //$NON-NLS-1$
//							|| fieldName.equals("right_torque_effectiveness") //$NON-NLS-1$
//							|| fieldName.equals("left_pedal_smoothness") //$NON-NLS-1$
//							|| fieldName.equals("right_pedal_smoothness") //$NON-NLS-1$
//
//							|| fieldName.equals("functional_threshold_power") //$NON-NLS-1$
//							|| fieldName.equals("power_setting") //$NON-NLS-1$
//							|| fieldName.equals("pwr_calc_type") //$NON-NLS-1$
//
//					// device
//							|| fieldName.equals("ant_network") //$NON-NLS-1$
//							|| fieldName.equals("battery_status") //$NON-NLS-1$
//							|| fieldName.equals("battery_voltage") //$NON-NLS-1$
//							|| fieldName.equals("cum_operating_time") //$NON-NLS-1$
//							|| fieldName.equals("device_index") //$NON-NLS-1$
//							|| fieldName.equals("device_type") //$NON-NLS-1$
//							|| fieldName.equals("friendly_name") //$NON-NLS-1$
//							|| fieldName.equals("hardware_version") //$NON-NLS-1$
//							|| fieldName.equals("manufacturer") //$NON-NLS-1$
//							|| fieldName.equals("product") //$NON-NLS-1$
//							|| fieldName.equals("serial_number") //$NON-NLS-1$
//							|| fieldName.equals("software_version") //$NON-NLS-1$
//							|| fieldName.equals("source_type") //$NON-NLS-1$
//
//							|| fieldName.equals("trigger") //$NON-NLS-1$
//							|| fieldName.equals("type") //$NON-NLS-1$
//							|| fieldName.equals("num_laps") //$NON-NLS-1$
//							|| fieldName.equals("num_sessions") //$NON-NLS-1$
//							|| fieldName.equals("sport_index") //$NON-NLS-1$
//							|| fieldName.equals("sub_sport") //$NON-NLS-1$
//
//							|| fieldName.equals("activity_class") //$NON-NLS-1$
//							|| fieldName.equals("default_max_biking_heart_rate") //$NON-NLS-1$
//							|| fieldName.equals("default_max_heart_rate") //$NON-NLS-1$
//							|| fieldName.equals("first_lap_index") //$NON-NLS-1$
//							|| fieldName.equals("hr_calc_type") //$NON-NLS-1$
//							|| fieldName.equals("hr_setting") //$NON-NLS-1$
//
//							|| fieldName.equals("nec_lat") //$NON-NLS-1$
//							|| fieldName.equals("nec_long") //$NON-NLS-1$
//							|| fieldName.equals("swc_lat") //$NON-NLS-1$
//							|| fieldName.equals("swc_long") //$NON-NLS-1$
//
//							|| fieldName.equals("active_time_zone") //$NON-NLS-1$
//							|| fieldName.equals("local_timestamp") //$NON-NLS-1$
//							|| fieldName.equals("time_created") //$NON-NLS-1$
//							|| fieldName.equals("time_offset") //$NON-NLS-1$
//							|| fieldName.equals("time_zone_offset") //$NON-NLS-1$
//							|| fieldName.equals("utc_offset") //$NON-NLS-1$
//
//							|| fieldName.equals("age") //$NON-NLS-1$
//							|| fieldName.equals("gender") //$NON-NLS-1$
//							|| fieldName.equals("height") //$NON-NLS-1$
//							|| fieldName.equals("weight") //$NON-NLS-1$
//							|| fieldName.equals("language") //$NON-NLS-1$
//
//							|| fieldName.equals("dist_setting") //$NON-NLS-1$
//							|| fieldName.equals("elev_setting") //$NON-NLS-1$
//							|| fieldName.equals("position_setting") //$NON-NLS-1$
//							|| fieldName.equals("speed_setting") //$NON-NLS-1$
//							|| fieldName.equals("temperature_setting") //$NON-NLS-1$
//							|| fieldName.equals("weight_setting") //$NON-NLS-1$
//
//					//
//							|| fieldName.equals("unknown") //$NON-NLS-1$
					//
					) {
						continue;
					}

					final long javaTime = (garminTimestamp * 1000) + com.garmin.fit.DateTime.OFFSET;

					System.out.println(
							String.format(
									"%s %d %s %-5d %-30s %20s %s", //$NON-NLS-1$
									TimeTools.getZonedDateTime(javaTime), // show readable date/time
									javaTime / 1000,
									Long.toString(garminTimestamp),
									field.getNum(),
									fieldName,
									field.getValue(),
									field.getUnits()));
				}
			}
		});
	}

	@Override
	public String buildFileNameFromRawData(final String rawDataFileName) {
		return null;
	}

	@Override
	public boolean checkStartSequence(final int byteIndex, final int newByte) {
		return false;
	}

	@Override
	public String getDeviceModeName(final int modeId) {
		return null;
	}

	@Override
	public SerialParameters getPortParameters(final String portName) {
		return null;
	}

	@Override
	public int getStartSequenceSize() {
		return 0;
	}

	@Override
	public int getTransferDataSize() {
		return 0;
	}

	@Override
	public boolean processDeviceData(	final String importFilePath,
										final DeviceData deviceData,
										final HashMap<Long, TourData> alreadyImportedTours,
										final HashMap<Long, TourData> newlyImportedTours) {

		boolean returnValue = false;

		try (FileInputStream fis = new FileInputStream(importFilePath)) {

			final MesgBroadcaster broadcaster = new MesgBroadcaster(new Decode());

			final FitContext context = new FitContext(//
					this,
					importFilePath,
					alreadyImportedTours,
					newlyImportedTours);

			// setup all fit listeners
			broadcaster.addListener(new ActivityMesgListenerImpl(context));
			broadcaster.addListener(new BikeProfileMesgListenerImpl(context));
			broadcaster.addListener(new DeviceInfoMesgListenerImpl(context));
			broadcaster.addListener(new EventMesgListenerImpl(context));
			broadcaster.addListener(new FileCreatorMesgListenerImpl(context));
			broadcaster.addListener(new FileIdMesgListenerImpl(context));
			broadcaster.addListener(new LapMesgListenerImpl(context));
			broadcaster.addListener(new RecordMesgListenerImpl(context));
			broadcaster.addListener(new SessionMesgListenerImpl(context));

			if (_isLogFitData) {

				//
				// START - show debug info
				//

				System.out.println();
				System.out.println();
				System.out.println(
						(System.currentTimeMillis() + " [" + getClass().getSimpleName() + "]")
								+ (" \t" + importFilePath));
				System.out.println();
				System.out.println(
						String.format(//
								"%s %-5s %-30s %20s %s", //$NON-NLS-1$
								"Timestamp",
								"Num",
								"Name",
								"Value",
								"Units"));
				System.out.println();

				addAllLogListener(broadcaster);

				//
				// END - show debug info
				//
			}

			broadcaster.run(fis);

			context.finalizeTour();

			returnValue = true;

		} catch (final IOException e) {
			TourLogManager.logError_CannotReadDataFile(importFilePath, e);
		}

		return returnValue;
	}

	@Override
	public boolean validateRawData(final String fileName) {

		boolean returnValue = false;
		FileInputStream fis = null;

		try {

			fis = new FileInputStream(fileName);
			returnValue = new Decode().checkFileIntegrity(fis);

			if (returnValue) {

				// log version if not yet done

				if (_isVersionLogged == false) {

					TourLogManager.logInfo(
							String.format(
									"FIT SDK %d.%d", //$NON-NLS-1$
									Fit.PROFILE_VERSION_MAJOR,
									Fit.PROFILE_VERSION_MINOR));

					_isVersionLogged = true;
				}

			} else {

				TourLogManager.logError(
						String.format(
								"FIT checkFileIntegrity failed '%s' - FIT SDK %d.%d", //$NON-NLS-1$
								fileName,
								Fit.PROFILE_VERSION_MAJOR,
								Fit.PROFILE_VERSION_MINOR));
			}

		} catch (final FileNotFoundException e) {
			TourLogManager.logError_CannotReadDataFile(fileName, e);
		} catch (final FitRuntimeException e) {
			TourLogManager.logEx(String.format("Invalid data file '%s'", fileName), e); //$NON-NLS-1$
		} finally {
			IOUtils.closeQuietly(fis);
		}

		return returnValue;
	}

}
