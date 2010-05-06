/*******************************************************************************
 * Copyright (C) 2005, 2010  Wolfgang Schramm and Contributors
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
 ********************************************************************************
 *
 * @author Meinhard Ritscher
 *
 ********************************************************************************

This class implements reading tour files written using HAC4Linux
http://hac4linux.sf.net
********************************************************************************/
package net.tourbook.device.hac4linux.tour;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.tourbook.chart.ChartLabel;
import net.tourbook.data.TimeData;
import net.tourbook.data.TourData;
import net.tourbook.data.TourMarker;
import net.tourbook.data.TourPerson;
import net.tourbook.data.TourType;
import net.tourbook.importdata.DeviceData;
import net.tourbook.importdata.SerialParameters;
import net.tourbook.importdata.TourbookDevice;


public class HAC4LinuxDeviceReader extends TourbookDevice {
	private Section m_section = Section.SECTION_NONE;;
	/*
	 * (non-Javadoc)
	 * The file to be parsed includes several sections with different information
	 * in it. This device reader operates similar to a state machine.
	 * Every section possible is reflected in one of the states to be found below
	 */
	private enum Section{
	    SECTION_NONE,
	    SECTION_FILE,
	    SECTION_INFO,
	    SECTION_NOTES,
	    SECTION_FRIENDS,
	    SECTION_PERSON,
	    SECTION_STATISTICS,
	    SECTION_SETTINGS,
	    SECTION_POLAREXTS,
	    SECTION_COACH,
	    SECTION_TOURDATA,
	    SECTION_MARKS
	}
	
	/**
	 * The box standard constructor
	 */
	public HAC4LinuxDeviceReader() {
		canReadFromDevice = false;
		// TODO
		canSelectMultipleFilesInImportDialog = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.tourbook.importdata.TourbookDevice#buildFileNameFromRawData(java.lang.String)
	 */
	@Override
	public String buildFileNameFromRawData(final String rawDataFileName) {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.tourbook.importdata.TourbookDevice#checkStartSequence(int, int)
	 */
	@Override
	public boolean checkStartSequence(final int byteIndex, final int newByte) {

		/*
		 * Files start with
		 * HAC4Linux-Tour-File created by HAC4Linux (c) by Rick-Rainer Ludwig
		 */
		if (byteIndex == 0 & newByte == 'H') {
			return true;
		}
		if (byteIndex == 1 & newByte == 'A') {
			return true;
		}
		if (byteIndex == 2 & newByte == 'C') {
			return true;
		}
		if (byteIndex == 3 & newByte == '4') {
			return true;
		}
		if (byteIndex == 4 & newByte == 'L') {
			return true;
		}
		if (byteIndex == 5 & newByte == 'i') {
			return true;
		}
		if (byteIndex == 6 & newByte == 'n') {
			return true;
		}
		if (byteIndex == 7 & newByte == 'u') {
			return true;
		}
		if (byteIndex == 8 & newByte == 'x') {
			return true;
		}
		if (byteIndex == 9 & newByte == '-') {
			return true;
		}
		if (byteIndex == 10 & newByte == 'T') {
			return true;
		}
		if (byteIndex == 11 & newByte == 'o') {
			return true;
		}
		if (byteIndex == 12 & newByte == 'u') {
			return true;
		}
		if (byteIndex == 13 & newByte == 'r') {
			return true;
		}
		if (byteIndex == 14 & newByte == '-') {
			return true;
		}
		if (byteIndex == 15 & newByte == 'F') {
			return true;
		}
		if (byteIndex == 16 & newByte == 'i') {
			return true;
		}
		if (byteIndex == 17 & newByte == 'l') {
			return true;
		}
		if (byteIndex == 18 & newByte == 'e') {
			return true;
		}

		return false;
	}
	
	/* (non-Javadoc)
	 * Some of the devices compatible with Hac4Linux can store totals for different
	 * equipment (e.g. two bikes)
	 * Within the file totals for every device is stored.
	 * Identifying the totals of the equipment the tour was recorded for (e.g. Bike1)
	 * and at it to the TourData object provided.
	 */
	private void deviceTotals(final String device, final short modeId, final String line, final TourData tourData){
		if(device.equals("CM414AM")){
			tourData.setDeviceTimeInterval((short) 20);
			char bikeNumber = ' ';
			if( modeId == 46) {
				bikeNumber = '2';
			} else if(modeId == 64) {
				bikeNumber = '1';
			} else if(modeId == 99) {
				return;
			}
			final String[] fields = line.split("=");
            if(fields.length < 2) {
				return;
			}
            final char settingsNumber = fields[0].charAt(fields[0].length()-1);
			if(bikeNumber == settingsNumber){
				fields[0] = fields[0].substring(0, fields[0].length()-1 );
                if(fields[0].equals("Distance")) {
					tourData.setStartDistance(Integer.parseInt(fields[1]));
				}
                if(fields[0].equals("HeightUp")) {
					tourData.setDeviceTotalUp(Integer.parseInt(fields[1]));
				}
                //tourData.setDeviceTotalDown(Integer.parseInt(fields[1]));
                // TODO welches Format?!
                //if(fields[0].equals("TotalTime")) tourData.setDeviceTravelTime(Long.parseLong(fields[1]));
                if(fields[0].equals("WheelPerimeter")) {
					tourData.setDeviceWheel(Integer.parseInt(fields[1]));
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.tourbook.importdata.IRawDataReader#getDeviceModeName(int)
	 */
	@Override
	public String getDeviceModeName(final int modeId) {
		// This is true for the CM414 AltiM but might be different
		// for other devices
		if( modeId == 46) {
			return "Bike2";
		} else if(modeId == 64) {
			return "Bike1";
		} else if(modeId == 99) {
			return "Jogging";
		}
		else {
			return ""; //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.tourbook.importdata.TourbookDevice#getPortParameters(java.lang.String)
	 */
	@Override
	public SerialParameters getPortParameters(final String portName) {
		// nothing to read from a device but from a file
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see net.tourbook.importdata.TourbookDevice#getStartSequenceSize()
	 */
	@Override
	public int getStartSequenceSize() {
		return 19;
	}

	private TourType getTourType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see net.tourbook.importdata.IRawDataReader#getTransferDataSize()
	 */
	@Override
	public int getTransferDataSize() {
		// not reading from a device - not relevant
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * Parses records containing the tour data slices all in one row
	 * For examples of a row and the meaning of each of the tab seperated
	 * fields see the comment on the top of the method
	 */
	private TimeData parseRecord(final String line, final int delta, final int lastAlti, final int lastDistance){
		//#Pulse0	Cadence1	Temperature2	Altitude3	Marking4	Gradient5	Speed6	Distance7
		//ClimbSpeed8	AverageSpeed9	SkiSpeed10	Power11 Hour:Minute:Second12
		//
		//!0	0	20	71	0	0.00	0.00	0.00	0.00	0.00	0.00	0.00	00:00:00.00
		//!4	95	20	70	0	-1.11	16.20	90.00	-3.00	16.20	1.20	5.96	00:00:20.00
		//!8	95	20	70	0	0.00	30.60	260.00	0.00	23.40	0.00	212.64	00:00:40.00
		//!12	95	20	70	0	0.00	30.60	430.00	0.00	25.80	0.00	257.47	00:01:00.00

		final TimeData timeData = new TimeData();
		final String[] fields = line.split("\t");
		if(fields.length < 13) {
			return null;
		}
		timeData.pulse = Integer.parseInt(fields[0].substring(1));
		timeData.cadence = Integer.parseInt(fields[1]);
		timeData.temperature = Integer.parseInt(fields[2]);
		final int alti = Integer.parseInt(fields[3]);
		timeData.absoluteAltitude = alti;
		timeData.altitude = alti - lastAlti;
		// time marker will imported in the next section
		// since not all markers may be device markers
		// and it's a bit tricky to reference between this
		// data time slice and the markers in the markers section
		// timeData.marker =  Integer.parseInt(fields[4]);
		final Double speed = Double.parseDouble(fields[6]) * 10.0;
		timeData.speed = speed.intValue();
		final Float dist = Float.parseFloat(fields[7]);
		timeData.absoluteDistance = dist;
		timeData.distance = dist.intValue() - lastDistance;
		final Float power = Float.parseFloat(fields[11]);
		timeData.power = power.intValue();
		timeData.time = delta;
		
		return timeData;
	}

	/*
	 * (non-Javadoc)
	 * @see net.tourbook.importdata.IRawDataReader#processDeviceData(java.lang.String,
	 * net.tourbook.importdata.DeviceData, java.util.HashMap)
	 */
	@Override
	public boolean processDeviceData(	final String importFileName,
										final DeviceData deviceData,
										final HashMap<Long, TourData> tourDataMap) {

		BufferedReader fileHac4LinuxData = null;

		final TourType defaultTourType = getTourType();
		m_section = Section.SECTION_NONE;

		try {
			if (validateRawData(importFileName) == false) {
				return false;
			}
			
			fileHac4LinuxData = new BufferedReader(new FileReader(importFileName));
			final TourData tourData = new TourData();
			final TourPerson tourPerson = new TourPerson();

			String line = null;
			final StringBuffer tourDescription = new StringBuffer();
			final ArrayList<TimeData> timeDataList = new ArrayList<TimeData>();

			// time in seconds between time slices 20 seconds as the deafault
			int deltaTime = 20;
			int lastAlti = 0;
			int lastDistance = 0;
			boolean isFirstTimeSlice = true;
			short modeId = 0;
			
			int friends = -1;
			while((line = fileHac4LinuxData.readLine()) != null){
				if(line.length() <= 0 || line.charAt(0) == '#') {
					continue;
				}
				if(line.charAt(0) == '['){
		            switchSection( line.trim() );
		            continue;
				}

		        final String[] fields;
		        int index = -1;
		        switch(m_section){
		        	case SECTION_FILE:
		        		fields = line.split("=");
		                if(fields.length < 2) {
							break;
						}
		                if(fields[0].equals("Version"))
						 {
							continue; // FileVersion "0.1.0"
						}
		        		break;
		        	case SECTION_INFO:
		                fields = line.split("=");
		                if(fields.length < 2) {
							break;
						}
		                if(fields[0].equals("Number"))
						 {
							continue;//
						}
		                if(fields[0].equals("Title")) {
							tourData.setTourTitle(fields[1]);
						}
		                if(fields[0].equals("Date")){//"dd.MM.yyyy"
		                	tourData.setStartYear(Short.parseShort(fields[1].substring(6)));
		        			tourData.setStartMonth(Short.parseShort(fields[1].substring(3, 5)));
		        			tourData.setStartDay(Short.parseShort(fields[1].substring(0, 2)));
		                }
		                if(fields[0].equals("Time")){//"hh:mm:ss.00"
		        			tourData.setStartHour(Short.parseShort(fields[1].substring(0, 2)));
		        			tourData.setStartMinute(Short.parseShort(fields[1].substring(3, 5)));
		                }
		                if(fields[0].equals("Mode")) {
		                	modeId = Short.parseShort(fields[1]);
		                	tourData.setDeviceMode(modeId);
		                	tourData.setDeviceModeName(getDeviceModeName(Integer.parseInt(fields[1])));
		                }
		                if(fields[0].equals("TimeDriven")){//"hh:mm:ss.00"
		                	int tourDrivingTime = Short.parseShort(fields[1].substring(6, 8));
		                	tourDrivingTime = tourDrivingTime + Short.parseShort(fields[1].substring(3, 5))*60;
		                	tourDrivingTime = tourDrivingTime + Short.parseShort(fields[1].substring(0, 2))*3600;
		                	tourData.setTourDrivingTime(tourDrivingTime);
		                }
		                if(fields[0].equals("RecTime")){//"hh:mm:ss.00"
		                	int tourRecordingTime = Short.parseShort(fields[1].substring(6, 8));
		                	tourRecordingTime = tourRecordingTime + Short.parseShort(fields[1].substring(3, 5))*60;
		                	tourRecordingTime = tourRecordingTime + Short.parseShort(fields[1].substring(0, 2))*3600;
		                	tourData.setTourRecordingTime(tourRecordingTime);
		                }
		                if(fields[0].equals("Distance")) {
							tourData.setTourDistance(Integer.parseInt(fields[1]));
						}
		                if(fields[0].equals("Start")) {
							tourData.setTourStartPlace(fields[1]);
						}
		                if(fields[0].equals("Finish")) {
							tourData.setTourEndPlace(fields[1]);
						}
		                break;
		        	case SECTION_NOTES:
		        		index = line.indexOf('=');
		                if(index < 0) {
							break;
						}
		                tourDescription.append(line.substring(index+1)+"\n");
		        		break;
		        	case SECTION_FRIENDS:
		        		index = line.indexOf('=');
		                if(index < 0) {
							break;
						}
		                if(friends < 0) {
							tourDescription.append("\nFriends: \n");
						}
		                friends++;
		                tourDescription.append(line.substring(index+1)+"\n");
		        		break;
		        	case SECTION_PERSON:
		        		fields = line.split("=");
		                if(fields.length < 2) {
							break;
						}
		                if(fields[0].equals("LastName")) {
							tourPerson.setLastName(fields[1]);
						}
		                if(fields[0].equals("FirstName")) {
							tourPerson.setFirstName(fields[1]);
						}
		                if(fields[0].equals("DateOfBirth"))
						 {
							continue; // tourPerson.set (fields[1]);
						}
		                if(fields[0].equals("SportsClub"))
						 {
							continue; // tourPerson.set (fields[1]);
						}
		                if(fields[0].equals("Equipment"))
						 {
							continue; // tourPerson.set (fields[1]); //tourData.bike?
						}
		                if(fields[0].equals("MaxPulse"))
						 {
							continue; // tourPerson.set (fields[1]);
						}
		                if(fields[0].equals("RestPulse")) {
							tourData.setRestPulse(Integer.parseInt(fields[1]));
						}
		                if(fields[0].equals("Weight")) {
							tourPerson.setWeight( Float.parseFloat(fields[1]));
						}
		        		break;
		        	case SECTION_STATISTICS:
		        		fields = line.split("=");
		                if(fields.length < 2) {
							break;
						}
		                if(fields[0].equals("Rosen")) {
							tourData.setTourAltUp(Integer.parseInt(fields[1]));
						}
		                if(fields[0].equals("Fallen")) {
							tourData.setTourAltDown(Integer.parseInt(fields[1]));
						}
		                //if(fields[0].equals("Altitude")) //Altitude=147;90;69
		                if(fields[0].equals("Temperature")){ //Temperature=24;22;20
		                	final String[] sTemps = fields[1].split(";");
		                	if(sTemps.length > 1) {
								tourData.setAvgTemperature(Integer.parseInt(sTemps[1]));
							}
		                }
		                // TODO !! statistic information with no fields in tour data base!
		                /*
		                 * Climb=24.00;6.21;0.00
		                 * Sink=-48.00;-7.61;0.00
		                 * CountClimb=0
		                 * CountSink=0
		                 * Rise=7.27;1.39;0.00
		                 * Fall=-10.00;-1.48;0.00
		                 * Pulse=252;138;0
		                 * Speed=50.40;30.74;0.00
		                 * Power=881.85;288.95;0.00
		                 * Cadence=112;84;0
		                 * SkiSpeed=19.20;3.05;0.00
		                 * 
		                 */
		        		break;
		        	case SECTION_SETTINGS:
		        		fields = line.split("=");
		                if(fields.length < 2) {
							break;
						}
		                if(fields[0].equals("DeviceShortName")) {
							tourData.setDeviceName(fields[1]);
						}
		                //if(fields[0].equals("DateOfTransfer")) tourData.set //13.04.2007
		                if(fields[0].equals("DeltaTime")) {
							deltaTime = Integer.parseInt(fields[1].substring(6,8));
						}
		                //if(fields[0].equals("HomeAltitude")) tourData.set // 71
		                if(fields[0].equals("HomeAltitude")) {
							lastAlti = Integer.parseInt(fields[1]);
						}
		                //if(fields[0].equals("Weight")) tourData.set // already set above
		                if(fields[0].equals("Distance1") || fields[0].equals("Distance2")
		                			|| fields[0].equals("HeightUp1")
		                			|| fields[0].equals("HeightUp2")
		                			|| fields[0].equals("TotalTime1")
		                			|| fields[0].equals("TotalTime2")
		                			|| fields[0].equals("WheelPerimeter1")
		                			|| fields[0].equals("WheelPerimeter2")){
		                	deviceTotals(tourData.getDeviceName(), modeId, line, tourData);
		                }
		        		break;
		        	case SECTION_POLAREXTS:
		        	//	ActiveLimit=0
		        	//	MaxVO2=0
		        	//	StartDelay=0
		        		break;
		        	case SECTION_COACH:
		        		fields = line.split("=");
		                if(fields.length < 2) {
							break;
						}
		                continue;
		                //if(fields[0].equals("AverageHR"))
		        		//if(fields[0].equals("Flag")) tourData.set
		                //if(fields[0].equals("IntervalAverageHR")) tourData.set //0
			        	//if(fields[0].equals("IntervalTime")) tourData.set //0
		                //if(fields[0].equals("MaximumHR")) tourData.set //0
			        	//if(fields[0].equals("ResultHR")) tourData.set //0
			            //if(fields[0].equals("ResultTime")) tourData.set //0
			        	//if(fields[0].equals("TargetZone1")) tourData.set //0;0;0;0;0;0;0;0;0
			            //if(fields[0].equals("TargetZone2")) tourData.set //0;0;0;0;0;0;0;0;0
		        		//if(fields[0].equals("TargetZone3")) tourData.set //0;0;0;0;0;0;0;0;0
		        		//break;
		        	case SECTION_TOURDATA:
		        		// first line starts with the string #Pulse Cadence Temperature
		        		// each line containing a data record starts with !
		        		final TimeData timeData = parseRecord(line, deltaTime, lastAlti, lastDistance);
		        		final Float fAlti = timeData.absoluteAltitude;
		        		lastAlti = fAlti.intValue();
		        		final Float fDistance = timeData.absoluteDistance;
		        		lastDistance = fDistance.intValue();
		        		// workaround for a bug within HAC4Linux writing
		        		// pulse information for that device even though
		        		// no pulse is recorded.
		        		// the data written alsways form a sawtooth graph
		        		// 0-256-0-256....
		        		if(tourData.getDeviceName().equals("CM414AM") ) {
							timeData.pulse = 0;
						}
		        		// The first time slice seems to be set to the total
		        		// altitude (no previous altitude record present, no
		        		// difference available
		        		if(isFirstTimeSlice){
		        			isFirstTimeSlice = false;
		        			timeData.time = 0;
		        			tourData.setStartAltitude(fAlti.shortValue());
		        			timeData.altitude = fAlti.intValue();
		        		}
		        		timeDataList.add(timeData);
		        		break;
		        	case SECTION_MARKS:
		        		// 0Label	1Number	2Time relativ	3Time absolut
		        		//!Abzw._Rieseberg	157	00:52:17.64	17:16:17.00
		        		//!z	370	02:03:10.08	18:27:10.00
		        		// each line containing a data record starts with !
		        		fields = line.split("\t");
		                if(fields.length < 4) {
							break;
						}
		                final TourMarker tourMarker = new TourMarker(tourData, ChartLabel.MARKER_TYPE_DEVICE);
		                tourMarker.setVisualPosition(ChartLabel.VISUAL_HORIZONTAL_ABOVE_GRAPH_CENTERED);
		                final int markerIndex = Integer.parseInt(fields[1]);
		                tourMarker.setSerieIndex(markerIndex);
		                tourMarker.setLabel(fields[0].substring(1,fields[0].length()));
		                tourMarker.setDistance((int)timeDataList.get(markerIndex).absoluteDistance );
		                int timeRelative = Integer.parseInt(fields[2].substring(6,8));
		                timeRelative += Integer.parseInt(fields[2].substring(3,5)) * 60;
		                timeRelative += Integer.parseInt(fields[2].substring(0,2)) * 3600;
		                tourMarker.setTime(timeRelative);
		                tourData.getTourMarkers().add(tourMarker);
		        		break;
		            default:
		               	break;
		        }
			}
			tourData.setTourDescription(tourDescription.toString());
			tourData.importRawDataFile = importFileName;
			tourData.setTourImportFilePath(importFileName);

			/*
			 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			 * !!! this is not valid because the used person must be available in the database !!!
			 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			 */
//			tourData.setTourPerson(tourPerson);
			
			
			// 	after all data are added, the tour id can be created
			final Long tourId = tourData.createTourId(Integer.toString(Math.abs(tourData.getStartDistance())));
			// check if the tour is in the tour map
			if (tourDataMap.containsKey(tourId) == false) {

				// add new tour to the map
				tourDataMap.put(tourId, tourData);

				/*
				 * disable data series when no data are available
				 */
				final TimeData firstTimeData = timeDataList.get(0);
				if (tourData.getDeviceName().equals("CM414AM")) {
					
					firstTimeData.pulse = Integer.MIN_VALUE;
				}

				tourData.createTimeSeries(timeDataList, true);
				tourData.computeComputedValues();
				tourData.computeTourDrivingTime();

				tourData.setTourType(defaultTourType);

				tourData.setDeviceId(deviceId);
				tourData.setDeviceName(visibleName);

				tourData.setWeek(tourData.getStartYear(), tourData.getStartMonth(), tourData.getStartDay());
			}


		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fileHac4LinuxData != null) {
				try {
					fileHac4LinuxData.close();
				} catch (final IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		return true;

	}

	/* (non-Javadoc)
	 * Switching the machine to another state depending on the section of
	 * the file currently read.
	 */
	private void switchSection(final String line){
		if(line.equals("[FILE]")){m_section = Section.SECTION_FILE; return;}
	    if(line.equals("[INFORMATION]")){m_section = Section.SECTION_INFO; return;}
	    if(line.equals("[NOTES]")){m_section = Section.SECTION_NOTES; return;}
	    if(line.equals("[FRIENDS]")){m_section = Section.SECTION_FRIENDS; return;}
	    if(line.equals("[PERSON]")){m_section = Section.SECTION_PERSON; return;}
	    if(line.equals("[STATISTICS]")){m_section = Section.SECTION_STATISTICS; return;}
	    if(line.equals("[SETTINGS]")){m_section = Section.SECTION_SETTINGS; return;}
	    if(line.equals("[POLAR-EXTENSION]")){m_section = Section.SECTION_POLAREXTS; return;}
	    if(line.equals("[COACH-PARAMETER]")){m_section = Section.SECTION_COACH; return;}
	    if(line.equals("[TOUR-DATA]")){m_section = Section.SECTION_TOURDATA; return;}
	    if(line.equals("[MARKING-DATA]")){m_section = Section.SECTION_MARKS; return;}

	    m_section = Section.SECTION_NONE;
	}

	/*
	 * (non-Javadoc)
	 * @see net.tourbook.importdata.IRawDataReader#validateRawData(java.lang.String)
	 */
	@Override
	public boolean validateRawData(final String fileName) {
		boolean isValid = false;

		BufferedInputStream inStream = null;

		try {

			final byte[] buffer = new byte[19];

			final File dataFile = new File(fileName);
			inStream = new BufferedInputStream(new FileInputStream(dataFile));

			inStream.read(buffer);
			if (!"HAC4Linux-Tour-File".equalsIgnoreCase(new String(buffer, 0, 19))) { //$NON-NLS-1$
				return false;
			}

			isValid = true;

		} catch (final NumberFormatException nfe) {
			return false;
		} catch (final FileNotFoundException e) {
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (final IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		return isValid;
	}
}