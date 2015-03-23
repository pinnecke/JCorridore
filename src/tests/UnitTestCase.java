package tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.ovgu.jcorridore.JCorridore;
import de.ovgu.jcorridore.annotations.BeforeRecord;
import de.ovgu.jcorridore.annotations.Constraint;
import de.ovgu.jcorridore.annotations.Record;

public class UnitTestCase {

	int maxLimit;

	@BeforeRecord
	public void init() {
		maxLimit = 100;
	}

	private String print(List<String> failedMethods) {
		return Utils.join("\n", failedMethods);
	}

	@Record(samples = 10, revision=1)
	@Constraint(allowedMaximumDeviation = 100, allowedMinimumDeviation = 100, samples = 10, revisionReference=1)
	public void run1() {
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < maxLimit; i++)
			l.add(i);
	}

	@Record(samples = 100, revision=1)
	@Constraint(samples = 10, revisionReference=1)
	public void run2() {
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < 300000; i++)
			l.add(i);
	}

	@Test
	public void testRuntimeInCorridore() {
		List<String> failedMethods = new JCorridore("/Users/marcus/temp/", "result.db").run(UnitTestCase.class);
		if (!failedMethods.isEmpty())
			System.err.println(print(failedMethods));
		Assert.assertEquals(0, failedMethods.size());
	}

	

}
