/* Plotinfo.js
 Purpose:
 
 Description:
 
 History:
 Thu Nov  5 12:33:21 TST 2009, Created by Jimmy
 Copyright (C) 2009 Potix Corporation. All Rights Reserved.
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 */
(function () {
	function _parseDateTime(dateString) {
		if (!dateString)
		    return null;
		try {
			// bug: ff cannot parse time in millisecond
			if (zk.ff) {
				var start = dateString.indexOf('.'),
					ms = dateString.substring(start, dateString.indexOf(' ', start)),
					date;
				dateString = dateString.replace(ms, '');
				date = new Date(Date.parse(dateString));
				
				date.setMilliseconds(parseInt(ms.substring(1, ms.length)));
				return date;
			}
		    return new Date(Date.parse(dateString));
		} catch (e) {
		    return null;
		}
	}

    function _newEvent(eventSource, params) {
        var evt = new Timeline.DefaultEventSource.Event({
            id: 			"dynaEvent" + params.id,
            start: 			_parseDateTime(params.start),
            end: 			_parseDateTime(params.end),
            latestStart: 	_parseDateTime(params.latestStart),
            earliestEnd: 	_parseDateTime(params.earliestEnd),
            instant: 		!params.duration, // instant
            text: 			params.text, // text
            description:	params.description,
            image: 			eventSource._resolveRelativeURL(params.image, ""),
            link: 			eventSource._resolveRelativeURL(params.link, ""),
            icon:			eventSource._resolveRelativeURL(params.icon, ""),
            color: 			params.color,
            textColor: 		params.textColor
            //hoverText: 	bindings["hoverText"], new version only, old one unimplement
            //caption: 		bindings["caption"],
            //classname:	bindings["classname"],
            //tapeImage: 	bindings["tapeImage"],
            //tapeRepeat: 	bindings["tapeRepeat"],
            //eventID: 		bindings["eventID"],
            //trackNum:		bindings["trackNum"]
        });
        if (params.wikiUrl) 
            evt.setWikiInfo(params.wikiUrl, params.wikiSection);
        return evt;
    }
	
	function _processCustomValues(data) {
		var customValues = data.customValues;
        if (typeof customValues == "string")
			data.customValues = jq.evalJSON(customValues);
		return data.customValues;
	}
	
timeplotz.Plotinfo = zk.$extends(zk.Widget, {
    _lineWidth: 1.0,
    _eventLineWidth: 1.0,
    _dotRadius: 2.0,
    _showValues: false,
    _roundValues: true,
    _valuesOpacity: 75,
    _bubbleWidth: 300,
    _bubbleHeight: 200,    
    
    $define: {
        lineWidth: function() {	
            var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
            plotinfo.lineWidth = parseFloat(this._lineWidth);
            plotinfo.timeplot.repaint();
        },
        eventLineWidth: function() {
            var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
            plotinfo.eventLineWidth = parseFloat(this._eventLineWidth);
            plotinfo.timeplot.repaint();
        },
        dotRadius: function() {
            var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
            plotinfo.dotRadius = parseFloat(this._dotRadius);
            plotinfo.timeplot.repaint();
        },
        showValues: null,
		hideValueFlag: null,
        roundValues: function() {
            var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
            plotinfo.roundValues = this._roundValues;
            plotinfo.timeplot.repaint();
        },
        valuesOpacity: null,
        bubbleWidth: function() {
            var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
            plotinfo.bubbleWidth = parseInt(this._bubbleWidth);
            plotinfo.timeplot.repaint();
        },
        bubbleHeight: function() {
            var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
            plotinfo.bubbleHeight = parseInt(this._bubbleHeight);
            plotinfo.timeplot.repaint();
        },
        lineColor: function() {
            var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
            plotinfo.lineColor = this._lineColor;
            plotinfo.timeplot.repaint();
        },
        fillColor: function() {
			var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
            plotinfo.fillColor = this._fillColor;
            plotinfo.timeplot.repaint();
        },
        dotColor: function() {
            var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
            plotinfo.dotColor = this._dotColor;
            plotinfo.timeplot.repaint();
        },
        eventSourceUri: function() {
            var plotinfo, eventSource;
            if (!(plotinfo = this._plotinfo) || !(eventSource = this._plotinfo.eventSource)) return;
            eventSource.clear();
            plotinfo.timeplot.loadXML(this._eventSourceUri, eventSource);
            if (!this._dynaEvents) return;
            for (var evtid in this._dynaEvents) {
                var evt = this._dynaEvents[evtid];
                if (evt) 
                    eventSource.add(evt);
            }
        },
		timeGeometry: function() {
			var timeGeometry = this._timeGeometry = jq.evalJSON(this._timeGeometry);
			this._timeGeometry.gridLineWidth = parseFloat(timeGeometry.gridLineWidth);
			this._timeGeometry.gridStep = zk.parseInt(timeGeometry.gridStep);
			this._timeGeometry.gridStepRange = zk.parseInt(timeGeometry.gridStepRange);	
			if (timeGeometry.formats)
				this._timeGeometry.formats = jq.evalJSON(timeGeometry.formats);
				
			var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
			this.repaint();
        },
		valueGeometry: function() {
			var valueGeometry = this._valueGeometry = jq.evalJSON(this._valueGeometry);
			this._valueGeometry.gridLineWidth = parseFloat(valueGeometry.gridLineWidth);
			this._valueGeometry.gridSpacing = zk.parseInt(valueGeometry.gridSpacing);
			this._valueGeometry.gridShortSize = zk.parseInt(valueGeometry.gridShortSize);
			this._valueGeometry.min = zk.parseInt(valueGeometry.min);
			this._valueGeometry.max = zk.parseInt(valueGeometry.max);
			
			var plotinfo;
            if (!(plotinfo = this._plotinfo)) return;
			this.repaint();
        },
		plotinfo: null
    },
    
	$init: function () {
		this.$supers('$init', arguments);
        this._addPlotDatas = [];
		this._addPlotEvents = [];
		this._modifyPlotDatas = [];
		this._modifyPlotEvents = [];
		this._removePlotDatas = [];
		this._removePlotEvents = [];
		this._plotDatas = [];
	},
	
    setNewPlotinfo: function() {
        this._plotinfo = this.$class._createPlotinfo(this);
    },
    
    setTimeplot: function(timeplot) {
        this._plotinfo.timeplot = timeplot;
		this.fillData();
    },
	
	fillData: function() {
		
		if (this._plotDatas.length) {
			var plotDatas = this._plotDatas.$clone();
			this._plotDatas = [];
			this.addPlotData(plotDatas);
		}
		
		if (this._addPlotDatas.length)
			this.addPlotData(this._addPlotDatas);
			
		if (this._addPlotEvents.length)
			this.addPlotEvents(this._addPlotEvents);
			
		if (this._modifyPlotDatas.length)
			this.modifyPlotData(this._modifyPlotDatas);
			
		if (this._modifyPlotEvents.length)
			this.modifyPlotEvent(this._modifyPlotEvents);
			
		if (this._removePlotDatas.length)
			this.removePlotData(this._removePlotDatas);
			
		if (this._removePlotEvents.length)
			this.removePlotEvent(this._removePlotEvents);
    },
    
    setDataSourceUri: function(dataSourceUri) {
        this.dataSourceUri = dataSourceUri;
        var plot = this._plotinfo,
			eventSource = this.getPlotEventSource(plot);	
		if (!eventSource) return;	     
        eventSource.clear();
        plot.timeplot.loadText(this.dataSourceUri, this.separator, eventSource);
        if (!this._dynaDatas) return;
        for (var evtid in this._dynaDatas) {
            var evt = this._dynaDatas[evtid];
            if (evt) 
                eventSource.add(evt);
        }
        eventSource._fire("onAddMany", []);
    },
    
    setAddPlotEvent: function(eventArray) {
        eventArray = jq.evalJSON(eventArray);
        if (!eventArray.length) return;
		
		if (!this.parent.isRealVisible()) {//store data
			for (var event; (event = eventArray.shift());)
			  this._addPlotEvents.push(event);
			return;
		}
		this.addPlotEvent(eventArray);
    },
	
	addPlotEvent: function(eventArray) {
        var plot = this._plotinfo,
			eventSource = plot.eventSource;
		
        this._hideTips(plot);
		
        if (!this._dynaEvents)
            this._dynaEvents = {};

        for (var event; (event = eventArray.shift());) {
			var evt = _newEvent(eventSource, event);
			if (this._dynaEvents[evt._id]) continue;//already exists.
			this._dynaEvents[evt._id] = evt;
			eventSource.add(evt);
        }
        plot.timeplot.repaint();
    },
    
    setModifyPlotEvent: function(eventArray) {
        if (!this._dynaEvents) return;
        eventArray = jq.evalJSON(eventArray);
        if (!eventArray.length) return;
		
        if (!this.parent.isRealVisible()) {//store data
			for (var event; (event = eventArray.shift());)
			  this._modifyPlotEvents.push(event);
			return;
		}
		this.modifyPlotEvent(eventArray);
    },
	
	modifyPlotEvent: function(eventArray) {
        var plot = this._plotinfo,
			eventSource = plot.eventSource;
			
        this._hideTips(plot);
		
        for (var event; (event = eventArray.shift());) {
            var id = "dynaEvent" + event.id,
				evt = this._dynaEvents[id];
            if (!evt) continue;
            eventSource._events._events.remove(evt);
            delete this._dynaEvents[id];
            evt = _newEvent(eventSource, event);
            eventSource.add(evt);
            this._dynaEvents[evt._id] = evt;
        }
        plot.timeplot.repaint();
    },
    
    setRemovePlotEvent: function(eventArray) {
        if (!this._dynaEvents) return;
        eventArray = jq.evalJSON(eventArray);
        if (!eventArray.length) return;
		
		 if (!this.parent.isRealVisible()) {//store data
			for (var event; (event = eventArray.shift());)
			  this._removePlotEvents.push(event);
			return;
		}
		this.removePlotEvent(eventArray);
    },
	
	removePlotEvent: function(eventArray) {
        var plot = this._plotinfo,
			eventSource = plot.eventSource, 
        	timeplot = plot.timeplot;
		
        this._hideTips(plot);
		
        for (var event; (event = eventArray.shift());) {
            var id = "dynaEvent" + event.id,
				evt = this._dynaEvents[id];
            if (!evt) break;
            eventSource._events._events.remove(evt);
            delete this._dynaEvents[id];
            jq('#' + timeplot._id + "-" + id).remove();
        }
        timeplot.repaint();
    },
    
    setAddPlotData: function(dataArray) {		
        dataArray = jq.evalJSON(dataArray);
        if (!dataArray.length) return;
		if (!this.parent.isRealVisible()) {//store data
			 for (var data; (data = dataArray.shift());)
			  this._addPlotDatas.push(data);
			return;
		}
		this.addPlotData(dataArray);
    },
	
	addPlotData: function(dataArray) {
        var plot = this._plotinfo,
        	dateTimeFormat = 'iso8601',
			eventSource = this.getPlotEventSource(plot);

        this._hideTips(plot);
        if (!this._dynaDatas)
            this._dynaDatas = {};
        
        for (var data; (data = dataArray.shift());) {
			var id = "dynmic" + data.id;
            if (this._dynaDatas[id]) continue;//already exists.
            var parseDateTimeFunction = eventSource._events.getUnit().getParser(dateTimeFormat),
				evt = new Timeplot.DefaultEventSource.NumericEvent(parseDateTimeFunction(data.time), 
							[parseFloat(data.value)], _processCustomValues(data), data.format);
            evt._id = id;
            eventSource.add(evt);
            this._dynaDatas[id] = evt;
			this._plotDatas.push(data);
        }
		
		var timeplot = plot.timeplot;
        eventSource._fire("onAddMany", []);
		this.reSetRange();
    },
    
    setModifyPlotData: function(dataArray) {
    	if (!this._dynaDatas) return;
        dataArray = jq.evalJSON(dataArray);
        if (!dataArray.length) return;
		if (!this.parent.isRealVisible()) {//store data
			 for (var data; (data = dataArray.shift());)
			  this._modifyPlotDatas.push(data);
			return;
		}
		this.modifyPlotData(dataArray);
    },
	
	modifyPlotData: function(dataArray) {
        var plot = this._plotinfo,
        	dateTimeFormat = 'iso8601',
        	eventSource = this.getPlotEventSource(plot);

        this._hideTips(plot);
		
        for (var data; (data = dataArray.shift());) {
            var id = "dynmic" + data.id,
				evt = this._dynaDatas[id];
            if (!evt) continue;
            eventSource._events._events.remove(evt);
            var parseDateTimeFunction = eventSource._events.getUnit().getParser(dateTimeFormat);
            evt = new Timeplot.DefaultEventSource.NumericEvent(parseDateTimeFunction(data["time"]), 
						[parseFloat(data.value)], _processCustomValues(data), data.format);
            evt._id = id;
            eventSource.add(evt);
            this._dynaDatas[id] = evt;
        }
        eventSource._fire("onAddMany", []);
		this.reSetRange();
    },
    
    setRemovePlotData: function(dataArray) {
        if (!this._dynaDatas)return;
        dataArray = jq.evalJSON(dataArray);
        if (!dataArray.length) return;
		if (!this.parent.isRealVisible()) {//store data
			 for (var data; (data = dataArray.shift());)
			  this._removePlotDatas.push(data);
			return;
		}
		this.removePlotData(dataArray);
    },
	
	removePlotData: function(dataArray) {
        var plot = this._plotinfo,
        	eventSource = this.getPlotEventSource(plot);
		
        this._hideTips(plot);
		
        for (var data; (data = dataArray.shift());) {
            var id = "dynmic" + data.id,
				evt = this._dynaDatas[id];
            if (!evt) continue;
            eventSource._events._events.remove(evt);
            delete this._dynaDatas[id];
            eventSource._events._index();
			this._plotDatas.$remove(data);
        }
        eventSource._fire("onAddMany", []);
      	this.reSetRange();
    },
	
	setRemoveAllPlotData: function(){
		var plot = this._plotinfo;
		if (!this._dynaDatas) return;
		this._hideTips(plot);
		this._dynaDatas = {};
		this.getPlotEventSource(plot).clear();
		this._plotDatas = [];
		this._addPlotDatas = [];
		this._modifyPlotDatas = [];
		this._removePlotDatas = [];
	},
	
	getPlotEventSource: function(plot){
		if (!plot) return null;
		var dataSource = plot.dataSource;
		return this.operator ? dataSource._dataSource._eventSource : dataSource._eventSource;
	},
	
	reSetRange: function(){
		var plot = this._plotinfo,
			timeplot = plot.timeplot;
		if (!timeplot) return;
		var plotinfos = timeplot._plotInfos,
			range = this.getPlotEventSource(plot).getRange(),
			timeGeometry = plot.timeGeometry,
			min = timeGeometry._min,
			max = timeGeometry._max,
			earliestDate = min || range.earliestDate,
			latestDate = max || range.latestDate;
		
		for(var i = 0, j = plotinfos.length ; i < j ; i++){
			var range = this.getPlotEventSource(plotinfos[i]).getRange(),
				dsEarliestDate = range.earliestDate,
				dsLatestDate =range.latestDate;
			
			if(dsEarliestDate && earliestDate > dsEarliestDate)
				earliestDate = dsEarliestDate;
			if(dsLatestDate && latestDate < dsLatestDate)
				latestDate = dsLatestDate;		
		}	
		
		timeGeometry._earliestDate = earliestDate;	
		timeGeometry._latestDate = latestDate;
	},
	
	repaint: function() {
		var parent;
		if (parent = this.parent)
			parent.setRepaint();
	},
	
    _hideTips: function(plotinfo) {
        var timeplot = this._plotinfo.timeplot;
        if (!plotinfo || !timeplot || !timeplot._plots) return;
        
        for (var i = timeplot._plots.length; i--;) {
            var p = timeplot._plots[i];
            if (p._timeFlag) 
                p._timeFlag.style.display = 'none';
            if (p._valueFlag) 
                p._valueFlag.style.display = 'none';
            if (p._valueFlagLineLeft) 
                p._valueFlagLineLeft.style.display = 'none';
            if (p._valueFlagLineRight) 
                p._valueFlagLineRight.style.display = 'none';
            if (p._valueFlagPole) 
                p._valueFlagPole.style.display = 'none';
        }
    }
});
})();