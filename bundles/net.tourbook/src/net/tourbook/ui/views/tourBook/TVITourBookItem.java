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
package net.tourbook.ui.views.tourBook;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

import net.tourbook.common.time.TimeTools;
import net.tourbook.common.time.TourDateTime;
import net.tourbook.common.util.TreeViewerItem;
import net.tourbook.tour.ITourItem;
import net.tourbook.ui.UI;

public abstract class TVITourBookItem extends TreeViewerItem implements ITourItem {

	static ZonedDateTime	calendar8	= ZonedDateTime.now().with(TimeTools.calendarWeek.dayOfWeek(), 1);

	static final String		SQL_SUM_COLUMNS;

	static {

		SQL_SUM_COLUMNS = UI.EMPTY_STRING
				//
				+ "SUM(TOURDISTANCE),\n" // 							0	//$NON-NLS-1$
				+ "SUM(TOURRECORDINGTIME),\n" //						1	//$NON-NLS-1$
				+ "SUM(TOURDRIVINGTIME),\n" //							2	//$NON-NLS-1$
				+ "SUM(TOURALTUP),\n" //								3	//$NON-NLS-1$
				+ "SUM(TOURALTDOWN),\n" //								4	//$NON-NLS-1$
				+ "SUM(1),\n" //										5	//$NON-NLS-1$
				//
				+ "MAX(MAXSPEED),\n" //									6	//$NON-NLS-1$
				+ "SUM(TOURDISTANCE),\n" //								7	//$NON-NLS-1$
				+ "SUM(TOURDRIVINGTIME),\n" //							8	//$NON-NLS-1$
				+ "MAX(MAXALTITUDE),\n" //								9	//$NON-NLS-1$
				+ "MAX(MAXPULSE),\n" //									10	//$NON-NLS-1$
				//
				+ "AVG( CASE WHEN AVGPULSE = 0			THEN NULL ELSE AVGPULSE END),\n" //										11	//$NON-NLS-1$
				+ "AVG( CASE WHEN AVGCADENCE = 0		THEN NULL ELSE DOUBLE(AvgCadence) * CadenceMultiplier END ),\n" //		12	//$NON-NLS-1$
				+ "AVG( CASE WHEN AvgTemperature = 0	THEN NULL ELSE DOUBLE(AvgTemperature) / TemperatureScale END ),\n" //	13	//$NON-NLS-1$
				+ "AVG( CASE WHEN WEATHERWINDDIR = 0	THEN NULL ELSE WEATHERWINDDIR END ),\n" //								14	//$NON-NLS-1$
				+ "AVG( CASE WHEN WEATHERWINDSPD = 0	THEN NULL ELSE WEATHERWINDSPD END ),\n" //								15	//$NON-NLS-1$
				+ "AVG( CASE WHEN RESTPULSE = 0			THEN NULL ELSE RESTPULSE END ),\n" //									16	//$NON-NLS-1$
				//
				+ "SUM(CALORIES),\n" //									17	//$NON-NLS-1$
				+ "SUM(power_TotalWork),\n" //							18	//$NON-NLS-1$

				+ "SUM(numberOfTimeSlices),\n" //						19	//$NON-NLS-1$
				+ "SUM(numberOfPhotos),\n" //							20	//$NON-NLS-1$
				//
				+ "SUM(frontShiftCount),\n" //							21	//$NON-NLS-1$
				+ "SUM(rearShiftCount)\n"; //							22	//$NON-NLS-1$
	}

	TourBookView	tourBookView;

	String			treeColumn;

	int				tourYear;

	/**
	 * month starts with 1 for january
	 */
	int				tourMonth;
	int				tourWeek;
	int				tourYearSub;
	int				tourDay;

	/**
	 * Contain the tour date time with time zone info when available.
	 */
	TourDateTime	colTourDateTime;
	String			colTimeZoneId;

	String			colTourTitle;
	long			colPersonId;							// tourPerson_personId

	long			colCounter;
	long			colCalories;
	long			colDistance;
	float			colBodyWeight;

	long			colRecordingTime;
	long			colDrivingTime;
	long			colPausedTime;

	long			colAltitudeUp;
	long			colAltitudeDown;

	float			colMaxSpeed;
	long			colMaxAltitude;
	long			colMaxPulse;

	float			colAvgSpeed;
	float			colAvgPace;
	float			colAvgPulse;
	float			colAvgCadence;
	float			colAvgTemperature;

	int				colWindSpd;
	int				colWindDir;
	String			colClouds;
	int				colRestPulse;

	int				colWeekNo;
	String			colWeekDay;
	int				colWeekYear;

	int				colNumberOfTimeSlices;
	int				colNumberOfPhotos;

	int				colDPTolerance;

	int				colFrontShiftCount;
	int				colRearShiftCount;

	float			colCadenceMultiplier;

	// ----------- POWER ---------

	float			colPower_AvgLeftTorqueEffectiveness;
	float			colPower_AvgRightTorqueEffectiveness;
	float			colPower_AvgLeftPedalSmoothness;
	float			colPower_AvgRightPedalSmoothness;
	int				colPower_PedalLeftRightBalance;

	float			colPower_Avg;
	int				colPower_Max;
	int				colPower_Normalized;
	long			colPower_TotalWork;

	int				colPower_FTP;
	float			colPower_TrainingStressScore;
	float			colPower_IntensityFactor;

	float			colPower_PowerToWeight;

	// ----------- IMPORT ---------

	String			col_ImportFileName;
	String			col_ImportFilePath;
	String			col_DeviceName;

	TVITourBookItem(final TourBookView view) {

		tourBookView = view;
	}

	public void addSumColumns(final ResultSet result, final int startIndex) throws SQLException {

		colDistance = result.getLong(startIndex + 0);

		colRecordingTime = result.getLong(startIndex + 1);
		colDrivingTime = result.getLong(startIndex + 2);

		colAltitudeUp = result.getLong(startIndex + 3);
		colAltitudeDown = result.getLong(startIndex + 4);

		colCounter = result.getLong(startIndex + 5);

		colMaxSpeed = result.getFloat(startIndex + 6);

		// compute average speed/pace, prevent divide by 0
		final long dbDistance = result.getLong(startIndex + 7);
		final long dbDrivingTime = result.getLong(startIndex + 8);

		colAvgSpeed = dbDrivingTime == 0 ? 0 : 3.6f * dbDistance / dbDrivingTime;
		colAvgPace = dbDistance == 0 ? 0 : dbDrivingTime * 1000f / dbDistance;

		colMaxAltitude = result.getLong(startIndex + 9);
		colMaxPulse = result.getLong(startIndex + 10);

		colAvgPulse = result.getFloat(startIndex + 11);
		colAvgCadence = result.getFloat(startIndex + 12);
		colAvgTemperature = result.getFloat(startIndex + 13);

		colWindDir = result.getInt(startIndex + 14);
		colWindSpd = result.getInt(startIndex + 15);
		colRestPulse = result.getInt(startIndex + 16);

		colCalories = result.getLong(startIndex + 17);
		colPower_TotalWork = result.getLong(startIndex + 18);

		colNumberOfTimeSlices = result.getInt(startIndex + 19);
		colNumberOfPhotos = result.getInt(startIndex + 20);

		colFrontShiftCount = result.getInt(startIndex + 21);
		colRearShiftCount = result.getInt(startIndex + 22);

		colPausedTime = colRecordingTime - colDrivingTime;
	}

	@Override
	public Long getTourId() {
		return null;
	}

}
