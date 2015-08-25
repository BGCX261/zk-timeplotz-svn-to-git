/* OverPlotEvent.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 1 10:16:58 TST 2010, Created by JimmyShiau

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.zkforge.timeplot.event;

import java.util.Date;
import java.util.Map;
import org.zkforge.timeplot.Plotinfo;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;

/**
 * @author Jimmy Shiau
 * 
 *         2010
 */
public class OverPlotEvent extends Event {
	private Date _time;
	private Float _value;
	private Map _customValues;
	private int _plotDataIndex;
	private String _format;

	/**
	 * The onOverPlotData event
	 */
	public static final String ON_OVER_PLOTDATA = "onOverPlotData";

	/** Converts an AU request to a over plot event.
	 */
	public static OverPlotEvent getOverPlotEvent(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, request);
		final Map data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});
		final String name = request.getCommand();
		Date time = new Date(Long.parseLong(String.valueOf(data.remove("time"))));
		
		String valueStr = String.valueOf(data.remove("value"));
		Float value = null;
		if (!valueStr.equals("null"))
			value = new Float(valueStr);
		int plotDataIndex = Integer.parseInt(String.valueOf(data.remove("plotDataIndex")));
		String format = String.valueOf(data.remove("format"));
		return new OverPlotEvent(name, comp, time, value, data, plotDataIndex, format);
	}
	
	/**
	 * Constructs a plot data over event.
	 */
	public OverPlotEvent(String name, Component target, Date time, Float value,
			Map customValues, int plotDataIndex, String format) {
		super(name, target);
		_time = time;
		_value = value;
		_customValues = customValues;
		_plotDataIndex = plotDataIndex;
		if (!"null".equals(format)) 
			_format = format;
	}
	/**
	 * Returns the date of the plot data.
	 * <p>Default: current time.
	 */
	public Date getTime() {
		return _time;
	}
	/**
	 * Returns the value of the plot data.
	 * <p>Default: 0.0.
	 */
	public Float getValue() {
		return _value;
	}
	/**
	 * Returns addition informations for plot data.
	 */
	public Map getCustomValues() {
		return _customValues;
	}
	/**
	 * Returns the index of the plot data in the {@link Plotinfo}.<br/>
	 * {@link Plotinfo#getDataModel} will be null if data from text file,
	 * so you can't using this index to get plot data from {@link Plotinfo#getDataModel}
	 * 
	 */
	public int getPlotDataIndex() {
		return _plotDataIndex;
	}
	
	/**
	 * Returns the date format.
	 */
	public String getFormat() {
		return _format;
	}
}
