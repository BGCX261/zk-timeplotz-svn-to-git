package org.zkforge.timeplot.geometry;
/**
 *  A Logarithmic value geometry is useful when plots have values in different magnitudes 
 *  but exhibit similar trends and such trends want to be shown on the same plot 
 *  (here a cartesian geometry would make the small magnitudes disappear). 
 *  NOTE: this class extends Timeplot.DefaultValueGeometry and inherits all of the methods 
 *  of that class. So refer to that class.
 * @author Jimmy Shiau
 *
 */
public class LogarithmicValueGeometry extends DefaultValueGeometry {

	public String toString() {
		return "LogarithmicValueGeometry";
	}
}
