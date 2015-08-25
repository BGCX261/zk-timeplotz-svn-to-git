package org.zkforge.timeplot.geometry;

/**
 * @author gwx
 * 
 */
public interface ValueGeometry {
	public static final String LEFT = "left";

	public static final String RIGHT = "right";

	public static final String TOP = "top";

	public static final String BOTTOM = "bottom";

	public static final String SHORT = "short";
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
	 * <p>Default: left.
	 */
	public String getAxisLabelsPlacement();
	/**
	 * Sets the position of the axis ( left or right ).
	 * <p>Default: left.
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
	 * Returns the short size of the grid.
	 * <p>Default: 10.
	 */
	public int getGridShortSize();
	/**
	 * Sets the short size of the grid.
	 * <p>Default: 10.
	 * @param gridShortSize
	 */
	public void setGridShortSize(int gridShortSize);
	/**
	 * Returns the spacing width of the grid.
	 * <p>Default: 50.
	 */
	public int getGridSpacing();
	/**
	 * Sets the spacing width of the grid.
	 * <p>Default: 50.
	 * @param gridSpacing
	 */
	public void setGridSpacing(int gridSpacing);
	/**
	 * Returns the type of the grid.
	 * <p>Default: short.
	 */
	public String getGridType();
	/**
	 * Sets the type of the grid ( long or short ).
	 * <p>Default: short.
	 * @param gridType
	 */
	public void setGridType(String gridType);
	/**
	 * Returns the max of the axis label
	 * <p>Default: 0.
	 */
	public int getMax();
	/**
	 * Sets the max of the axis label
	 * <p>Default: 0.
	 * @param max
	 */
	public void setMax(int max);
	/**
	 * Returns the min of the axis label.
	 * <p>Default: 0.
	 */
	public int getMin();
	/**
	 * Sets the min of the axis label
	 * <p>Default: 0.
	 * @param min
	 */
	public void setMin(int min);
	/**
	 * Returns the id of the ValueGeometry
	 */
	public String getValueGeometryId();
}
