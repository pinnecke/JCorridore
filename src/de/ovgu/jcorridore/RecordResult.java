package de.ovgu.jcorridore;

import de.ovgu.jcorridore.annotations.Record;

public class RecordResult {

	private Record annotation;

	double average;

	private Exception exception = null;
	double lowerQuartile;
	double maximumRuntime;
	double median;
	String methodIdentifier;
	double minimumRuntime;
	double standardError;

	double upperQuartile;

	public double variance;

	long timestamp;
	
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param annotation
	 */
	public RecordResult(Record annotation, String methodIdentifier) {
		this.annotation = annotation;
		this.methodIdentifier = methodIdentifier;
	}

	/**
	 * @return the annotation
	 */
	public Record getAnnotation() {
		return annotation;
	}

	/**
	 * @return the average
	 */
	public double getAverage() {
		return average;
	}

	public Exception getException() {
		return exception;
	}

	/**
	 * @return the lowerQuartile
	 */
	public double getLowerQuartile() {
		return lowerQuartile;
	}

	/**
	 * @return the maximumRuntime
	 */
	public double getMaximumRuntime() {
		return maximumRuntime;
	}

	/**
	 * @return the median
	 */
	public double getMedian() {
		return median;
	}

	/**
	 * @return the minimumRuntime
	 */
	public double getMinimumRuntime() {
		return minimumRuntime;
	}

	/**
	 * @return the standardError
	 */
	public double getStandardError() {
		return standardError;
	}

	/**
	 * @return the upperQuartile
	 */
	public double getUpperQuartile() {
		return upperQuartile;
	}

	/**
	 * @return the variance
	 */
	public double getVariance() {
		return variance;
	}

	public boolean hasException() {
		return exception != null;
	}

	/**
	 * @param annotation
	 *            the annotation to set
	 */
	public void setAnnotation(Record annotation) {
		this.annotation = annotation;
	}

	public void setException(Exception e) {
		exception = e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (exception != null)
			return "RecordResult [exception=" + exception.getMessage() + "]";
		else
			return "RecordResult [method=" + methodIdentifier + ", N=" + annotation.samples() +", minimumRuntime=" + minimumRuntime + ", maximumRuntime=" + maximumRuntime + ", lowerQuartile=" + lowerQuartile + ", upperQuartile=" + upperQuartile + ", median=" + median + ", average=" + average + ", standardError=" + standardError + ", variance=" + variance + "]";
	}


}
