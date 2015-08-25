package org.zkforge.timeplot.geometry;

import java.util.Date;
import java.util.Map;

public interface TimeGeometry {
	
	public static final String FORMAT_SECOND = "org.zkforge.timeplot.geometry.FORMAT_SECOND";
	public static final String FORMAT_QUATERMINUTE = "org.zkforge.timeplot.geometry.FORMAT_QUATERMINUTE";
	public static final String FORMAT_MINUTE = "org.zkforge.timeplot.geometry.FORMAT_MINUTE";
	public static final String FORMAT_QUATERHOUR = "org.zkforge.timeplot.geometry.FORMAT_QUATERHOUR";
	public static final String FORMAT_HOUR = "org.zkforge.timeplot.geometry.FORMAT_HOUR";
	public static final String FORMAT_DAY = "org.zkforge.timeplot.geometry.FORMAT_DAY";
	public static final String FORMAT_YEAR = "org.zkforge.timeplot.geometry.FORMAT_YEAR";
	
	/**
	 * Returns the color of the axis.
	 * <p>Default: #606060.
	 */
	public String getAxisColor();
	/**
	 * Sets the color of the axis.
	 * <p>Default: #606060.
	 * @param axisColor
	 */
	public void setAxisColor(String axisColor);
	/**
	 * Returns the position of the axis.
	 * <p>Default: bottom.
	 */
	public String getAxisLabelsPlacement();
	/**
	 * Sets the position of the axis ( top or bottom ).
	 * <p>Default: bottom.
	 * @param axisLabelsPlacement
	 */
	public void setAxisLabelsPlacement(String axisLabelsPlacement);
	/**
	 * Returns the color of the grid.
	 * <p>Default: #000000.
	 */
	public String getGridColor();
	/**
	 * Sets the color of the grid.
	 * <p>Default: #000000.
	 * @param gridColor
	 */
	public void setGridColor(String gridColor);
	/**
	 * Returns the line width of the grid.
	 * <p>Default: 0.5.
	 */
	public float getGridLineWidth();
	/**
	 * Sets the line width of the grid.
	 * <p>Default: 0.5.
	 * @param gridLineWidth
	 */
	public void setGridLineWidth(float gridLineWidth);
	/**
	 * Returns the step width of the grid.
	 * <p>Default: 100.
	 */
	public int getGridStep();
	/**
	 * Sets the step of the grid.
	 * <p>Default: 100.
	 * @param gridStep
	 */
	public void setGridStep(int gridStep);
	/**
	 * Returns the step range of the grid.
	 * <p>Default: 20.
	 */
	public int getGridStepRange();
	/**
	 * Sets the step range of the grid.
	 * <p>Default: 20.
	 * @param gridStepRange
	 */
	public void setGridStepRange(int gridStepRange);
	/**
	 * Returns the latest time of the grid.
	 */
	public Date getMax();
	/**
	 * Sets the latest time of the grid.
	 * @param max
	 */
	public void setMax(Date max);
	/**
	 * Returns the earliest time of the grid.
	 */
	public Date getMin();
	/**
	 * Sets the earliest time of the grid.
	 * @param min
	 */
	public void setMin(Date min);
	/**
	 * Returns the position of the time flag.
	 * <p>Default: bottom.
	 */
	public String getTimeValuePosition();
	/**
	 * Sets the position of the time flag ( top or bottom ).
	 * @param timeValuePosition
	 */
	public void setTimeValuePosition(String timeValuePosition);
	/**
	 * 
	 * Returns the id of the TimeGeometry
	 */
	public String getTimeGeometryId();	
	
	/** Returns all formats associated with this TimeGeometry.
	 * @since 1.1_50
	 */
	public Map getFormats();
	/** Returns the custom format associated with this TimeGeometry.
	 * @since 1.1_50
	 */
	public String getFormat(String timeRange);
	/** Returns if the custom format is associate with this TimeGeometry.
	 * <p>Notice that <code>null</code> is a valid value, so you can
	 * tell if an attribute is associated by examining the return value
	 * of {@link #getFormat}.
	 * @since 1.1_50
	 */
	public boolean hasFormat(String timeRange);
	/** Sets the custom format associated with this TimeGeometry.
	 */
	public String setFormat(String timeRange, String format);
	/** Removes the custom format associated with this TimeGeometry.
	 */
	public String removeFormat(String timeRange);
	/** Sets whether display millisecond
	 */
	public void setDisplayMilli (boolean displayMilli);
	public String getDisplayMilli ();

	/**
	 * Sets the measure density when unit is millisecond
	 * @param measureDensityOfMillisecond
	 */
	public void setMeasureDensityOfMillisecond (int measureDensityOfMillisecond);
	public int getMeasureDensityOfMillisecond ();
}
