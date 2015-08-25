/* Trendline.js
 Purpose:
 
 Description:
 
 History:
 Thu Dec  27 12:33:21 TST 2011, Created by Matthew
 Copyright (C) 2009 Potix Corporation. All Rights Reserved.
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 */
timeplotz.Trendline = zk.$extends(zk.Widget, {
	_label: '', 
	_labelColor: '#000000',
	_lineColor: '#000000',
	_value: 0,
	_dashed: false,
	_fixPosition: [0, 0],
	$define: {
		label: _zkf = function() { 
			if (this.desktop) {
				this.getTimeplot().repaint();
			}
		},
		
		fixPosition: function(v) {
			if (this.desktop) {
				this.getTimeplot().repaint();
			}
			this._fixPosition = $eval(v) || [0, 0];
		},
		
		labelColor: _zkf,
		
		lineColor: _zkf,
		
		value: _zkf,
		
		dashed: _zkf
	},
	
	setTimeplot: function(timeplot) {
        this._timeplot = timeplot;
    },
    
    getTimeplot: function(){
    	return this._timeplot
    },
	
	unbind_: function () {
		this.$supers(timeplotz.Trendline,'unbind_', arguments);
		if (this.desktop) {
			this.getTimeplot().repaint();
		}
	},
	
	getZclass: function () {
		return this._zclass != null ? this._zclass: "z-trendline";
	}
});