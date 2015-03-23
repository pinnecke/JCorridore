package de.ovgu.jcorridore;

import static de.ovgu.jcorridore.StringTable.BAD_REPEAT_COUNT;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import de.ovgu.jcorridore.annotations.Record;

public class Recorder {

	public static RecordResult createRecord(Object instance, Method method) {
		final Record recordAnnotation = method.getAnnotation(Record.class);
		final String methodIdentifer = ReflectionUtils.makeMethodIdentifier(instance, method);
		final RecordResult result = new RecordResult(recordAnnotation, methodIdentifer);

		final int repeat = recordAnnotation.samples();
		
		if (repeat < 0) {
			result.setException(new IllegalArgumentException(BAD_REPEAT_COUNT));
		} else {
			final double[] measurements = new double[repeat];
			
			for (int i = 0; i < repeat; i++) {				
				try {
					long start = System.currentTimeMillis();
					method.invoke(instance);
					long elapsed = System.currentTimeMillis() - start;
					measurements[i] = elapsed;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					result.setException(e);
				}				
			}
			
			DescriptiveStatistics statistics = new DescriptiveStatistics(measurements);		
					
			result.minimumRuntime = statistics.getMin();
			result.lowerQuartile = statistics.getPercentile(25);
			result.median = statistics.getPercentile(50);
			result.upperQuartile = statistics.getPercentile(75);
			result.average = statistics.getMean();
			result.maximumRuntime = statistics.getMax();
			result.variance = statistics.getVariance();
			result.standardError = Math.sqrt(statistics.getVariance());
			result.timestamp = System.currentTimeMillis();
			
		}

		return result;
	}

}
