/*******************************************************************************
 * Copyright (C) 2005, 2007  Wolfgang Schramm and Contributors
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
package net.tourbook.application;

import net.tourbook.plugin.TourbookPlugin;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.splash.BasicSplashHandler;

/**
 * This was a copy of EclipseSplashHandler Parses the well known product constants and constructs a
 * splash handler accordingly.
 */
public class MyTourbookSplashHandler extends BasicSplashHandler {

	private static final String	APP_BUILD_ID	= "Version 1.1.0.dev"; //$NON-NLS-1$

	@Override
	public void init(Shell splash) {

		super.init(splash);

		// keep the splash handler to be used outside of this splash handlers
		TourbookPlugin.getDefault().setSplashHandler(this);

		String progressRectString = null;
		String messageRectString = null;
		String foregroundColorString = null;

		IProduct product = Platform.getProduct();
		if (product != null) {
			progressRectString = product.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
			messageRectString = product.getProperty(IProductConstants.STARTUP_MESSAGE_RECT);
			foregroundColorString = product.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);
		}

		// set progressbar position
		Rectangle progressRect = parseRect(progressRectString);
		if (progressRect == null) {
			progressRect = new Rectangle(10, 0, 300, 15);
		}
		setProgressRect(progressRect);

		// set message position
		Rectangle messageRect = parseRect(messageRectString);
		if (messageRect == null) {
			messageRect = new Rectangle(10, 25, 300, 15);
		}
		setMessageRect(messageRect);

		// set message color
		int foregroundColorInteger;
		try {
			foregroundColorInteger = Integer.parseInt(foregroundColorString, 16);
		} catch (Exception ex) {
			foregroundColorInteger = 0xD2D7FF; // off white
		}

		setForeground(new RGB((foregroundColorInteger & 0xFF0000) >> 16,
				(foregroundColorInteger & 0xFF00) >> 8,
				foregroundColorInteger & 0xFF));

//		final String buildId = "Version " + System.getProperty("eclipse.buildId", "Unknown Version"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		getContent().addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				Point extend = gc.textExtent(APP_BUILD_ID);

				gc.setForeground(getForeground());
				gc.drawText(APP_BUILD_ID, 383 - extend.x, 57, true);
			}
		});
	}

	private Rectangle parseRect(String string) {
		if (string == null) {
			return null;
		}
		int x, y, w, h;
		int lastPos = 0;
		try {
			int i = string.indexOf(',', lastPos);
			x = Integer.parseInt(string.substring(lastPos, i));
			lastPos = i + 1;
			i = string.indexOf(',', lastPos);
			y = Integer.parseInt(string.substring(lastPos, i));
			lastPos = i + 1;
			i = string.indexOf(',', lastPos);
			w = Integer.parseInt(string.substring(lastPos, i));
			lastPos = i + 1;
			h = Integer.parseInt(string.substring(lastPos));
		} catch (RuntimeException e) {
			// sloppy error handling
			return null;
		}
		return new Rectangle(x, y, w, h);
	}
}
