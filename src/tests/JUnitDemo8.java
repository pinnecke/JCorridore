package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import de.ovgu.jcorridore.JCorridore;
import de.ovgu.jcorridore.annotations.BeforeRecord;
import de.ovgu.jcorridore.annotations.Constraint;
import de.ovgu.jcorridore.annotations.Record;

// Shows a test, in which a method "testFoo" is tested by JUnit. This method has to pass the test
// within 10msec. That's mostly okay because it mostly runs less than 10msec. 

// Because the test sometimes failed, the working group decides to increase the timeout to 100 times
// of the original

// Unfortunately the method "testFoo" uses some method "someMethod" which are changed now and has
// a poor performance. Because the timeout limit is so high, no one notice that.

// Let's try it from another perspective. What's about measuring the regular runtime of the method
// "testFoo" and compare each new test against the old performance results (with a kind of smoothness)?

// Once again, the underlying method "someMethod" decreases it's performance. Because this has an impact
// of more than 10ms around the median performance, the test will fail.

// If there is a mayor change such that the old knowledge about the performance does not help any longer
// a method's performance record can be reset (or set to a new version of that method)

// If there are more than one method, you can skip some

// Like JUnit it is possible to run several methods before the record/constraint phase
public class JUnitDemo8 {
	
	private boolean condition = false;
	
	// Just pretty printing
	private void prettryPrintIfFail(List<String> failedMethods) {
		if (!failedMethods.isEmpty())
			System.err.println(String.join("\n", failedMethods));
	}
	
	@BeforeRecord															// <-- Is called before any record/constraint
	public void setCondition() {											//	   method. Here you can initialize field etc.
		condition = true;
	}	
																	
																	
	// This is just for simulating some calculations performed by 
	// a underlying component
	private void someMethod() {
		final int upperBound = 10000 + new Random().nextInt(1000000);									
																			
		final List<Integer> list = new ArrayList<>();							
		for (int i = 0; i < upperBound; i++)
			list.add(i);
	}																		 
	
	@Record(repeat = 50, revision = 2)										
	@Constraint(allowedMedianDeviation = 10, repeat = 50, revision = 2)		
	public void testFoo2() {												
		// Call to a underlying method (e.g. Third Party)	
		if (condition)
			someMethod();												
	}
	
	@Test
	public void testFooRuntimeInsideBounds() {								
		List<String> failedMethods = new JCorridore("/Users/marcus/temp/",  
				"stored_performances.db").run(JUnitDemo8.class);			
		prettryPrintIfFail(failedMethods);									
		Assert.assertEquals(0, failedMethods.size());						
	}

}
