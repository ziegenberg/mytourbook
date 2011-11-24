/*******************************************************************************
 * Copyright (C) 2005, 2011  Wolfgang Schramm and Contributors
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
package net.tourbook.chart;

import java.util.ArrayList;

/**
 * Contains the highValues and display attributes for one data serie
 */
public class ChartDataYSerie extends ChartDataSerie {

	/**
	 * the bars has only a low and high value
	 */
	public static final int			BAR_LAYOUT_SINGLE_SERIE		= 1;

	/**
	 * the bars are displayed one of the other
	 */
	public static final int			BAR_LAYOUT_STACKED			= 2;

	/**
	 * the bars are displayed beside each other
	 */
	public static final int			BAR_LAYOUT_BESIDE			= 3;

	public static final String		YDATA_INFO					= "yDataInfo";					//$NON-NLS-1$

	public static final int			FILL_METHOD_FILL_BOTTOM		= 1;
	public static final int			FILL_METHOD_FILL_ZERO		= 2;
	public static final int			FILL_METHOD_CUSTOM			= 100;

	/**
	 * Slider label format: n.1
	 */
	public static final int			SLIDER_LABEL_FORMAT_DEFAULT	= 0;
	/**
	 * Slider label format: mm:ss
	 */
	public static final int			SLIDER_LABEL_FORMAT_MM_SS	= 1;

	private int						_sliderLabelFormat			= SLIDER_LABEL_FORMAT_DEFAULT;
	private int						_chartLayout				= BAR_LAYOUT_SINGLE_SERIE;
	private String					_yTitle;

	/**
	 * contains the color index for each value
	 */
	private int[][]					_colorIndex;

	private int						_graphFillMethod			= FILL_METHOD_FILL_BOTTOM;

	private boolean					_isShowYSlider				= false;

	/**
	 * This value is set when a y-slider is dragged
	 */
	float							adjustedYValue				= Float.MIN_VALUE;

	/**
	 * <p>
	 * true: the direction is from bottom to top by increasing number <br>
	 * false: the direction is from top to bottom by increasing number
	 */
	private boolean					_yAxisDirection				= true;

	/**
	 * Contains all layers which are drawn on top of the graph before the slider
	 */
	private ArrayList<IChartLayer>	_customFgLayers				= new ArrayList<IChartLayer>();

	/**
	 * Contains all layers which are drawn in the background of the graph
	 */
	private ArrayList<IChartLayer>	_customBgLayers				= new ArrayList<IChartLayer>();

	/**
	 * Contains a painter which fills the graph
	 */
	private IFillPainter			_customFillPainter;

	private ChartYSlider			_ySlider1;

	private ChartYSlider			_ySlider2;

	private final int				_chartType;

	/**
	 * When this value is > 0 a line chart will not draw a line to the next value point when the
	 * difference in the x-data values is greater than this value.
	 * <p>
	 * Lines are not drawn to the next value with this feature because these lines (and filling)
	 * looks ugly (a triangle is painted) when a tour is paused.
	 */
//	private int						_disabledLineToNext;

	public ChartDataYSerie(final int chartType, final float[] valueSerie) {
		_chartType = chartType;
		setMinMaxValues(new float[][] { valueSerie });
	}

	public ChartDataYSerie(final int chartType, final float[] lowValueSerie, final float[] highValueSerie) {
		_chartType = chartType;
		setMinMaxValues(new float[][] { lowValueSerie }, new float[][] { highValueSerie });
	}

	public ChartDataYSerie(final int chartType, final float[][] valueSeries) {
		_chartType = chartType;
		setMinMaxValues(valueSeries);
	}

	public ChartDataYSerie(final int chartType, final float[][] lowValueSeries, final float[][] highValueSeries) {
		_chartType = chartType;
		setMinMaxValues(lowValueSeries, highValueSeries);
	}

	public ChartDataYSerie(	final int chartType,
							final int chartLayout,
							final float[][] lowValueSeries,
							final float[][] highValueSeries) {

		_chartType = chartType;
		_chartLayout = chartLayout;

		setMinMaxValues(lowValueSeries, highValueSeries);
	}

	/**
	 * @return Returns the chartLayout.
	 */
	protected int getChartLayout() {
		return _chartLayout;
	}

	public int getChartType() {
		return _chartType;
	}

	/**
	 * @return Returns the valueColors.
	 */
	public int[][] getColorsIndex() {
		if (_colorIndex == null || _colorIndex.length == 0 || _colorIndex[0] == null || _colorIndex[0].length == 0) {
			setAllValueColors(0);
		}
		return _colorIndex;
	}

	public ArrayList<IChartLayer> getCustomBackgroundLayers() {
		return _customBgLayers;
	}

	public IFillPainter getCustomFillPainter() {
		return _customFillPainter;
	}

	public ArrayList<IChartLayer> getCustomForegroundLayers() {
		return _customFgLayers;
	}

	/**
	 * @return returns true if the graph is filled
	 */
	public int getGraphFillMethod() {
		return _graphFillMethod;
	}

//	public int getDisabledLineToNext() {
//		return _disabledLineToNext;
//	}

	/**
	 * @return Returns the format how the slider label will be formatted, which can be <br>
	 *         {@link #SLIDER_LABEL_FORMAT_DEFAULT}<br>
	 *         {@link #SLIDER_LABEL_FORMAT_MM_SS}
	 */
	public int getSliderLabelFormat() {
		return _sliderLabelFormat;
	}

	public String getXTitle() {
		return null;
	}

	/**
	 * @return Returns the ySliderTop.
	 */
	public ChartYSlider getYSlider1() {
		return _ySlider1;
	}

	/**
	 * @return Returns the ySliderBottom.
	 */
	public ChartYSlider getYSlider2() {
		return _ySlider2;
	}

	/**
	 * @return Returns the title.
	 */
	public String getYTitle() {
		return _yTitle;
	}

	/**
	 * @return Returns the showYSlider.
	 */
	public boolean isShowYSlider() {
		return _isShowYSlider;
	}

	/**
	 * <p>
	 * true: the direction is from bottom to top by increasing number <br>
	 * false: the direction is from top to bottom by increasing number
	 */
	public boolean isYAxisDirection() {
		return _yAxisDirection;
	}

	/**
	 * set the color index of all values
	 * 
	 * @param colorIndexValue
	 */
	public void setAllValueColors(final int colorIndexValue) {

		if (_highValues == null || _highValues.length == 0 || _highValues[0] == null || _highValues[0].length == 0) {
			return;
		}

		_colorIndex = new int[1][_highValues[0].length];

		final int[] colorIndex0 = _colorIndex[0];

		for (int colorIndex = 0; colorIndex < colorIndex0.length; colorIndex++) {
			colorIndex0[colorIndex] = colorIndexValue;
		}
	}

	/**
	 * @param colorIndex
	 *            set's the color index for each value
	 */
	public void setColorIndex(final int[][] colorIndex) {
		_colorIndex = colorIndex;
	}

	public void setCustomBackgroundLayers(final ArrayList<IChartLayer> customBackgroundLayers) {
		_customBgLayers = customBackgroundLayers;
	}

	public void setCustomFillPainter(final IFillPainter fillPainter) {
		_customFillPainter = fillPainter;
	}

	public void setCustomForegroundLayers(final ArrayList<IChartLayer> customLayers) {
		_customFgLayers = customLayers;
	}

	/**
	 * @param fillMethod
	 *            when set to <tt>true</tt> graph is filled, default is <tt>false</tt>
	 */
	public void setGraphFillMethod(final int fillMethod) {
		_graphFillMethod = fillMethod;
	}

	@Override
	void setMinMaxValues(final float[][] valueSeries) {

		if (valueSeries == null || valueSeries.length == 0 || valueSeries[0] == null || valueSeries[0].length == 0) {
			_highValues = new float[0][0];
			_visibleMaxValue = _visibleMinValue = 0;
			_originalMaxValue = _originalMinValue = 0;

		} else {

			_highValues = valueSeries;

			// set initial min/max value
			_visibleMaxValue = _visibleMinValue = valueSeries[0][0];

			if (_chartType == ChartDataModel.CHART_TYPE_LINE
					|| _chartType == ChartDataModel.CHART_TYPE_LINE_WITH_BARS
					|| _chartType == ChartDataModel.CHART_TYPE_XY_SCATTER) {

				super.setMinMaxValues(valueSeries);

			} else if (_chartType == ChartDataModel.CHART_TYPE_BAR) {

				switch (_chartLayout) {
				case ChartDataYSerie.BAR_LAYOUT_SINGLE_SERIE:
				case ChartDataYSerie.BAR_LAYOUT_BESIDE:

					// get the min/max highValues for all data
					for (final float[] valuesOuter : valueSeries) {
						for (final float valuesInner : valuesOuter) {
							_visibleMaxValue = (_visibleMaxValue >= valuesInner) ? _visibleMaxValue : valuesInner;
							_visibleMinValue = (_visibleMinValue <= valuesInner) ? _visibleMinValue : valuesInner;
						}
					}
					break;

				case ChartDataYSerie.BAR_LAYOUT_STACKED:

					final float serieMax[] = new float[valueSeries[0].length];

					// get the max value for the data which are stacked on each
					// other
					for (final float[] valuesOuter : valueSeries) {
						for (int valueIndex = 0; valueIndex < valuesOuter.length; valueIndex++) {

							final float outerValue = valuesOuter[valueIndex];
							final float outerValueWithMax = serieMax[valueIndex] + outerValue;

							serieMax[valueIndex] = (_visibleMaxValue >= outerValueWithMax)
									? _visibleMaxValue
									: outerValueWithMax;

							_visibleMinValue = (_visibleMinValue <= outerValue) ? _visibleMinValue : outerValue;
						}
					}

					// get max for all series
					_visibleMaxValue = 0;
					for (final float serieValue : serieMax) {
						_visibleMaxValue = (_visibleMaxValue >= serieValue) ? _visibleMaxValue : serieValue;
					}

					break;
				}
			}

			_originalMinValue = _visibleMinValue;
			_originalMaxValue = _visibleMaxValue;
		}
	}

	@Override
	void setMinMaxValues(final float[][] lowValues, final float[][] highValues) {

		if (lowValues == null || lowValues.length == 0 || lowValues[0] == null || lowValues[0].length == 0

		|| highValues == null || highValues.length == 0 || highValues[0] == null || highValues[0].length == 0) {

			_visibleMaxValue = _visibleMinValue = 0;
			_originalMaxValue = _originalMinValue = 0;

			_lowValues = new float[1][2];
			_highValues = new float[1][2];

		} else {

			_lowValues = lowValues;
			_highValues = highValues;
			_colorIndex = new int[_highValues.length][_highValues[0].length];

			// set initial min/max value
			_visibleMinValue = lowValues[0][0];
			_visibleMaxValue = highValues[0][0];

			if (_chartType == ChartDataModel.CHART_TYPE_LINE
					|| (_chartType == ChartDataModel.CHART_TYPE_BAR && _chartLayout == ChartDataYSerie.BAR_LAYOUT_SINGLE_SERIE)
					|| (_chartType == ChartDataModel.CHART_TYPE_BAR && _chartLayout == ChartDataYSerie.BAR_LAYOUT_BESIDE)) {

				// get the min/max values for all data
				for (final float[] valueSerie : highValues) {
					for (final float value : valueSerie) {
						_visibleMaxValue = (_visibleMaxValue >= value) ? _visibleMaxValue : value;
					}
				}

				for (final float[] valueSerie : lowValues) {
					for (final float value : valueSerie) {
						_visibleMinValue = (_visibleMinValue <= value) ? _visibleMinValue : value;
					}
				}

			} else if (_chartType == ChartDataModel.CHART_TYPE_BAR
					&& _chartLayout == ChartDataYSerie.BAR_LAYOUT_STACKED) {

				/*
				 * calculate the max value
				 */

				// summarize the data
				final float[] summarizedMaxValues = new float[highValues[0].length];
				for (final float[] valueSerie : highValues) {
					for (int valueIndex = 0; valueIndex < valueSerie.length; valueIndex++) {
						summarizedMaxValues[valueIndex] += valueSerie[valueIndex];
					}
				}

				// get max value for the summarized values
				for (final float value : summarizedMaxValues) {
					_visibleMaxValue = (_visibleMaxValue >= value) ? _visibleMaxValue : value;
				}

				/*
				 * calculate the min value
				 */
				for (final float[] serieData : lowValues) {
					for (final float value : serieData) {
						_visibleMinValue = (_visibleMinValue <= value) ? _visibleMinValue : value;
					}
				}
			}

			_originalMinValue = _visibleMinValue;
			_originalMaxValue = _visibleMaxValue;
		}
	}

	/**
	 * show the y-sliders for the chart
	 * 
	 * @param showYSlider
	 *            The showYSlider to set.
	 */
	public void setShowYSlider(final boolean showYSlider) {

		_isShowYSlider = showYSlider;

		_ySlider1 = new ChartYSlider(this);
		_ySlider2 = new ChartYSlider(this);
	}

	/**
	 * @param sliderLabelFormat
	 */
	public void setSliderLabelFormat(final int sliderLabelFormat) {
		_sliderLabelFormat = sliderLabelFormat;
	}

	/**
	 * set the direction for the y axis <code>
	 * true: the direction is from bottom to top by increasing number
	 * false: the direction is from top to bottom by increasing number
	 * </code>
	 * 
	 * @param axisDirection
	 */
	public void setYAxisDirection(final boolean axisDirection) {
		_yAxisDirection = axisDirection;
	}

	/**
	 * @param title
	 *            set the title for the y-axis
	 */
	public void setYTitle(final String title) {
		_yTitle = title;
	}

	@Override
	public String toString() {
		return "[ChartDataYSerie]";//$NON-NLS-1$
	}
}
