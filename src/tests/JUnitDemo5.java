package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import de.ovgu.jcorridore.JCorridore;
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
public class JUnitDemo5 {
	
	// Just pretty printing
	private void prettryPrintIfFail(List<String> failedMethods) {
		if (!failedMethods.isEmpty())
			System.err.println(String.join("\n", failedMethods));
	}																
																	
																	
	// This is just for simulating some calculations performed by 
	// a underlying component
	private void someMethod() {
		final int upperBound = 1000000 + new Random().nextInt(1000000);			// <-- poor performance				
																				//     the test fails with the message
		final List<Integer> list = new ArrayList<>();							//	   "tests.JUnitDemo5:void testFoo() value of median (42.5) does not match 15.5 +/-10.0"
		for (int i = 0; i < upperBound; i++)
			list.add(i);
	}																		 
	
	@Record(samples = 50)	
	@Constraint(allowedMedianDeviation = 10, samples = 50)
	public void testFoo() {											
		// Call to a underlying method (e.g. Third Party)			
		someMethod();												
	}
	
	@Test
	public void testFooRuntimeInsideBounds() {								
		List<String> failedMethods = new JCorridore("/Users/marcus/temp/",  
				"stored_performances.db").run(JUnitDemo5.class);			
		prettryPrintIfFail(failedMethods);									
		Assert.assertEquals(0, failedMethods.size());						
	}

}
