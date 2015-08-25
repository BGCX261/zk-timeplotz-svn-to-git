package org.zkforge.timeplot.data;

public class PlotDataSource {
	private String dataSourceUri;

	private String separator = ",";

	private int dataSourceColumn = 1;
	/**
	 * Returns which column will be loaded.
	 * <p>Default: 1.
	 */
	public int getDataSourceColumn() {
		return dataSourceColumn;
	}
	/**
	 * Sets which column will be loaded.
	 * <p>Default: 1.
	 * @param dataSourceColumn
	 */
	public void setDataSourceColumn(int dataSourceColumn) {
		this.dataSourceColumn = dataSourceColumn;
	}
	/**
	 * Returns the URL of the data file.
	 */
	public String getDataSourceUri() {
		return dataSourceUri;
	}
	/**
	 * Sets the URL of the data file.
	 * @param dataSourceUri
	 */
	public void setDataSourceUri(String dataSourceUri) {
		this.dataSourceUri = dataSourceUri;
	}
	/**
	 * Returns the separator between the columns in data file.
	 * <p>Default: ,(comma).
	 */
	public String getSeparator() {
		return separator;
	}
	/**
	 * Sets the separator between the columns in data file.
	 * <p>Default: ,(comma).
	 * @param separator
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}
}
