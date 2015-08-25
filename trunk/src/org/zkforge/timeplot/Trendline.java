/* Trendline.js
 Purpose:
 
 Description:
 
 History:
 Thu Dec  27 12:33:21 TST 2011, Created by Matthew
 Copyright (C) 2009 Potix Corporation. All Rights Reserved.
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.zkforge.timeplot;

import org.zkoss.lang.Objects;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

public class Trendline extends XulElement {

	private String _label = "";
	private Number _value;
	private String _labelColor = "#000000";
	private String _lineColor = "#000000";
	private boolean _dashed = false;
	private String _fixPosition = "";// ex: "10,-20" for x/y

	public String getFixPosition() {
		return _fixPosition;
	}

	public void setFixPosition(String fixPosition) {
		if (!Objects.equals(_fixPosition, fixPosition)) {
			this._fixPosition = fixPosition;
			smartUpdate("fixPosition", getParsedFixPosition(_fixPosition));
		}
	}

	public boolean isDashed() {
		return _dashed;
	}

	public void setDashed(boolean dashed) {
		if (_dashed != dashed) {
			_dashed = dashed;
			smartUpdate("dashed", _dashed);
		}
	}

	public Number getValue() {
		return _value;
	}

	public void setValue(int value) {
		setValue(Integer.valueOf(value));
	}

	public void setValue(double value) {
		setValue(Double.valueOf(value));
	}

	public void setValue(Number value) {
		if (!Objects.equals(_value, value)) {
			_value = value;
			smartUpdate("value", _value);
		}
	}

	public String getLabelColor() {
		return _labelColor;
	}

	public void setLabelColor(String labelColor) {
		if (!Objects.equals(_labelColor, labelColor)) {
			_labelColor = labelColor;
			smartUpdate("labelColor", _labelColor);
		}
	}

	public String getLineColor() {
		return _lineColor;
	}

	public void setLineColor(String lineColor) {
		if (!Objects.equals(_lineColor, lineColor)) {
			_lineColor = lineColor;
			smartUpdate("lineColor", _lineColor);
		}
	}

	public String getLabel() {
		return _label;
	}

	public void setLabel(String label) {
		if (!Objects.equals(_label, label)) {
			_label = label;
			smartUpdate("label", _label);
		}
	}

	// super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "value", _value);

		if (!_label.equals(""))
			render(renderer, "label", _label);
		if (!_labelColor.equals("#000000"))
			render(renderer, "labelColor", _labelColor);
		if (!_lineColor.equals("#000000"))
			render(renderer, "lineColor", _lineColor);
		if (_dashed != false)
			render(renderer, "dashed", _dashed);
		if (!_fixPosition.equals(""))
			render(renderer, "fixPosition", getParsedFixPosition(_fixPosition));
	}

	public String getZclass() {
		return (this._zclass != null ? this._zclass : "z-trendline");
	}

	protected boolean isChildable() {
		return false;
	}

	private int[] getParsedFixPosition(String fixpos) {
		return Utils.stringToInts(fixpos, 0);
	}
}
