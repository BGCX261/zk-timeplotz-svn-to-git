package org.zkforge.timeplot.geometry;
/**
 * A Default value geometry is what regulates mapping of the plot values to the screen y coordinate. 
 * If two plots share the same value geometry, they will be drawn using the same scale. 
 * If "min" and "max" parameters are not set, the geometry will stretch itself automatically 
 * so that the entire plot will be drawn without overflowing. The stretching happens also 
 * when a geometry is shared between multiple plots, the one with the biggest range 
 * will win over the others.
 * @author Jimmy Shiau
 *
 */
public class DefaultValueGeometry implements ValueGeometry {

	public String toString() {
		// TODO Auto-generated method stub
		return "DefaultValueGeometry";
	}

	private static int count = 0;

	private int valueGeometryId = count++;

	private String axisColor = "#606060";

	private String gridColor = "#000000";

	private float gridLineWidth = (float) 0.5;

	private String axisLabelsPlacement = "left";

	private int gridSpacing = 50;

	private String gridType = "short";

	private int gridShortSize = 10;

	private int min = 0;

	private int max = 0;

	public String getAxisColor() {
		return axisColor;
	}

	public void setAxisColor(String axisColor) {
		this.axisColor = axisColor;
	}

	public String getAxisLabelsPlacement() {
		return axisLabelsPlacement;
	}

	public void setAxisLabelsPlacement(String axisLabelsPlacement) {
		this.axisLabelsPlacement = axisLabelsPlacement;
	}

	public String getGridColor() {
		return gridColor;
	}

	public void setGridColor(String gridColor) {
		this.gridColor = gridColor;
	}

	public float getGridLineWidth() {
		return gridLineWidth;
	}

	public void setGridLineWidth(float gridLineWidth) {
		this.gridLineWidth = gridLineWidth;
	}

	public int getGridShortSize() {
		return gridShortSize;
	}

	public void setGridShortSize(int gridShortSize) {
		this.gridShortSize = gridShortSize;
	}

	public int getGridSpacing() {
		return gridSpacing;
	}

	public void setGridSpacing(int gridSpacing) {
		this.gridSpacing = gridSpacing;
	}

	public String getGridType() {
		return gridType;
	}

	public void setGridType(String gridType) {
		this.gridType = gridType;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public String getValueGeometryId() {
		return "valueGeometry" + valueGeometryId;
	}

}
