/* Plotinfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  5 12:32:58 TST 2009, Created by Jimmy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkforge.timeplot;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.zkforge.timeline.data.*;
import org.zkforge.timeplot.data.PlotData;
import org.zkforge.timeplot.data.PlotDataSource;
import org.zkforge.timeplot.event.OverPlotEvent;
import org.zkforge.timeplot.geometry.*;
import org.zkforge.timeplot.operator.*;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.lang.Objects;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import org.zkoss.zul.event.*;
/**
 * A plot layer is the main building block for timeplots and it's the object 
 * that is responsible for painting the plot itself. Each plot needs to have 
 * a time geometry, either a DataSource (for time series plots) or 
 * an EventSource (for event plots) and a value geometry in case of time series plots.
 * @author Jimmy Shiau
 */
public class Plotinfo extends HtmlBasedComponent {

	{
		addClientEvent(Plotinfo.class, OverPlotEvent.ON_OVER_PLOTDATA, 0);
	}
	
	private ListModel _dataModel;

	private LinkedList _dataList = new LinkedList();

	private ListModel _eventModel;

	private LinkedList _eventList = new LinkedList();

	private transient ListDataListener _dataListener;

	private transient ListDataListener _eventListener;

	private String _fillColor;
	
	private String _dotColor;

	private String _lineColor;

	private float _lineWidth = (float) 1.0;

	private float _eventLineWidth = (float) 1.0;

	private float _dotRadius = (float) 2.0;

	private boolean _showValues = false;
	
	private boolean _hideValueFlag = false;
	
	private boolean _roundValues = false;

	private int _valuesOpacity = 75;

	private int _bubbleWidth = 300;

	private int _bubbleHeight = 200;
	
	private PlotDataSource _pds = null;

	private Operator _operator = null;

	private String _eventSourceUri = null;

	private ValueGeometry _valueGeometry = null;

	private TimeGeometry _timeGeometry = null;

	private List _addEvtList, _mdyEvtList, _rmEvtList, _addDataList, _mdyDataList, _rmDataList;

	private static final String ATTR_ON_ADD_EVENT_RESPONSE =
		"org.zkforge.timeplot.onAddEventResponse";
	private static final String ATTR_ON_REMOVE_EVENT_RESPONSE =
		"org.zkforge.timeplot.onRemoveEventResponse";
	private static final String ATTR_ON_MODIFY_EVENT_RESPONSE =
		"org.zkforge.timeplot.onModifyEventResponse";
	private static final String ATTR_ON_ADD_DATA_RESPONSE =
		"org.zkforge.timeplot.onAddDataResponse";
	private static final String ATTR_ON_REMOVE_DATA_RESPONSE =
		"org.zkforge.timeplot.onRemoveDataResponse";
	private static final String ATTR_ON_MODIFY_DATA_RESPONSE =
		"org.zkforge.timeplot.onModifyDataResponse";
	
	static {		
		addClientEvent(Plotinfo.class, "onEventClick", CE_REPEAT_IGNORE);
	}	
	
	/**
	 * Returns height of the bubble.
	 * <p>Default: 200.
	 */
	public int getBubbleHeight() {
		return _bubbleHeight;
	}

	/**
	 * Sets the height of the bubble.
	 * <p>Default: 200.
	 * @param bubbleHeight
	 */
	public void setBubbleHeight(int bubbleHeight) {
		if (this._bubbleHeight != bubbleHeight) {
			this._bubbleHeight = bubbleHeight;
			smartUpdate("bubbleHeight", this._bubbleHeight);
		}
	}

	/**
	 * Returns width of the bubble.
	 * <p>Default: 300.
	 */
	public int getBubbleWidth() {
		return _bubbleWidth;
	}

	/**
	 * sets width of the bubble.
	 * <p>Default: 300.
	 * @param bubbleWidth
	 */
	public void setBubbleWidth(int bubbleWidth) {
		if (this._bubbleWidth != bubbleWidth) {
			this._bubbleWidth = bubbleWidth;
			smartUpdate("bubbleWidth", this._bubbleWidth);
		}
	}
	
	/**
	 * Return PlotDataSource Object.
	 */
	public PlotDataSource getPlotDataSource() {
		return _pds;
	}

	/**
	 * sets PlotDataSource to plotinfo.
	 * @param pds
	 */
	public void setPlotDataSource(PlotDataSource pds) {
		if (!Objects.equals(this._pds, pds)) {
			this._pds = pds;
			smartUpdate("separator",this._pds.getSeparator());
			smartUpdate("dataSourceColumn",this._pds.getDataSourceColumn());
			smartUpdate("dataSourceUri", new EncodedURL(this._pds.getDataSourceUri()));
		}
	}

	/**
	 * Returns the color of the dot.
	 */
	public String getDotColor() {
		return _dotColor;
	}

	/**
	 * Sets the color of the dot.
	 * @param dotColor
	 */
	public void setDotColor(String dotColor) {
		if (!Objects.equals(this._dotColor, dotColor)) {
			this._dotColor = dotColor;
			smartUpdate("dotColor", this._dotColor);
		}
	}

	/**
	 * Returns the radius of the dot.
	 * <p>Default: 2.0.
	 */
	public float getDotRadius() {
		return _dotRadius;
	}

	/**
	 * Sets the radius of the dot.
	 * <p>Default: 2.0.
	 * @param dotRadius
	 */
	public void setDotRadius(float dotRadius) {
		if (this._dotRadius != dotRadius) {
			this._dotRadius = dotRadius;
			smartUpdate("dotRadius",String.valueOf(this._dotRadius));
		}
	}

	/**
	 * Returns the line width of the event bar.
	 * <p>Default: 1.0.
	 */
	public float getEventLineWidth() {
		return _eventLineWidth;
	}

	/**
	 * Sets the line width of the event bar
	 * <p>Default: 1.0.
	 * @param eventLineWidth
	 */
	public void setEventLineWidth(float eventLineWidth) {
		if (this._eventLineWidth != eventLineWidth) {
			this._eventLineWidth = eventLineWidth;
			smartUpdate("eventLineWidth",String.valueOf(this._eventLineWidth));
		}
	}

	/**
	 * Returns the URL of the event file.
	 */
	public String getEventSourceUri() {
		return _eventSourceUri;
	}

	/**
	 * Sets the URL of the event file.
	 * @param eventSourceUri
	 */
	public void setEventSourceUri(String eventSourceUri) {
		if (!Objects.equals(this._eventSourceUri, eventSourceUri)) {
			this._eventSourceUri = eventSourceUri;
			smartUpdate("eventSourceUri", new EncodedURL(this._eventSourceUri));			
		}
	}
	/**
	 * Invoke the repaint of the parent (Timeplot).
	 */
	public void repaint(){
		Component parent = getParent();
		if (parent != null)
			((Timeplot)getParent()).repaint();
	}

	/**
	 * Returns the color of the area under the line.
	 */
	public String getFillColor() {
		return _fillColor;
	}

	/**
	 * Sets the color of the area under the line.
	 * @param fillColor
	 */
	public void setFillColor(String fillColor) {
		if (!Objects.equals(this._fillColor, fillColor)) {
			this._fillColor = fillColor;
			smartUpdate("fillColor", this._fillColor);
		}
	}

	/**
	 * Returns the color of the line which link the dots.
	 */
	public String getLineColor() {
		return _lineColor;
	}

	/**
	 * Sets the color of the line which link the dots.
	 * @param lineColor
	 */
	public void setLineColor(String lineColor) {
		if (!Objects.equals(this._lineColor , lineColor)) {
			this._lineColor = lineColor;			
			smartUpdate("lineColor", this._lineColor);
		}
	}

	/**
	 * Returns the width of the line.
	 * <p>Default: 1.0.
	 */
	public float getLineWidth() {
		return _lineWidth;
	}

	/**
	 * Sets the width of the line.
	 * <p>Default: 1.0.
	 * @param lineWidth
	 */
	public void setLineWidth(float lineWidth) {
		if (this._lineWidth != lineWidth) {
			this._lineWidth = lineWidth;
			smartUpdate("lineWidth",String.valueOf(this._lineWidth));
		}
	}

	/**
	 * Returns whether is show the round values, 
	 * the timeplot can show floating point numbers if false.
	 * <p>Default: true.
	 */
	public boolean isRoundValues() {
		return _roundValues;
	}

	/**
	 * Sets whether s show the round values, 
	 * the timeplot can show floating point numbers if false.
	 * <p>Default: true.
	 * @param roundValues
	 */
	public void setRoundValues(boolean roundValues) {
		if (this._roundValues != roundValues) {
			this._roundValues = roundValues;
			smartUpdate("roundValues",this._roundValues);
		}
	}

	/**
	 * Returns whether is show the plot values on dot, 
	 * the timeplot shows plot value label when mouse over if true.
	 * <p>Default: false.
	 */
	public boolean isShowValues() {
		return _showValues;
	}

	/**
	 * Sets whether is show the plot values on dot, 
	 * the timeplot shows plot value label when mouse over if true.
	 * <p>Default: false.
	 * @param showValues
	 */
	public void setShowValues(boolean showValues) {
		if (this._showValues != showValues) {
			this._showValues = showValues;
			smartUpdate("showValues", this._showValues);
			repaint();
		}
	}
	
	/**
	 * Returns whether is hide the flag of plot values on dot, 
	 * the timeplot hides plot value flag when mouse over if true.
	 * <p>Default: false.
	 */
	public boolean isHideValueFlag() {
		return _hideValueFlag;
	}

	/**
	 * Sets whether is hide the flag of plot values on dot, 
	 * the timeplot hides plot value flag when mouse over if true.
	 * <p>Default: false.
	 * @param hideValueFlag
	 */
	public void setHideValueFlag(boolean hideValueFlag) {
		if (this._hideValueFlag != hideValueFlag) {
			this._hideValueFlag = hideValueFlag;
			smartUpdate("hideValueFlag", this._hideValueFlag);
			repaint();
		}
	}
	
	/**
	 * Returns the opacity of the plot value label.
	 * <p>Default: 75.
	 */
	public int getValuesOpacity() {
		return _valuesOpacity;
	}

	/**
	 * Sets the opacity of the plot value label.
	 * <p>Default: 75.
	 * @param valuesOpacity
	 */
	public void setValuesOpacity(int valuesOpacity) {
		if (this._valuesOpacity != valuesOpacity) {
			this._valuesOpacity = valuesOpacity;
			smartUpdate("valuesOpacity", this._valuesOpacity);
			repaint();
		}
	}

	private String getJSONResponse(List list) {
		final StringBuffer sb = new StringBuffer().append('[');
		for (Iterator it = list.iterator(); it.hasNext();) {
			sb.append(it.next()).append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(']');
		list.clear();
		return sb.toString();
	}
	public void onAddEventResponse() {
		removeAttribute(ATTR_ON_ADD_EVENT_RESPONSE);
		response("addPlotEvent" + getUuid(), new AuSetAttribute(this,"addPlotEvent",getJSONResponse(_addEvtList)));
	}
	public void onRemoveEventResponse() {
		removeAttribute(ATTR_ON_REMOVE_EVENT_RESPONSE);
		response("removePlotEvent" + getUuid(), new AuSetAttribute(this,"removePlotEvent",getJSONResponse(_rmEvtList)));
	}
	public void onModifyEventResponse() {
		removeAttribute(ATTR_ON_MODIFY_EVENT_RESPONSE);
		response("modifyPlotEvent" + getUuid(), new AuSetAttribute(this,"modifyPlotEvent",getJSONResponse(_mdyEvtList)));
	}
	public void onAddDataResponse() {
		removeAttribute(ATTR_ON_ADD_DATA_RESPONSE);
		response("addPlotData" + getUuid(), new AuSetAttribute(this,"addPlotData",getJSONResponse(_addDataList)));
	}
	public void onRemoveDataResponse() {
		removeAttribute(ATTR_ON_REMOVE_DATA_RESPONSE);		
		response("removePlotData" + getUuid(), new AuSetAttribute(this,"removePlotData",getJSONResponse(_rmDataList)));
	}
	public void onModifyDataResponse() {
		removeAttribute(ATTR_ON_MODIFY_DATA_RESPONSE);
		response("modifyPlotData" + getUuid(), new AuSetAttribute(this,"modifyPlotData",getJSONResponse(_mdyDataList)));
	}
	
	/**
	 * add an OccurEvent to the plotinfo
	 * @param oe
	 */
	public void addPlotEvent(OccurEvent oe) {
		if (_addEvtList == null)
			_addEvtList = new LinkedList();
		_addEvtList.add(oe);
		if (getAttribute(ATTR_ON_ADD_EVENT_RESPONSE) == null) {
			setAttribute(ATTR_ON_ADD_EVENT_RESPONSE, Boolean.TRUE);
			Events.postEvent(-20000, "onAddEventResponse", this, null);
		}
	}

	/**
	 * modify OccurEvent in the plotinfo
	 * @param oe
	 */
	public void modifyPlotEvent(OccurEvent oe) {
		if (_mdyEvtList == null)
			_mdyEvtList = new LinkedList();
		_mdyEvtList.add(oe);
		if (getAttribute(ATTR_ON_MODIFY_EVENT_RESPONSE) == null) {
			setAttribute(ATTR_ON_MODIFY_EVENT_RESPONSE, Boolean.TRUE);
			Events.postEvent(-20000, "onModifyEventResponse", this, null);
		}
	}

	/**
	 * remove OccurEvent from the plotinfo
	 * @param oe
	 */
	public void removePlotEvent(OccurEvent oe) {
		if (_rmEvtList == null)
			_rmEvtList = new LinkedList();
		_rmEvtList.add(oe);
		if (getAttribute(ATTR_ON_REMOVE_EVENT_RESPONSE) == null) {
			setAttribute(ATTR_ON_REMOVE_EVENT_RESPONSE, Boolean.TRUE);
			Events.postEvent(-20000, "onRemoveEventResponse", this, null);
		}
	}

	/**
	 * add a PlotData to the plotinfo
	 * @param pd
	 */
	public void addPlotData(PlotData pd) {
		if (_addDataList == null)
			_addDataList = new LinkedList();
		_addDataList.add(pd);
		if (getAttribute(ATTR_ON_ADD_DATA_RESPONSE) == null) {
			setAttribute(ATTR_ON_ADD_DATA_RESPONSE, Boolean.TRUE);
			Events.postEvent(-20000, "onAddDataResponse", this, null);
		}
	}

	/**
	 * modify a PlotData in  the plotinfo
	 * @param pd
	 */
	public void modifyPlotData(PlotData pd) {
		if (_mdyDataList == null)
			_mdyDataList = new LinkedList();
		_mdyDataList.add(pd);
		if (getAttribute(ATTR_ON_MODIFY_DATA_RESPONSE) == null) {
			setAttribute(ATTR_ON_MODIFY_DATA_RESPONSE, Boolean.TRUE);
			Events.postEvent(-20000, "onModifyDataResponse", this, null);
		}
	}

	/**
	 * remove a PlotData from the plotinfo
	 * @param pd
	 */
	public void removePlotData(PlotData pd) {
		if (_rmDataList == null)
			_rmDataList = new LinkedList();
		_rmDataList.add(pd);
		if (getAttribute(ATTR_ON_REMOVE_DATA_RESPONSE) == null) {
			setAttribute(ATTR_ON_REMOVE_DATA_RESPONSE, Boolean.TRUE);
			Events.postEvent(-20000, "onRemoveDataResponse", this, null);
		}
	}

	/**
	 * 
	 * @return a ListModel for Data
	 */
	public ListModel getDataModel() {
		return _dataModel;
	}

	/** Initializes _dataListener and register the listener to the model
	 */
	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					onListDataChange(event);
				}
			};

		_dataModel.addListDataListener(_dataListener);
	}
	
	/**
	 * sets a ListModel to the plotinfo
	 * @param dataModel
	 */
	public void setDataModel(ListModel dataModel) {
		if (dataModel == null) return;
		if (_dataModel != null) {
			_dataModel.removeListDataListener(_dataListener);
			response("removeAllPlotData" + getUuid(), new AuSetAttribute(this,"removeAllPlotData",null));
		}
		_dataModel = dataModel;

		if (_dataModel != null) {
			initDataListener();
			for (int i = 0, j = _dataModel.getSize(); i < j; i++) {
				PlotData pd = (PlotData)_dataModel.getElementAt(i);
				_dataList.add(pd);
				this.addPlotData(pd);
			}
		}

	}
	
	protected void onListDataChange(ListDataEvent event) {
		int lower = event.getIndex0();
		int upper = event.getIndex1();
		switch (event.getType()) {
		case ListDataEvent.INTERVAL_ADDED:
			for (int i = lower; i <= upper; i++) {
				PlotData pd = (PlotData) _dataModel.getElementAt(i);
				_dataList.add(pd);
				this.addPlotData(pd);
			}
			break;
		case ListDataEvent.INTERVAL_REMOVED:			
			int count = upper - lower + 1,nowCount = 0;
			for (ListIterator it = _dataList.listIterator(upper+1); it.hasPrevious();) {
				PlotData pd = (PlotData) it.previous();				
				if(count == nowCount)break;
				it.remove();
				_dataList.remove(pd);
				this.removePlotData(pd);			
				nowCount++;
			}
			break;
		case ListDataEvent.CONTENTS_CHANGED:	
			for (int i = lower; i <= upper; i++) {
				PlotData pd = (PlotData) _dataModel.getElementAt(i);
				_dataList.set(i, pd);
				this.modifyPlotData(pd);
			}
		}
	}

	/**
	 * 
	 * @return a ListModel for Event
	 */
	public ListModel getEventModel() {
		return _eventModel;
	}

	/** Initializes _eventListener and register the listener to the model
	 */
	private void initEventListener() {
		if (_eventListener == null)
			_eventListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					onListEventChange(event);
				}
			};
		_eventModel.addListDataListener(_eventListener);
	}
	
	/**
	 * sets a ListModel to the plotinfo
	 * @param eventModel
	 */
	public void setEventModel(ListModel eventModel) {
		if (_eventModel != null) {
			_eventModel.removeListDataListener(_eventListener);
			for (int i = 0, j = _eventList.size(); i < j; i++) {
				this.removePlotEvent((OccurEvent)_eventList.removeFirst());
			}
		}
		_eventModel = eventModel;
		if (_eventModel != null) {
			initEventListener();
			for (int i = 0, j = _eventModel.getSize(); i < j; i++) {
				OccurEvent oe = (OccurEvent)_eventModel.getElementAt(i);
				_eventList.add(oe);
				this.addPlotEvent(oe);
			}
		}
	}	
	
	protected void onListEventChange(ListDataEvent event) {
		int lower = event.getIndex0();
		int upper = event.getIndex1();
		switch (event.getType()) {
		case ListDataEvent.INTERVAL_ADDED:			
			for (int i = lower; i <= upper; i++) {
				OccurEvent oe = (OccurEvent) _eventModel.getElementAt(i);
				_eventList.add(oe);
				this.addPlotEvent(oe);
			}
			break;
		case ListDataEvent.INTERVAL_REMOVED:
			int count = upper - lower + 1,nowCount = 0;
			for (ListIterator it = _eventList.listIterator(upper+1); it.hasPrevious();) {
				OccurEvent oe = (OccurEvent) it.previous();				
				if(count == nowCount)break;
				it.remove();
				_eventList.remove(oe);
				this.removePlotEvent(oe);			
				nowCount++;
			}
			break;
		case ListDataEvent.CONTENTS_CHANGED:
			for (int i = lower; i <= upper; i++) {
				OccurEvent oe = (OccurEvent) _eventModel.getElementAt(i);
				_eventList.set(i, oe);
				this.modifyPlotEvent(oe);
			}
		}
	}

	/**
	 * Returns TimeGeometry object
	 */
	public TimeGeometry getTimeGeometry() {
		return _timeGeometry;
	}

	/**
	 * Sets a timeGeometry to the plotinfo
	 * @param timeGeometry
	 */
	public void setTimeGeometry(TimeGeometry timeGeometry) {
		if (!Objects.equals(_timeGeometry, timeGeometry)) {
			_timeGeometry = timeGeometry;
			smartUpdate("timeGeometry", converTimeGeometryToJSON());
		}
	}

	/**
	 * Returns ValueGeometry object
	 */
	public ValueGeometry getValueGeometry() {
		return _valueGeometry;
	}

	/**
	 * Sets a valueGeometry to the plotinfo
	 * @param valueGeometry
	 */
	public void setValueGeometry(ValueGeometry valueGeometry) {
		if (!Objects.equals(_valueGeometry, valueGeometry)) {
			_valueGeometry = valueGeometry;
			smartUpdate("valueGeometry", converValueGeometryToJSON());
		}
	}

	/**
	 * Returns the Operator object
	 */
	public Operator getOperator() {
		return _operator;
	}

	/**
	 * Sets a Operator to the plotinfo
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(Operator operator) {
		if (!Objects.equals(this._operator, operator)) {
			this._operator = operator;
			super.invalidate();
			if (getParent() != null)
				getParent().invalidate();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.AbstractComponent#setParent(org.zkoss.zk.ui.Component)
	 */
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Timeplot))
			throw new UiException("Unsupported parent for plotinfo: " + parent);
		super.setParent(parent);
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.AbstractComponent#insertBefore(org.zkoss.zk.ui.Component, org.zkoss.zk.ui.Component)
	 */
	public boolean insertBefore(Component child, Component insertBefore) {		
		 throw new UiException("no allow child in plotinfo");
	}
	
	/**package*/ List getEventModelList() {
		return Collections.unmodifiableList(_eventList);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#service(org.zkoss.zk.au.AuRequest, boolean)
	 */
	public void service(AuRequest request, boolean everError) {		
		
		final String cmd = request.getCommand();
		
		if (cmd.equals("onEventClick")) {
			final Plotinfo comp = (Plotinfo) request.getComponent();
			if (comp == null)
				throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);			
			
			final JSONArray data = (JSONArray) request.getData().get("data");
			if (data != null && data.size() != 1)
				throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA, new Object[]{Objects.toString(data), this });
			OccurEvent oe = null;
			for (Iterator it = comp.getEventModelList().iterator(); it.hasNext();) {
				OccurEvent o = ((OccurEvent) it.next());
				if (((String)data.get(0)).equals(o.getId())) {
					oe = o;
					break;
				}
			}			
			Events.postEvent(new Event("onEventClick", comp, oe));
		} else if (cmd.equals(OverPlotEvent.ON_OVER_PLOTDATA)) {
			Events.postEvent(OverPlotEvent.getOverPlotEvent(request));
		} else super.service(request, everError);
	}

	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#renderProperties(org.zkoss.zk.ui.sys.ContentRenderer)
	 */
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws IOException {
		super.renderProperties(renderer);
		
		render(renderer, "fillColor", _fillColor);			
		render(renderer, "dotColor", _dotColor);			
		render(renderer, "lineColor", _lineColor);			
		if (_lineWidth != 1.0f)
			renderer.render("lineWidth", _lineWidth);		
		if (_eventLineWidth != 1.0f)
			renderer.render("eventLineWidth", _eventLineWidth);		 
		if (_dotRadius != 2.0f)
			renderer.render("dotRadius", _dotRadius);		
		if (_showValues)
			renderer.render("showValues", _showValues);
		if (_hideValueFlag)
			renderer.render("hideValueFlag", _hideValueFlag);
		if (!_roundValues)
			renderer.render("roundValues", _roundValues);
		if (_valuesOpacity != 75)
			renderer.render("valuesOpacity", _valuesOpacity);
		if (_bubbleWidth != 300)
			renderer.render("bubbleWidth", _bubbleWidth);
		if (_bubbleHeight != 200)
			renderer.render("bubbleHeight", _bubbleHeight);		
		
		if (_operator != null) {
			render(renderer, "operator", _operator.getOperator());
			String params = _operator.getParams();
			if (params != null)
				render(renderer, "operatorParams", params);
		}
		if (_pds != null){
			render(renderer, "separator", _pds.getSeparator());
			renderer.render( "dataSourceColumn", _pds.getDataSourceColumn());
			render(renderer, "dataSourceUri",getEncodedURL(_pds.getDataSourceUri()));
		}
		if (_eventSourceUri != null)
			render(renderer, "eventSourceUri", getEncodedURL(_eventSourceUri));
		if (_valueGeometry != null) 
			render(renderer,"valueGeometry", converValueGeometryToJSON());
		if (_timeGeometry != null)
			render(renderer,"timeGeometry", converTimeGeometryToJSON());
	}
	
	private String converValueGeometryToJSON() {
		JSONObject json = new JSONObject();
		
		json.put("type", _valueGeometry.toString());
		json.put("id", _valueGeometry.getValueGeometryId());
		json.put("axisColor", _valueGeometry.getAxisColor());
		json.put("gridColor", _valueGeometry.getGridColor());
		json.put("gridLineWidth", String.valueOf(_valueGeometry.getGridLineWidth()));
		json.put("axisLabelsPlacement", _valueGeometry.getAxisLabelsPlacement());
		json.put("gridSpacing", String.valueOf(_valueGeometry.getGridSpacing()));
		json.put("gridType", _valueGeometry.getGridType());
		json.put("gridShortSize", String.valueOf(_valueGeometry.getGridShortSize()));
		json.put("min", String.valueOf(_valueGeometry.getMin()));
		json.put("max", String.valueOf(_valueGeometry.getMax()));
		
		return json.toString();
	}
	
	private String converTimeGeometryToJSON() {
		JSONObject json = new JSONObject();
		
		json.put("id", _timeGeometry.getTimeGeometryId());
		json.put("axisColor", _timeGeometry.getAxisColor());
		json.put("gridColor", _timeGeometry.getGridColor());
		json.put("gridLineWidth", String.valueOf(_timeGeometry.getGridLineWidth()));
		json.put("axisLabelsPlacement",_timeGeometry.getAxisLabelsPlacement());
		json.put("gridStep", String.valueOf(_timeGeometry.getGridStep()));
		json.put("gridStepRange", String.valueOf(_timeGeometry.getGridStepRange()));
		json.put("min", getTimeStr(_timeGeometry.getMin()));
		json.put("max", getTimeStr(_timeGeometry.getMax()));
		json.put("timeValuePosition", _timeGeometry.getTimeValuePosition());
		json.put("displayMilli", _timeGeometry.getDisplayMilli());
		json.put("measureDensityOfMillisecond", String.valueOf(_timeGeometry.getMeasureDensityOfMillisecond()));
		
		
		Map formats = _timeGeometry.getFormats();
		if (formats.size() > 0) {
			JSONObject formatJson = new JSONObject();
			
			for (Iterator it = formats.entrySet().iterator(); it.hasNext();) {
				Map.Entry me = (Map.Entry) it.next();
				formatJson.put(me.getKey(), me.getValue());
			}
			json.put("formats", formatJson.toString());
		}
		
		return json.toString();
	}
	
	private String getTimeStr(Date date) {
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
		if (date != null)
			return format.format(date);
		return "0";
	}
	
	private String getEncodedURL(String url) {
		  final Desktop dt = getDesktop(); //it might not belong to any desktop
		  return dt != null ? dt.getExecution().encodeURL(url): "";			 
	}
	
	private class EncodedURL implements org.zkoss.zk.ui.util.DeferredValue {
		private String url;
		
		public EncodedURL(String url) {
			super();
			this.url = url;
		}

		public Object getValue() {
			return getEncodedURL(this.url);
		}
	}
	// -- Super --//
	/** Not childable. */
	public boolean isChildable() {
		return false;
	}
}