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
public class JUnitDemo4 {
	
	// Just pretty printing
	private void prettryPrintIfFail(List<String> failedMethods) {
		if (!failedMethods.isEmpty())
			System.err.println(String.join("\n", failedMethods));
	}
																	// This is just for simulating some calculations performed by 
	// a underlying component
	private void someMethod() {
		final int upperBound = 100 + new Random().nextInt(1000000);			// <-- Here again the old performance					
		
		final List<Integer> list = new ArrayList<>();
		for (int i = 0; i < upperBound; i++)
			list.add(i);
	}
	
	@Record(samples = 50)	
	@Constraint(allowedMedianDeviation = 10, samples = 50)
	public void testFoo() {											// <-- Do not test "testFoo" any longer.
		// Call to a underlying method (e.g. Third Party)			//     Instead record it's performance over
		someMethod();												//	   e.g. 10 runs. If historic performance
	}																//	   knowledge is available, create constraint
	
	//	   that "testFoo"'s performance is allowed
																	//	   to change around +/- 200ms for e.g. median
	@Test
	public void testFooRuntimeInsideBounds() {								//   <-- This is actually the test which checks
		List<String> failedMethods = new JCorridore("/Users/marcus/temp/",  //		 if each method annotated with @Constraint
				"stored_performances.db").run(JUnitDemo4.class);			//		 is inside the given performance bounds
		prettryPrintIfFail(failedMethods);									//		 If some methods will fail, just use
		Assert.assertEquals(0, failedMethods.size());						//		 here quick and dirty pretty printing
	}																		//		 The test passes 

}
