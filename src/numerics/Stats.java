//*****************************************************************************
//
// Stats.java
//
// This is a small class for doing some basic stats functions that NUANCE
// needs access to quite often.
//
//*****************************************************************************
package numerics;
public class Stats {
    /**
     * Returns the percentage of pairs that match across the given range
     */
    public static double pcthit(double[] array1, double[] array2,
				int offset, int len) {
	int hits = 0;
	int  top = offset + len;
	for(int i = offset; i < top; i++)
	    if(array1[i] == array2[i])
		hits++;
	return (double)hits / (double)len;
    }

    /**
     * Returns the percentage of pairs that match across the two arrays
     */
    public static double pcthit(double[] array1, double[] array2) {
	return pcthit(array1, array2, 0, array1.length);
    }

    /**
     * Returns the average of the array, in the given range
     */
    public static double average(double[] array, int offset, int len) {
	double sum = 0;
	int    top = offset + len;
	for(int i = offset; i < top; i++)
	    sum += array[i];
	return sum / len;
    }

    /**
     * Returns the average value of the array
     */
    public static double average(double[] array) {
	return average(array, 0, array.length);
    }

    /**
     * Returns the covariance of the two arrays in the given range
     */
    public static double covariance(double[] array1, double a1avg,
				    double[] array2, double a2avg,
				    int offset, int len) {
	double sum = 0;
	int    top = offset + len;
	for(int i = offset; i < top; i++)
	    sum += (array1[i] - a1avg)*(array2[i] - a2avg);
	return sum;
    }

    /**
     * Returns the covariance of two arrays, using the provided averages
     */
    public static double covariance(double[] array1, double a1avg,
				    double[] array2, double a2avg) {
	return covariance(array1, a1avg, array2, a2avg, 0, array1.length);
    }

    /**
     * Returns the covariance of the two arrays in the given range
     */
    public static double covariance(double[] array1, double[] array2,
				    int offset, int len) {
	return covariance(array1, average(array1, offset, len), 
			  array2, average(array2, offset, len),
			  offset, len);
    }

    /**
     * Returns the covariance of two arrays
     */
    public static double covariance(double[] array1, double[] array2) {
	return covariance(array1, average(array1), array2, average(array2));
    }

    /**
     * Returns the variance of the array in the given range
     */
    public static double variance(double[] array, double avg,
				  int offset, int len) {
	double var = 0;
	int    top = offset + len;
	for(int i = offset; i < top; i++)
	    var += Math.pow(array[i] - avg, 2);
	return var;
    }

    /**
     * Returns the variance of the values in the array, with the provided
     * variance
     */
    public static double variance(double[] array, double avg) {
	return variance(array, avg, 0, array.length);
    }

    /**
     * Returns the variance of the values in the array, in the given range
     */
    public static double variance(double[] array, int offset, int len) {
	return variance(array, average(array, offset, len), offset, len);
    }

    /**
     * Returns the variance of the values in the array
     */
    public static double variance(double[] array) {
	return variance(array, average(array));
    }
    
    /**
     * Returns the standard deviation of the values in the given range
     * (bias-corrected), using the given average.
     */
    public static double stdev(double[] array, double avg,
			       int offset, int len) {
	return Math.sqrt(variance(array, avg, offset, len) / (len-1));
    }

    /**
     * Returns the standard deviation of the values in the given range
     * (bias-corrected)
     */
    public static double stdev(double[] array, int offset, int len) {
	return stdev(array, average(array, offset, len), offset, len);
    }

    /**
     * Returns the standard deviation of the values in the array 
     * (bias-corrected), using the given average
     */
    public static double stdev(double[] array, double avg) {
	return stdev(array, avg, 0, array.length);
    }

    /**
     * Returns the sample standard deviation of the array (bias-corrected)
     */
    public static double stdev(double[] array) {
	return stdev(array, 0, array.length);
    }

    /**
     * Returns the minimum value in the given range
     */ 
    public static double min(double[] array, int offset, int len) {
	double min = array[offset];
	int    top = offset + len;
	for(int i = offset+1; i < top; i++)
	    if(array[i] < min) min = array[i];
	return min;
    }

    /**
     * Returns the minimum value in the array
     */
    public static double min(double[] array) {
	return min(array, 0, array.length);
    }

    /**
     * returns the range of the values in the array, given the offset and len
     */
    public static Range range(double[] array, int offset, int len) {
	double min = array[offset];
	double max = array[offset];
	int    top = offset + len;
	for(int i = offset+1; i < top; i++) {
	    if(array[i] < min) min = array[i];
	    if(array[i] > max) max = array[i];
	}
	return new Range(min, max);
    }

    /**
     * returns the range of the values in the array
     */
    public static Range range(double[] array) {
	return range(array, 0, array.length);
    }

    /**
     * Returns the maximum value in the given range
     */ 
    public static double max(double[] array, int offset, int len) {
	double max = array[offset];
	int    top = offset + len;
	for(int i = offset+1; i < top; i++)
	    if(array[i] > max) max = array[i];
	return max;
    }

    /**
     * Returns the maximum value in the array
     */
    public static double max(double[] array) {
	return max(array, 0, array.length);
    }

    /**
     * Round a number to the specified number of decimal places
     */
    public static double round(double val, int places) {
	int tmp = (int)(val * Math.pow(10, places));
	return tmp/Math.pow(10, places);
    }

    /**
     * Returns the distance between two vectors in the given range. 
     * Degree of 1 is city block distance, 2 is euclidean distance, etc...
     */
    public static double distance(double[] array1, double[] array2, int degree,
				  int offset, int len) {
	double distance = 0;
	int         top = offset + len;
	for(int i = offset; i < top; i++)
	    distance += Math.abs(Math.pow((array1[i] - array2[i]), degree));
	return distance;
    }

    /**
     * Returns the distance between two vectors. Degree of 1 is city block
     * distance, 2 is euclidean distance, etc...
     */
    public static double distance(double[] array1, double[] array2, int degree){
	return distance(array1, array2, degree, 0, array1.length);
    }

    /**
     * Returns the scaled sum of squared error for the two arrays of data
     * within the given range
     */
    public static double scaledSSE(double[] targets, double[] estimates,
				   int offset, int len) {
	double a;
	double b;
	double avg_y = 0, avg_t = 0;
	double var_y = 0, covar = 0;
	int top = offset + len;
	
	// figure out all of our values for t and y, plus averages
	for(int i = offset; i < top; i++) {
	    avg_t += targets[i];
	    avg_y += estimates[i];
	}
	avg_t /= len;
	avg_y /= len;
	
	// calculate our variance and covariance
	for(int i = offset; i < top; i++) {
	    covar += (targets[i] - avg_t) * (estimates[i] - avg_y);
	    var_y += Math.pow(estimates[i] - avg_y, 2);
	}
	
	// catch the case in which there is no variance...
	try {
	    b = covar/var_y;
	} catch(Error e) {
	    b = 0;
	}
	a = avg_t - b*avg_y;
	
	// now calculate our error
	double error = 0;
	for(int i = offset; i < top; i++)
	    error += Math.pow(targets[i] - (a + b*estimates[i]), 2);
	return error;
    }

    /**
     * Returns the scaled sum of squared error for the two arrays of data
     */
    public static double scaledSSE(double[] targets, double[] estimates) {
	return scaledSSE(targets, estimates, 0, targets.length);
    }

    /**
     * Derives the slope for x predicting y in a regression equation, using
     * the given range
     */
    public static double slope(double[] y, double[] x, int offset, int len) {
	double avg_x = 0, avg_y = 0;
	int top = offset + len;
	
	// figure out all of our values for t and y, plus averages
	for(int i = offset; i < top; i++) {
	    avg_y += y[i];
	    avg_x += x[i];
	}
	avg_y /= len;
	avg_x /= len;
	return slope(y, x, avg_y, avg_x, offset, len);
    }

    /**
     * Derives the slope for x predicting y in a regression equation
     */
    public static double slope(double[] y, double[] x) {
	return slope(y, x, 0, y.length);
    }

    /**
     * Derives the slope for x predicting y in a regression equation, using
     * the given range and averages
     */
    public static double slope(double[] y, double[] x, 
			       double avg_y, double avg_x,
			       int offset, int len) {
	double var_x = 0, covar = 0;
	int      top = offset + len;  
	// calculate our variance and covariance
	for(int i = offset; i < top; i++) {
	    covar += (y[i] - avg_y) * (x[i] - avg_x);
	    var_x += Math.pow(x[i] - avg_x, 2);
	}
	
	// catch the case in which there is no variance...
	try {
	    return covar/var_x;
	} catch(Error e) {
	    return 0;
	}
    }

    /**
     * Derives the slope for x predicting y in a regression equation
     */
    public static double slope(double[] y, double[] x, 
			       double avg_y, double avg_x) {
	return slope(y, x, avg_y, avg_x, 0, y.length);
    }

    /**
     * Derives the y-intercept for this regression equation in the given range
     */
    public static double intercept(double[] y, double[] x, int offset, int len){
	return average(y, offset, len) - 
	    slope(y, x, offset, len) * average(x, offset, len);
    }

    /**
     * Derives the y-intercept for this regression equation
     */
    public static double intercept(double[] y, double[] x) {
	return intercept(y, x, 0, y.length);
    }

    /**
     * returns the correlation between the two arrays in the given range, and
     * using the specified sums
     */
    public static double correlation(double         sum1, double         sum2,
				     double sum1ValsSqrd, double sum2ValsSqrd,
				     double   sum1Times2, int N) {
        double sqrSum1 = sum1 * sum1;
        double sqrSum2 = sum2 * sum2;
        
        double RNumerator   = (N*sum1Times2) - (sum1*sum2);
        double RDenominator = (((N*sum1ValsSqrd) - sqrSum1) * 
			       ((N*sum2ValsSqrd) - sqrSum2));
        RDenominator = Math.sqrt(RDenominator);

        double R = RNumerator/RDenominator;        
        if(Double.isInfinite(R) || Double.isNaN(R))
	    return 0;
        else
            return R;
    }

    /**
     * returns the correlation of the two arrays in the given range
     */
    public static double correlation(double[] array1, double[] array2,
				     int offset, int len) {
	int  top = offset + len;
        double sum1         = 0;
        double sum2         = 0;
        double sum1ValsSqrd = 0;
        double sum2ValsSqrd = 0;
        double sum1Times2   = 0;

	for(int i = offset; i < top; i++) {
            sum1ValsSqrd += array1[i] * array1[i];
            sum2ValsSqrd += array2[i] * array2[i];
            sum1Times2   += array1[i] * array2[i];            
            sum1         += array1[i];
            sum2         += array2[i];
        }

	return correlation(sum1, sum2, sum1ValsSqrd, sum2ValsSqrd, sum1Times2,
			   len);
    }

    /**
     * returns the correlation between two arrays
     */
    public static double correlation(double[] array1, double[] array2) {
	return correlation(array1, array2, 0, array1.length);
    }
}
