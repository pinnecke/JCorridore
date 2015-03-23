package de.ovgu.jcorridore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Constraint {
	
	float allowedAverageDeviation() default Float.MAX_VALUE;
	
	/**
	 * Specify the allowed +/- deviation runtime against the revision template lower quartile. 
	 */
	float allowedLowerQuartileDeviation() default Float.MAX_VALUE;
	
	/**
	 * Specify the allowed +/- deviation runtime against the revision template maximum runtime. 
	 */
	float allowedMaximumDeviation() default Float.MAX_VALUE;
	
	/**
	 * Specify the allowed +/- deviation runtime against the revision template median. 
	 */
	float allowedMedianDeviation() default Float.MAX_VALUE;
	
	/**
	 * Specify the allowed +/- deviation runtime against the revision template minimum runtime. 
	 */
	float allowedMinimumDeviation() default Float.MAX_VALUE;
	
	double allowedStandardError() default Float.MAX_VALUE;
	
	/**
	 * Specify the allowed +/- deviation runtime against the revision template upper quartile. 
	 */
	float allowedUpperQuartileDeviation() default Float.MAX_VALUE;
	
	double allowedVarianceDeviation() default Float.MAX_VALUE;

	/**
	 * Specify how often this method is rerun before a stable result is expected and reported against
	 * the current template for the actual revision.
	 */
	int samples();

	int revisionReference() default 1;
	
	
	
}
