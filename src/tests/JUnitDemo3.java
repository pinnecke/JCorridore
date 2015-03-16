package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

// Shows a test, in which a method "testFoo" is tested by JUnit. This method has to pass the test
// within 10msec. That's mostly okay because it mostly runs less than 10msec. 

// Because the test sometimes failed, the working group decides to increase the timeout to 100 times
// of the original

// Unfortunately the method "testFoo" uses some method "someMethod" which are changed now and has
// a poor performance. Because the timeout limit is so high, no one notice that.
public class JUnitDemo3 {
	
	// This is just for simulating some calculations performed by 
	// a underlying component
	private void someMethod() {
		final int upperBound = 1000000 + new Random().nextInt(1000000);			// <-- poor performance
		
		final List<Integer> list = new ArrayList<>();
		for (int i = 0; i < upperBound; i++)
			list.add(i);
	}	
	
	@Test (timeout = 10*100)											 
	public void testFoo() {
		// Call to a underlying method (e.g. Third Party)
		someMethod();		
	}

}
