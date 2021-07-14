//*****************************************************************************
//
// SummaryStats.java
//
// Builds a class that has a bunch of summary stats about the values in the
// given array.
//
//*****************************************************************************
package numerics;
public class SummaryStats implements java.io.Serializable {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Range  range;
    private double  mean;
    private double stdev;



    //*************************************************************************
    // constructors
    //*************************************************************************
    public SummaryStats(double[] array) {
	this(array, 0, array.length);
    }

    public SummaryStats(double[] array, int offset, int len) {
	range = Stats.range(array, offset, len);
	mean  = Stats.average(array, offset, len);
	stdev = Stats.stdev(array, mean, offset, len);
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    public Range getRange()  { return range; }
    public double getMean()  { return  mean; }
    public double getStdev() { return stdev; }
    public double getMin()   { return range.min(); }
    public double getMax()   { return range.max(); }
}
