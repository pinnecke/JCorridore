package de.ovgu.jcorridore;

public class RecordEntry {
	
	public String author;

	public double average;
	public String comment;	
	public String contact;
	public double lowerQuartile;
	public double maximumRuntime;
	public int maximumStandardError;
	public double median;
	public String methodIdentifier;
	public double minimumRuntime;
	public int repeat;
	public int revision;

	public double standardError;

	public String subject;
	public String topic;
	public double upperQuartile;
	public double variance;
	public String versionedMethodIdentifier;
	public RecordEntry(RecordResult r) {
		this(r.methodIdentifier, r.getAnnotation().revision(), r.getAnnotation().subject().toString(), r.getAnnotation().topic(), r.getAnnotation().comment(), r.getAnnotation().author(), r.getAnnotation().contact(), r.getAnnotation().repeat(), (int) r.getAnnotation().maximumStandardError(), r.average, r.lowerQuartile, r.maximumRuntime, r.median, r.minimumRuntime, r.standardError, r.upperQuartile, r.variance);
	}
	/**
	 * @param methodIdentifier
	 * @param revision
	 * @param subject
	 * @param topic
	 * @param comment
	 * @param author
	 * @param contact
	 * @param repeat
	 * @param maximumStandardError
	 * @param average
	 * @param lowerQuartile
	 * @param maximumRuntime
	 * @param median
	 * @param minimumRuntime
	 * @param standardError
	 * @param upperQuartile
	 * @param variance
	 */
	public RecordEntry(String methodIdentifier, int revision, String subject, String topic, String comment, String author, String contact, int repeat, int maximumStandardError, double average, double lowerQuartile, double maximumRuntime, double median, double minimumRuntime, double standardError, double upperQuartile, double variance) {
		super();
		this.methodIdentifier = methodIdentifier;
		this.versionedMethodIdentifier = methodIdentifier + "$" + revision;
		this.revision = revision;
		this.subject = subject;
		this.topic = topic;
		this.comment = comment;
		this.author = author;
		this.contact = contact;
		this.repeat = repeat;
		this.maximumStandardError = maximumStandardError;
		this.average = average;
		this.lowerQuartile = lowerQuartile;
		this.maximumRuntime = maximumRuntime;
		this.median = median;
		this.minimumRuntime = minimumRuntime;
		this.standardError = standardError;
		this.upperQuartile = upperQuartile;
		this.variance = variance;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordEntry other = (RecordEntry) obj;
		if (versionedMethodIdentifier == null) {
			if (other.versionedMethodIdentifier != null)
				return false;
		} else if (!versionedMethodIdentifier.equals(other.versionedMethodIdentifier))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((versionedMethodIdentifier == null) ? 0 : versionedMethodIdentifier.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RecordEntry [versionedMethodIdentifier=" + versionedMethodIdentifier + ", methodIdentifier=" + methodIdentifier + ", revision=" + revision + ", subject=" + subject + ", topic=" + topic + ", comment=" + comment + ", author=" + author + ", contact=" + contact + ", repeat=" + repeat + ", maximumStandardError=" + maximumStandardError + ", average=" + average + ", lowerQuartile=" + lowerQuartile + ", maximumRuntime=" + maximumRuntime + ", median=" + median + ", minimumRuntime="
				+ minimumRuntime + ", standardError=" + standardError + ", upperQuartile=" + upperQuartile + ", variance=" + variance + "]";
	}

}
