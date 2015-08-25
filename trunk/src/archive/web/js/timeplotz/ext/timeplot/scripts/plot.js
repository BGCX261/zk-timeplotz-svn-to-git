/**
 * Plot Layer
 * 
 * @fileOverview Plot Layer
 * @name Plot
 */
 
/**
 * A plot layer is the main building block for timeplots and it's the object
 * that is responsible for painting the plot itself. Each plot needs to have
 * a time geometry, either a DataSource (for time series
 * plots) or an EventSource (for event plots) and a value geometry in case 
 * of time series plots. Such parameters are passed along
 * in the 'plotInfo' map.
 * 
 * @constructor
 */
Timeplot.Plot = function(timeplot, plotInfo, trendlines) {
	this._timeplot = timeplot;
    this._canvas = timeplot.getCanvas();
    this._plotInfo = plotInfo;
    this._id = plotInfo.id;
    this._timeGeometry = plotInfo.timeGeometry;
    this._valueGeometry = plotInfo.valueGeometry;
    this._showValues = plotInfo.showValues;
	this._hideValueFlag = plotInfo.hideValueFlag;
    this._theme = new Timeline.getDefaultTheme();
    this._dataSource = plotInfo.dataSource;
    this._eventSource = plotInfo.eventSource;
    this._bubble = null;
    this._trendlines = trendlines;
};

Timeplot.Plot.prototype = {
    
    /**
     * Initialize the plot layer
     */
    initialize: function() {
	    if (this._showValues && this._dataSource && this._dataSource.getValue) {
            this._timeFlag = this._timeplot.putDiv("timeflag","timeplot-timeflag");
	        this._valueFlag = this._timeplot.putDiv(this._id + "valueflag","timeplot-valueflag");
	        this._valueFlagLineLeft = this._timeplot.putDiv(this._id + "valueflagLineLeft","timeplot-valueflag-line");
            this._valueFlagLineRight = this._timeplot.putDiv(this._id + "valueflagLineRight","timeplot-valueflag-line");
            if (!this._valueFlagLineLeft.firstChild) {
            	this._valueFlagLineLeft.appendChild(SimileAjax.Graphics.createTranslucentImage(Timeplot.urlPrefix + "images/line_left.png"));
                this._valueFlagLineRight.appendChild(SimileAjax.Graphics.createTranslucentImage(Timeplot.urlPrefix + "images/line_right.png"));
            }
	        this._valueFlagPole = this._timeplot.putDiv(this._id + "valuepole","timeplot-valueflag-pole");

            var opacity = this._plotInfo.valuesOpacity;
            
            SimileAjax.Graphics.setOpacity(this._timeFlag, opacity);
            SimileAjax.Graphics.setOpacity(this._valueFlag, opacity);
            SimileAjax.Graphics.setOpacity(this._valueFlagLineLeft, opacity);
            SimileAjax.Graphics.setOpacity(this._valueFlagLineRight, opacity);
            SimileAjax.Graphics.setOpacity(this._valueFlagPole, opacity);

            var plot = this;
            
		    var mouseOverHandler = function(elmt, evt, target) {
				if (!plot._hideValueFlag && plot._dataSource && plot._dataSource.getData())
		        	plot._valueFlag.style.display = "block";
		        mouseMoveHandler(elmt, evt, target);
		    };
		
		    var day = 24 * 60 * 60 * 1000;
		    var month = 30 * day;
		    
		    var mouseMoveHandler = function(elmt, evt, target) {
		    	if (typeof SimileAjax != "undefined") {
                    var c = plot._canvas,
						x = Math.round(SimileAjax.DOM.getEventRelativeCoordinates(evt,plot._canvas).x),
						timeStr;
			        if (x > c.width) x = c.width;
			        if (isNaN(x) || x < 0) x = 0;
			        var t = plot._timeGeometry.fromScreen(x);
			        if (t == 0 || plot._dataSource == null) { // something is wrong; IE is the only one that claims _dataSource is ever null, and only after items have changed
                        plot._valueFlag.style.display = "none";
			        	return;
			        }
					var dataSource = plot._dataSource,
						validTime = dataSource.getClosestValidTime(t),
						dataIndex = dataSource.getDataIndex(validTime),
						v = dataIndex != null ? dataSource.getData().values[dataIndex]: 0, //dataIndex maybe is 0 
						d = new Date(validTime),
						p = plot._timeGeometry.getPeriod(),
						wgt = zk.Widget.$(plot._id),
						datas = {},
						customValues,
						format = plot._timeplot._timeFlagFormat;
					if (!dataSource.getData() || !validTime) return;
					x = plot._timeGeometry.toScreen(validTime);
					if (v) {
						if (plot._plotInfo.roundValues) v = Math.round(v);
						plot._valueFlag.innerHTML = new String(v);
					}
					
					//ZK add
					if (plot._timeGeometry._earliestDate) {
						var d1 = plot._timeGeometry._earliestDate;
						month = new Date(new Date(d1.getYear(), d1.getMonth()+1, 1).getTime() - 2000).getDate() * day;
					}

					//ZK add
					if (format)
						timeStr = zk.fmt.Date.formatDate(d,format);//ZK add
					else if (p < day) {
						if (plot.getTimeGeometry()._displayMilli == 'TRUE') {
							// _displayMilli defined in geometry.js
							var h = d.getHours(),
								m = d.getMinutes(),
			        			s = d.getSeconds(),
			        			ms = d.getMilliseconds();
							timeStr = ((h < 10)? '0' : '') + h + ':'
									+ ((m < 10)? '0' : '') + m + ':'
									+ ((s < 10)? '0' : '') + s + '.'
									+ ((ms < 100)? '0' : ((ms < 10)? '00' : '')) + ms;
						} else
							timeStr = d.toLocaleTimeString();
					}
			        else if (p > month)
			        	timeStr = d.toLocaleDateString();
			        else timeStr = d.toLocaleString();

					plot._timeFlag.innerHTML = timeStr;
			        
			        var tw = plot._timeFlag.clientWidth,
						th = plot._timeFlag.clientHeight,
						tdw = Math.round(tw / 2),
						vw = plot._valueFlag.clientWidth,
						vh = plot._valueFlag.clientHeight,
						y = plot._valueGeometry.toScreen(v),
						poleOffset = plot._timeplot._hideTimeFlag ? [0,0]: [5,6];

                    if (x + tdw > c.width) {
                        var tx = c.width - tdw;
                    } else if (x - tdw < 0) {
                        var tx = tdw;
                    } else {
                    	var tx = x;
                    }
					if (plot._timeGeometry._timeValuePosition == "top") {
						plot._updateTimeFlag(x, (th - poleOffset[0]), (c.height - y - th + poleOffset[1]), 
								(tx - tdw), -6);
					}
					else {
						plot._updateTimeFlag(x, (th - poleOffset[0]), (y - th + poleOffset[1]), 
								(tx - tdw), -6);
					}
					if (v) {
						if (x + vw + 14 > c.width && y + vh + 4 > c.height) {
							plot._updateValueFlag(plot._valueFlagLineLeft, plot._valueFlagLineRight, (x - 14), (y - 14), (x - vw - 13), (y - vh - 13));
						} else if (x + vw + 14 > c.width && y + vh + 4 < c.height) {
							plot._updateValueFlag(plot._valueFlagLineRight, plot._valueFlagLineLeft, (x - 14), y, (x - vw - 13), (y + 13));
						} else if (x + vw + 14 < c.width && y + vh + 4 > c.height) {
							plot._updateValueFlag(plot._valueFlagLineRight, plot._valueFlagLineLeft, x, (y - 13), (x + 13), (y - 13));
						} else plot._updateValueFlag(plot._valueFlagLineLeft, plot._valueFlagLineRight, x, y, (x + 13), (y + 13));
					}
					//ZK add
					if (dataIndex != null && dataIndex != wgt.plotDataIndex) {
						customValues = dataSource._eventSource._events._events._a[dataIndex]._customValues;
						format = dataSource._eventSource._events._events._a[dataIndex]._format;
						if (customValues)
							datas = customValues;
						if (format)
							datas.format = format;
						wgt.plotDataIndex = datas.plotDataIndex = dataIndex;
						datas.time = d.getTime();
						datas.value = v;
						wgt.fire('onOverPlotData', datas);
					}
					//ZK add
		    	}
		    };

            var timeplotElement = this._timeplot.getElement();
            SimileAjax.DOM.registerEvent(timeplotElement, "mouseover", mouseOverHandler);
            SimileAjax.DOM.registerEvent(timeplotElement, "mousemove", mouseMoveHandler);
	    }
    },

    /**
     * Dispose the plot layer and all the data sources and listeners associated to it
     */
    dispose: function() {
        if (this._dataSource) {
            this._dataSource.removeListener(this._paintingListener);
            this._paintingListener = null;
            this._dataSource.dispose();
            this._dataSource = null;
            this._trendlines = this._lastX = null;
        }
    },

 	/**
     * Hide the values
     */
    hideValues: function() {
        if (this._valueFlag) this._valueFlag.style.display = "none";
        if (this._timeFlag) this._timeFlag.style.display = "none";
        if (this._valueFlagLineLeft) this._valueFlagLineLeft.style.display = "none";
        if (this._valueFlagLineRight) this._valueFlagLineRight.style.display = "none";
        if (this._valueFlagPole) this._valueFlagPole.style.display = "none";
    },

    /**
     * Return the data source of this plot layer (it could be either a DataSource or an EventSource)
     */
    getDataSource: function() {
        return (this._dataSource) ? this._dataSource : this._eventSource;
    },

    /**
     * Return the time geometry associated with this plot layer
     */
    getTimeGeometry: function() {
        return this._timeGeometry;
    },

    /**
     * Return the value geometry associated with this plot layer
     */
    getValueGeometry: function() {
        return this._valueGeometry;
    },

    /**
     * Paint this plot layer
     */
    paint: function() {
        var ctx = this._canvas.getContext('2d');
        
        ctx.lineWidth = this._plotInfo.lineWidth;
        ctx.lineJoin = 'miter';
        
        if (this._dataSource) { 
            if (this._plotInfo.fillColor) {
                if (this._plotInfo.fillGradient) {
                    var gradient = ctx.createLinearGradient(0,this._canvas.height,0,0);
                    gradient.addColorStop(0,this._plotInfo.fillColor.toString());
                    gradient.addColorStop(0.5,this._plotInfo.fillColor.toString());
                    gradient.addColorStop(1, 'rgba(255,255,255,0)');

                    ctx.fillStyle = gradient;
                } else {
                    ctx.fillStyle = this._plotInfo.fillColor.toString();
                }

                ctx.beginPath();
                ctx.moveTo(0,0);
	            this._plot(function(x,y) {
					if (!isNaN(x)) {
						if (!isNaN(y)) {
							ctx._lastX = x;
							if (!ctx._lastY)
					   			ctx.lineTo(x, y);
							else {
								ctx._lastY = false;
								ctx.lineTo(x - 1, 0);
								ctx.lineTo(x, y);
							}
						} else {
							ctx._lastY = true;
							ctx.lineTo(ctx._lastX || 0, 0);
							ctx._lastX = x;
						}
					}
	            });
	            
				ctx._lastX = ctx._lastY = false;
				
                if (this._plotInfo.fillFrom == Number.NEGATIVE_INFINITY) {
					ctx.lineTo(this._canvas.width, 0);
                } else if (this._plotInfo.fillFrom == Number.POSITIVE_INFINITY) {
                    ctx.lineTo(this._canvas.width, this._canvas.height);
                    ctx.lineTo(0, this._canvas.height);
                } else {
                    ctx.lineTo(this._canvas.width, this._valueGeometry.toScreen(this._plotInfo.fillFrom));
                    ctx.lineTo(0, this._valueGeometry.toScreen(this._plotInfo.fillFrom));
                }

                ctx.fill();
            }
                    
            if (this._plotInfo.lineColor) {
                ctx.strokeStyle = this._plotInfo.lineColor.toString();
	            ctx.beginPath();
                    var first = true;
	            this._plot(function(x,y) {
	            	ctx._lastX = x;
					if (isNaN(x)) return;
                    if (first) {
                         first = false;
                         if (!isNaN(y))
						 	ctx.moveTo(x,y);
						 else
						 	ctx.moveTo(0,0);
                    }
					if (!isNaN(y)) {
					   	if (!ctx._lastY)
					   		ctx.lineTo(x, y);
						else {
							ctx._lastY = false;
							ctx.moveTo(x, y);
						}
					} else {
						ctx._lastY = true;
						ctx.moveTo(x, 0);
						ctx.lineTo(x, 0);
				   }
	            });
	            ctx.stroke();
                this._lastX = ctx._lastX;
            }

            if (this._plotInfo.dotColor) {
                ctx.fillStyle = this._plotInfo.dotColor.toString();
                var r = this._plotInfo.dotRadius;
                this._plot(function(x,y) {
					if (!isNaN(x)) {
						ctx.beginPath();
						if (!isNaN(y)) 
							ctx.arc(x, y, r, 0, 2 * Math.PI, true);
						ctx.fill();
					}
                });
            }
        }

        if (this._eventSource) {
            var gradient = ctx.createLinearGradient(0,0,0,this._canvas.height);
            gradient.addColorStop(1, 'rgba(255,255,255,0)');

            ctx.strokeStyle = gradient;
            ctx.fillStyle = gradient; 
            ctx.lineWidth = this._plotInfo.eventLineWidth;
            ctx.lineJoin = 'miter';
            
            var i = this._eventSource.getAllEventIterator();
            while (i.hasNext()) {
                var event = i.next();
                var color = event.getColor();
                color = (color) ? new Timeplot.Color(color) : this._plotInfo.lineColor;
                var eventStart = event.getStart().getTime();
                var eventEnd = event.getEnd().getTime();
                
                if (eventStart == eventEnd) {
                    var c = color.toString();
                    gradient.addColorStop(0, c);
                    var start = this._timeGeometry.toScreen(eventStart);
                    start = Math.floor(start) + 0.5; // center it between two pixels (makes the rendering nicer)
                    var end = start;
                    ctx.beginPath();
                    ctx.moveTo(start,0);
                    ctx.lineTo(start,this._canvas.height);
                    ctx.stroke();
                    var x = start - 4;
                    var w = 7;
                } else {
                	var c = color.toString(0.5);
                    gradient.addColorStop(0, c);
                    var start = this._timeGeometry.toScreen(eventStart);
                    start = Math.floor(start) + 0.5; // center it between two pixels (makes the rendering nicer)
                    var end = this._timeGeometry.toScreen(eventEnd);
                    end = Math.floor(end) + 0.5; // center it between two pixels (makes the rendering nicer)
                    ctx.fillRect(start,0,end - start, this._canvas.height);
                    var x = start;
                    var w = end - start - 1;
                }
					
                var div = this._timeplot.putDiv(event.getID(),"timeplot-event-box",{
                    left: Math.round(x),
                    width: Math.round(w),
                    top: 0,
                    height: this._canvas.height - 1
                });

                var plot = this;
                var clickHandler = function(event) { 
                    return function(elmt, evt, target) { 
                        var doc = plot._timeplot.getDocument();
						if (plot._bubble) {
							if (plot._bubble.content)
								jq(plot._bubble.content).unbind('mousedown',plot._bubble._doClick);
							plot._bubble._doClick = null;
						}
                    	plot._closeBubble();
                    	var coords = SimileAjax.DOM.getEventPageCoordinates(evt);
                    	var elmtCoords = SimileAjax.DOM.getPageCoordinates(elmt);
                        plot._bubble = SimileAjax.Graphics.createBubbleForPoint(
							coords.x, parseInt(elmtCoords.top) + parseInt(plot._canvas.height), 
							plot._plotInfo.bubbleWidth, plot._plotInfo.bubbleHeight, "bottom");
                        event.fillInfoBubble(plot._bubble.content, plot._theme, plot._timeGeometry.getLabeler());
						plot._bubble._doClick = function (evt) {
							zk.Widget.$(plot._id).fire("onEventClick", {data: [event._id.replace("dynaEvent", '')]});
						};
						plot._bubble.content.id = plot._id + "!bubble";
						jq(plot._bubble.content).mousedown(plot._bubble._doClick);
                    };
                };
                var mouseOverHandler = function(elmt, evt, target) {
                	elmt.oldClass = elmt.className;
                    elmt.className = elmt.className + " timeplot-event-box-highlight";
                };
                var mouseOutHandler = function(elmt, evt, target) {
                    elmt.className = elmt.oldClass;
                    elmt.oldClass = null;
                }
                
                if (!div.instrumented) {
	                SimileAjax.DOM.registerEvent(div, "click"    , clickHandler(event));
	                SimileAjax.DOM.registerEvent(div, "mouseover", mouseOverHandler);
	                SimileAjax.DOM.registerEvent(div, "mouseout" , mouseOutHandler);

		            div.instrumented = true;
                }
            }
        }
        
        if (this._trendlines.length)
        	this.paintTrendline();
    },

    _plot: function(f) {
        var data = this._dataSource.getData();
        if (data) {
	        var times = data.times;
	        var values = data.values;
	        var T = times.length;
	        for (var t = 0; t < T; t++) {
	        	var x = this._timeGeometry.toScreen(times[t]);
	        	var y = this._valueGeometry.toScreen(values[t]);
	            f(x, y);
	        }
        }
    },
    
    _closeBubble: function() {
        if (this._bubble != null) {
            this._bubble.close();
            this._bubble = null;
        }
    },
	
	_updateValueFlag: function(hideNode, showNode, lineX, lineY, x, y) {
		if (this._hideValueFlag) return;
		hideNode.style.display = "none";
	    this._timeplot.placeDiv(showNode,{
	        left: lineX,
	        bottom: lineY,
	        display: "block"
	    });
	    this._timeplot.placeDiv(this._valueFlag,{
	        left: x,
	        bottom: y,
	        display: "block"
	    });
    },
	
	_updateTimeFlag: function(poleX, poleY, poleH, x, y) {
		this._timeplot.placeDiv(this._valueFlagPole, {
			left: poleX,
			bottom: poleY,
			height: poleH,
			display: "block"
		});
		if (this._timeplot._hideTimeFlag) return;
		this._timeplot.placeDiv(this._timeFlag, {
			left: x,
			bottom: y,
			display: "block"
		});
    },
    
    paintTrendline: function() {
    	if (this._trendlines) {
    		var ctx = this._canvas.getContext('2d');
    		
    		for (var i = 0, j = this._trendlines.length; i < j; i++) {
    			var trendline = this._trendlines[i];
    			var fp = trendline._fixPosition;
		    	var value = this._valueGeometry.toScreen(trendline._value);
		    	ctx.strokeStyle = trendline._lineColor;
		    	if (!trendline._dashed) {
					ctx.beginPath();
			        ctx.moveTo(0, value);
			        ctx.lineTo(0, value);
			        ctx.lineTo(this._lastX, value);
			        ctx.stroke();
		    	} else {
					ctx.beginPath();
			        ctx.dashedLine(0, value, this._lastX, value);
		    	}
		    	this._timeplot.putText(this._id + trendline.uuid + "-" + 'trendline-label', trendline._label, "timeplot-grid-label", {
		            left: 5 + parseInt(fp[0] || 0),
		            bottom: value + parseInt(fp[1] || 0),
		            color: trendline._labelColor
		        });
    		}
    	}
    }
    

};