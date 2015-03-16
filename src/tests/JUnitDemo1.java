package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

// Shows a test, in which a method "testFoo" is tested by JUnit. This method has to pass the test
// within 10msec. That's mostly okay because it mostly runs less than 10msec. 
public class JUnitDemo1 {
	
	// This is just for simulating some calculations performed by 
	// a underlying component
	private void someMethod() {
		final int upperBound = 100 + new Random().nextInt(1000000);
		
		final List<Integer> list = new ArrayList<>();
		for (int i = 0; i < upperBound; i++)
			list.add(i);
	}	
	
	@Test (timeout = 10)
	public void testFoo() {
		// Call to a underlying method (e.g. Third Party)
		someMethod();		
	}

}
