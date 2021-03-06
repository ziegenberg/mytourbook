/*******************************************************************************
 * Copyright (C) 2005, 2012  Wolfgang Schramm and Contributors
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
/*
 * Author: Wolfgang Schramm Created: 20.10.2012
 */
package net.tourbook.data;

import java.sql.Date;

/**
 * Contains data for one history time slice.
 */
public class HistoryData {

	/**
	 * Contains the time value from {@link Date#getTime()} or {@link Long#MIN_VALUE} when the time
	 * is not set.
	 */
	public long	absoluteTime	= Long.MIN_VALUE;

	@Override
	public String toString() {
		return "HistoryData [absoluteTime=" + absoluteTime + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
