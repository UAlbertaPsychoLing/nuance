//*****************************************************************************
//
// Range.java
//
// Implements a numeric range from min to max. Can be inclusive or exclusive.
//
//*****************************************************************************
package numerics;
public class Range implements java.io.Serializable {
    //*************************************************************************
    // private variables
    //*************************************************************************
    protected double min;
    protected double max;
    protected boolean min_inclusive;
    protected boolean max_inclusive;



    //*************************************************************************
    // constructors
    //*************************************************************************
    public Range(double min, double max) {
	this(min, true, max, true);
    }

    public Range(double min, boolean min_inclusive,
		 double max, boolean max_inclusive) {
	this.min = min;
	this.max = max;
	this.min_inclusive = min_inclusive;
	this.max_inclusive = max_inclusive;
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    public double min() { return min; }
    public double max() { return max; }
    public boolean includesMin() { return min_inclusive; }
    public boolean includesMax() { return max_inclusive; }
    public void setMin(double min) { this.min = min; }
    public void setMax(double max) { this.max = max; }
    public void set(double min, double max) {
	this.min = min;
	this.max = max;
    }

    public boolean contains(double val) { 
	return ((val > min && val < max) ||
		(min_inclusive && val == min) ||
		(max_inclusive && val == max));
    }

    public Range intersection(Range range) {
	Range lower = (min < range.min ? this : range);
	Range upper = (lower == range ?  this : range);  
	if(!lower.contains(upper.min))
	    return null;
	else {
	    Range intersection = new Range(upper.min, 
					   Math.min(lower.max, upper.max));
	    if(lower.min == upper.min && 
	       !(lower.min_inclusive || upper.min_inclusive))
		intersection.min_inclusive = false;
	    if(lower.max == upper.max &&
	       !(lower.max_inclusive || upper.max_inclusive))
		intersection.max_inclusive = false;
	    return intersection;
	}
    }

    public String toString() {
	return ((min_inclusive ? "[" : "(") + min + ", " + max + 
		(max_inclusive ? "]" : ")"));
    }
}
