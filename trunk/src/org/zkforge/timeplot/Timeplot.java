/* Timeplot.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  5 12:32:58 TST 2009, Created by Jimmy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkforge.timeplot;
/**
 * 
 * @author Jimmy
 */
import java.io.IOException;
import java.util.List;

import org.zkforge.timeline.data.OccurEvent;
import org.zkforge.timeplot.data.PlotData;
import org.zkoss.lang.Objects;
import org.zkoss.zk.au.out.AuSetAttribute;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;

public class Timeplot extends HtmlBasedComponent {

	private String _height = "200px";// default

	private String _width;
	
	private boolean _hideTimeFlag = false;
	
	private String _timeFlagFormat;
	
	/**
	 * Returns the height of the timeplot component.
	 * <p>Default: 150px.
	 */
	public String getHeight() {
		return _height;
	}

	/**
	 * Sets the height of the timeplot component.
	 * <p>Default: 150px.
	 * @param height
	 */
	public void setHeight(String height) {
		if (!Objects.equals(_height, height)) {
			_height = height;
			smartUpdate("height", height);
		}
	}

	/**
	 * Returns the width of the timeplot component.
	 * <p>Default: 100%.
	 */
	public String getWidth() {
		return _width;

	}

	/**
	 * Sets the width of the timeplot component.
	 * <p>Default: 100%.
	 * @param width
	 */
	public void setWidth(String width) {
		if (!Objects.equals(_width, width)) {
			_width = width;
			smartUpdate("width", width);
		}
	}
	
	/**
	 * Returns whether is hide the time flag of plot values on dot, 
	 * the timeplot hides plot time flag when mouse over if true.
	 * <p>Default: false.
	 */
	public boolean isHideTimeFlag() {
		return _hideTimeFlag;
	}

	/**
	 * Sets whether is hide the time flag of plot values on dot, 
	 * the timeplot hides plot time flag when mouse over if true.
	 * <p>Default: false.
	 * @param hideTimeFlag
	 */
	public void setHideTimeFlag(boolean hideTimeFlag) {
		if (this._hideTimeFlag != hideTimeFlag) {
			this._hideTimeFlag = hideTimeFlag;
			smartUpdate("hideTimeFlag", this._hideTimeFlag);
			repaint();
		}
	}
	
	/**
	 * Sets the date flag format.
	 * @since 1.1_50
	 */
	public void setTimeFlagFormat(String timeFlagFormat) {
		if (this._timeFlagFormat != timeFlagFormat)
			_timeFlagFormat = timeFlagFormat;
		smartUpdate("timeFlagFormat", this._timeFlagFormat);
		repaint();
	}
	
	/**
	 * Returns the date flag format.
	 * @since 1.1_50
	 */
	public String getTimeFlagFormat() {
		return _timeFlagFormat;
	}
	
	/**
	 * Execute javascritp repaint function.
	 */
	public void repaint(){
		if (isInvalidated()) return;
		response("repaint" + getUuid(), new AuSetAttribute(this, "repaint", null));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.AbstractComponent#insertBefore(org.zkoss.zk.ui.Component, org.zkoss.zk.ui.Component)
	 */
	public boolean insertBefore(Component child, Component insertBefore) {
		 if (!(child instanceof Plotinfo || child instanceof Trendline))
			 throw new UiException("Unsupported child for timeplot: " + child);
		return super.insertBefore(child, insertBefore);
	}
	
	public void onChildAdded(Component child) {
		invalidate();
	}
	
	public void onChildRemoved(Component child) {
		invalidate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zul.impl.XulElement#renderProperties(org.zkoss.zk.ui.sys.ContentRenderer)
	 */
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws IOException {
		super.renderProperties(renderer);		
		if (!"200px".equals(_height))
			render(renderer, "height", _height);
		render(renderer, "width", _width);
		if (_hideTimeFlag)
			renderer.render("hideTimeFlag", _hideTimeFlag);
		if (_timeFlagFormat != null)
			renderer.render("timeFlagFormat", _timeFlagFormat);
	}


}
