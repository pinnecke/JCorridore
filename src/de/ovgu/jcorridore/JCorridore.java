package de.ovgu.jcorridore;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ovgu.jcorridore.annotations.Constraint;

public class JCorridore {

	final static Logger logger = LogManager.getLogger(JCorridore.class
			.getName());

	private String filename;
	private Path path;

	public JCorridore(Path storeToFile) {
		this.path = storeToFile;
	}

	public List<String> run(Class<?> clazz, Method method) {

		List<String> failedMethods = new ArrayList<>();
		try {

			RecordResult rr = JCorridoreCore.runRecord(clazz, method);

			if (rr != null) {

				Object instance = clazz.newInstance();

				PlainTextRecordDataBase db = new PlainTextRecordDataBase(path);
				RecordEntry re = new RecordEntry(rr);

				logger.info("Running on \"" + re.versionedMethodIdentifier
						+ "\"...");

				if (!db.containsRecordFor(re.versionedMethodIdentifier)) {
					logger.info("For \"" + re.versionedMethodIdentifier
							+ "\" does not exists a database record currently.");
					db.storeRecord(re);
					db.save();
					// run(clazz, method);
					return new ArrayList<String>();
				} else {
					if (JCorridoreCore.checkConstraints(clazz, method)) {
						Constraint c = method.getAnnotation(Constraint.class);
						final String versionedMethodIdentifier = ReflectionUtils
								.makeMethodIdentifier(instance, method)
								+ "$"
								+ c.revisionReference();

						int lastSize = failedMethods.size();
						if (versionedMethodIdentifier
								.equals(re.versionedMethodIdentifier)) {

							logger.debug("Check constraints for \""
									+ re.versionedMethodIdentifier + "\"...");

							for (RecordEntry entry : db
									.get(re.versionedMethodIdentifier)) {
								test(rr.getAverage(),
										c.allowedAverageDeviation(),
										entry.average, "mean", failedMethods,
										re);
								test(rr.getLowerQuartile(),
										c.allowedLowerQuartileDeviation(),
										entry.lowerQuartile, "lower quartile",
										failedMethods, re);
								test(rr.getMaximumRuntime(),
										c.allowedMaximumDeviation(),
										entry.maximumRuntime,
										"maximum runtime", failedMethods, re);
								test(rr.getMedian(),
										c.allowedMedianDeviation(),
										entry.median, "median", failedMethods,
										re);
								test(rr.getMinimumRuntime(),
										c.allowedMinimumDeviation(),
										entry.minimumRuntime,
										"minimum runtime", failedMethods, re);
								test(rr.getStandardError(),
										c.allowedStandardError(),
										entry.standardError, "standard error",
										failedMethods, re);
								test(rr.getUpperQuartile(),
										c.allowedUpperQuartileDeviation(),
										entry.upperQuartile, "upper quartile",
										failedMethods, re);
								test(rr.getVariance(),
										c.allowedVarianceDeviation(),
										entry.variance, "variance",
										failedMethods, re);

								if (failedMethods.size() != lastSize) {
									logger.info("Method \""
											+ re.versionedMethodIdentifier
											+ "\" violates at least one constraint.");
									break;
								}
							}
						}
						if (failedMethods.size() == lastSize) {
							logger.info("Method \""
									+ re.versionedMethodIdentifier
									+ "\" passes.");
							db.storeRecord(re);
						}
					} else {
						logger.info("Method \"" + re.versionedMethodIdentifier
								+ "\" passes.");
						db.storeRecord(re);
					}

				}
				db.save();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return failedMethods;
	}

	private void test(double value, double epsilon, double referenceValue,
			String valueName, List<String> messages, RecordEntry currentEntry) {
		if (!(value <= referenceValue + epsilon && value >= referenceValue
				- epsilon))
			messages.add(currentEntry.methodIdentifier + " value of "
					+ valueName + " (" + value + ") does not match "
					+ referenceValue + " +/-" + epsilon
					+ ". Range has to be at least: +/- "
					+ Math.abs(referenceValue - value));
	}

}
