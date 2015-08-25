/* Timeplot.js
 Purpose:
 
 Description:
 
 History:
 Thu Nov  5 12:33:21 TST 2009, Created by Jimmy
 Copyright (C) 2009 Potix Corporation. All Rights Reserved.
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 */
(function () {
	function _createValueGeometry(valueGeometry) {
		if (valueGeometry.type == "LogarithmicValueGeometry")
			return new Timeplot.LogarithmicValueGeometry(valueGeometry);
        else {
            return new Timeplot.DefaultValueGeometry(valueGeometry);
        }
	}
    
    function _createTimeGeometry(timeGeometry) {		
		return new Timeplot.DefaultTimeGeometry(timeGeometry);
    }
	
	function _createPlotinfo(widget) {
		var params = {
			id:                widget.uuid,
	        dataSource:        widget.dataSource,
	        eventSource:       widget.eventSource,
	        lineWidth:         widget._lineWidth,
	        dotRadius:         widget._dotRadius,
	        eventLineWidth:    widget._eventLineWidth,
	        showValues:        widget._showValues,
			hideValueFlag:     widget._hideValueFlag,
	        roundValues:       widget._roundValues,
	        valuesOpacity:     widget._valuesOpacity,
	        bubbleWidth:       widget._bubbleWidth,
	        bubbleHeight:      widget._bubbleHeight
		};
		
        if (widget._lineColor)
            params.lineColor = widget._lineColor;
        if (widget._fillColor)
            params.fillColor = widget._fillColor;
        if (widget._dotColor)
            params.dotColor = widget._dotColor;
		if (widget.timeGeometry)
            params.timeGeometry = widget.timeGeometry;
		if (widget.valueGeometry)
            params.valueGeometry = widget.valueGeometry;
        
        return Timeplot.createPlotInfo(params);
	}
	
timeplotz.Timeplot = zk.$extends(zk.Widget, {
    _height: "200px",
	_defaultDataSource:'defaultDataSource',
	_defaultEventSource:'defaultEventSource',
    
    $define: {
        hideTimeFlag: null,
		timeFlagFormat: null,
		height: function() {
            var node;
            if (!(node = this.$n())) return;
            jq(node).height(this._height);
            this.setRepaint();
        },
        width: function() {
            var node;
            if (!(node = this.$n())) return;
			jq(node).width(this._width);
            this.setRepaint();
        }
    },
	
	_initData: function() {
	 	this._plotinfos = [];
	    this._eventSources = {};
	    this._dataSources = {};
		this._columnSources = {};
		this._valueGeometries = {};
	    this._timeGeometries = {};
	    this._trendlines = [];
	},
    
    bind_: function() {
        this.$supers('bind_', arguments);
		zWatch.listen({onShow: this,onSize: this});
		if (zk(this.$n()).isRealVisible())
        	this._initialize();
    },
   
	unbind_: function() {
		zWatch.unlisten({onShow: this,onSize: this});
		if (this._timeplot) {
			this._timeplot.dispose();
	        this._timeplot = null;
		}
		this.$supers('unbind_', arguments);
    },
    
	_initialize: function() {
		this._initData();
		var dsLoadList = [],
			esLoadList = [];
		
		for (var child = this.firstChild; child; child = child.nextSibling) {
			if (child.$instanceof(timeplotz.Plotinfo)){
				child.dataSource = this._createDateSource(child, dsLoadList);
				child.eventSource = this._createEventSource(child, esLoadList);
				this._createGeometry(child);
				child.setPlotinfo(_createPlotinfo(child));
				this._plotinfos.push(child.getPlotinfo());
				child._dynaDatas = {};
				child._dynaEvents = {};
			}else if (child.$instanceof(timeplotz.Trendline)){
				this._trendlines.push(child);
			}
		}
		if (!this.desktop) return; 
		var timeplot =  this._timeplot = Timeplot.create(this.$n(), this._plotinfos, this._trendlines);
		if (this._hideTimeFlag)
			timeplot._hideTimeFlag = this._hideTimeFlag;
		if (this._timeFlagFormat)
			timeplot._timeFlagFormat = this._timeFlagFormat;
		this._loadPlotData(dsLoadList);
		this._loadEventData(esLoadList);
		for (var child = this.firstChild; child; child = child.nextSibling)
			child.setTimeplot(timeplot);
       
    },
	
	_createDateSource: function(child, dsLoadList) {
		var uri = child.dataSourceUri,
			dataSourceUri = uri ? uri: this._defaultDataSource,
			ds = this._dataSources[dataSourceUri],
			cs =  this._columnSources[dataSourceUri],
			operator = eval("(" + child.operator + ")"),
			dataSourceColumn = child.dataSourceColumn;
				
		
	 	if (!uri || !ds) {// if dataSource not the same 
            ds = this._dataSources[dataSourceUri] = 
					new Timeplot.DefaultEventSource();
            cs = this._columnSources[dataSourceUri] = 
					new Timeplot.ColumnSource(ds, dataSourceColumn ? dataSourceColumn: 1);
            if (uri)
                dsLoadList.push(child);
        }
		
        if (operator)
            cs = this._columnSources[dataSourceUri] = 
					new Timeplot.Processor(cs, operator, eval("(" + child.operatorParams + ")"));
		
		return cs;
	},
	
	_createEventSource: function(child, esLoadList) {
		var uri = child.getEventSourceUri(),
			eventSourceUri = uri ? uri: this._defaultEventSource,
			es = this._eventSources[eventSourceUri];
		
		 if (!uri || !es) {// if eventSource not the same 
            es = this._eventSources[eventSourceUri] = 
					new Timeplot.DefaultEventSource();
            if (uri) 
                esLoadList.push(uri);
        }
		
		return es;
	},
	
	_createGeometry: function(child) {
		var valueGeometry = child._valueGeometry,
			timeGeometry = child._timeGeometry;
		if (valueGeometry) {
            var id = valueGeometry.id,
				vg = this._valueGeometries[id];
            if (!vg)
                vg = this._valueGeometries[id] = _createValueGeometry(valueGeometry);                
            child.valueGeometry = vg;
        }
       
        if (timeGeometry) {
            var id = timeGeometry.id;
				tg = this._timeGeometries[id];
            if (!tg) {
                tg = this._timeGeometries[id] = _createTimeGeometry(timeGeometry);
            }
            child.timeGeometry = tg;
        }
	},
	
	_loadPlotData: function(dsLoadList) {
		for (var i = dsLoadList.length; i--;) {//load txt
			var plot = dsLoadList[i],
				uri = plot.dataSourceUri;
			this._timeplot.loadText(uri, plot.separator, this._dataSources[uri]);
        }
    },
	
	_loadEventData: function(esLoadList) {
		for (var i = esLoadList.length; i--;) {//load xml
			var uri = esLoadList[i];
			this._timeplot.loadXML(uri, this._eventSources[uri]);
        }
    },
	
	setRepaint: function() {
		var wgt = this,
			timer = wgt._repaintTimer;
		if (timer)
			clearTimeout(timer);
		// prevent multiple repaint
		timer = wgt._repaintTimer = setTimeout(function () {
			wgt._timeplot.dispose();
			wgt._initialize();
			clearTimeout(wgt._repaintTimer);
			wgt._repaintTimer = null;
		}, 100);
	},
	
	onShow: function() {
		if (this._timeplot) {
			this._timeplot.repaint();
			for (var child = this.firstChild; child; child = child.nextSibling) 
				child.fillData();
		} else 
			this._initialize();
    },
	
	onSize: function() {
		var wgt = this,
			timeplot = wgt._timeplot;
		if (!this.resizeTimerID && timeplot) {
			this.resizeTimerID = window.setTimeout(function() {
				wgt.resizeTimerID = null;
				timeplot.repaint();
	        }, 100);
	    }
    }
	
});
})();