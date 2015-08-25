package org.zkforge.timeplot.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.zkforge.json.simple.JSONObject;

public class PlotData {

	private static int count = 0;

	private int _id = count++;

	private Date _time = new Date();

	private Float _value = new Float(0);
	
	private String _format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	
	private Map _customValues;
	
	private boolean isCustomFormat = false;
	
	public String toString() {
		JSONObject json = new JSONObject();
		json.put("id",String.valueOf(_id));
		json.put("time", getDefaultFormat().format(_time));
		json.put("value",String.valueOf( _value));
		if(isCustomFormat)
			json.put("format", _format);
		if (_customValues != null)
			json.put("customValues",enclossMapToJason(_customValues));
		return json.toString();
	}
	
	/**
	 * Sets the date format.
	 * @since 1.0_3.1
	 */
	public void setFormat(String format) {
		if (format == null || format.length() == 0)
			format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		_format = format;
		isCustomFormat = true;
	}
	
	/**
	 * Returns the date format.
	 * <p>Default: "yyyy-MM-dd'T'HH:mm:ss"
	 * @since 1.0_3.1
	 */
	public String getFormat() {
		return _format;
	}
	
	/**
	 * Returns the default {@link DateFormat} according to {@link #getFormat()}.
	 * with US locale.
	 * @since 1.0_3.1
	 */
	protected DateFormat getDefaultFormat() {
		return new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
	}
	/**
	 * Returns the id of the PlotData.
	 */
	public int getId() {
		return _id;
	}
	/**
	 * Sets the id of the PlotData.
	 * @param id
	 */
	public void setId(int id) {
		this._id = id;
	}
	/**
	 * Returns the date of the plot data.
	 * <p>Default: current time.
	 */
	public Date getTime() {
		return _time;
	}
	/**
	 * Sets the date of the plot data.
	 * <p>Default: current time.
	 * @param time
	 */
	public void setTime(Date time) {

		this._time = time;
	}
	/**
	 * Returns the value of the plot data.
	 * <p>Default: 0.0.
	 */
	public Float getValue() {
		return _value;
	}
	/**
	 * Sets the value of the plot data.
	 * <p>Default: 0.0.
	 * @param value Float
	 */
	public void setValue(Float value) {
		this._value = value;
	}
	/**
	 * Sets the value of the plot data.
	 * <p>Default: 0.0.
	 * @param value float
	 */
	public void setValue(float value) {
		setValue(Float.valueOf(value));
	}
	/**
	 * Sets the value of the plot data.
	 * <p>Default: 0.0.
	 * @param value double
	 */
	public void setValue(double value) {
		setValue(Double.valueOf(value).floatValue());
	}
	/**
	 * Sets addition informations for plot data.
	 * @param _customValues
	 * @since 1.1_50
	 */
	public void setCustomValues(Map _customValues) {
		this._customValues = _customValues;
	}
	/**
	 * Returns addition informations for plot data.
	 */
	public Map getCustomValues() {
		return _customValues;
	}
	
	private String enclossMapToJason(Map map) {
		JSONObject json = new JSONObject();
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry me = (Map.Entry) it.next();
			json.put(me.getKey(), me.getValue());
		}
		return json.toString();
	}

}
