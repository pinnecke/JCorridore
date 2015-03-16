# JCorridore

A proof of concept of a lightweight framework for testing that method's performances are inside a given performance corridore.


If you want to **test** that your code runs **inside a certain range of performance** after updateing it, just write:
``` Java
@Record(repeat = 50)    
@Constraint(allowedMedianDeviation = 10, repeat = 15)
public void foo() {
  // Insert code here
}  
```

-------
<p align="center">
    <a href="#motivation">Motivation</a> &bull; 
    <a href="#ensuring-performance-inside-a-corridore">Quick Start</a> &bull; 
    <a href="#additional-options">Additional options</a> &bull; 
    <a href="#license">License</a> &bull; 
    <a href="#contributing">Contributing</a>
</p>
-------

Get in contact with the developer on Twitter: [@marcus_pinnecke](https://twitter.com/marcus_pinnecke)

## Motivation
The following source code shows a test, in which a method `testFoo` is tested by *JUnit*. This method has to pass the test
within 10msec. That's mostly okay because it mostly runs less than 10msec. 
```java

public class RegularJUnitTest {
	
	// This is just for simulating some calculations performed by 
	// a underlying component
	private void someMethod() {
		final int upperBound = 100 + new Random().nextInt(1000000);    // <-- Performance variation
		
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
```
Because the test failed sometimes, the developers decide to increase the timeout to 100 times of the original
``` Java
  @Test (timeout = 10*100)	 // <-- increased timeout such that the test avails passes 
	public void testFoo() {
		// Call to a underlying method (e.g. Third Party)
		someMethod();		
	}
```

Unfortunately the method `testFoo` uses some method `someMethod` which are changed now and has a poor performance. Because the timeout limit is so high, no one notice that.
``` Java
  private void someMethod() {
		final int upperBound = 1000000 + new Random().nextInt(1000000);			// <-- poor performance
		
		final List<Integer> list = new ArrayList<>();
		for (int i = 0; i < upperBound; i++)
			list.add(i);
	}	
```

## Ensuring performance inside a corridore
Let's try it from another perspective. What's about measuring the regular runtime of the method
`testFoo` and compare each new test against the old performance results (with a kind of smoothness)? That's basically what **JCorridore** does for you.
``` Java
public class JCorridoreTest {
	
	// Just pretty printing
	private void prettryPrintIfFail(List<String> failedMethods) {
		if (!failedMethods.isEmpty()) System.err.println(String.join("\n", failedMethods));
	}
																	
	private void someMethod() {
		final int upperBound = 100 + new Random().nextInt(1000000);	// <-- Here again the old performance					
		//...
	}
	
	@Record(repeat = 50)	
	@Constraint(allowedMedianDeviation = 10, repeat = 50)
	public void testFoo() {		  // <-- Do not test "testFoo" any longer.
		someMethod();             //     Instead record it's performance over
	}			                  //	 e.g. 50 runs. If historic performance
								  //	 knowledge is available, create constraint
		                          //	 that "testFoo"'s performance is allowed
					              //	 to change around +/- 10ms for median (in this case)
	@Test
	public void testFooRuntimeInsideBounds() {			      // This is actually the test which checks
		List<String> failedMethods = new JCorridore(/*PATH*/, // if each method annotated with @Constraint
				/*FILENAME*/).run(JCorridoreTest.class);	  // is inside the given performance bounds
		prettryPrintIfFail(failedMethods);					  // If some methods will fail, just use
		Assert.assertEquals(0, failedMethods.size());		  // here quick and dirty pretty printing
	}														   // The test passes .

}
```
Once again, the underlying method `someMethod` decreases it's performance. Because this has an impact
of more than 10ms around the median performance, the test will fail.
``` Java
  private void someMethod() {
		final int upperBound = 1000000 + new Random().nextInt(1000000);	// <-- poor performance				
		//     the test fails with the message
	}	//	   "tests.JCorridoreTest:void testFoo() value of median (42.5) does not match 15.5 +/-10.0"
```	

## Additional options
### Resetting
If there is a mayor change such that the old knowledge about the performance does not help any longer a method's performance record can be reset (or set to a new version of that method).

``` Java
private void someMethod() {
		final int upperBound = 1000000 + new Random().nextInt(1000000);		// <-- The revision change will take this into account							
																			//	   and no longer that for the "high performance"
		final List<Integer> list = new ArrayList<>();							
		for (int i = 0; i < upperBound; i++)
			list.add(i);
}																		 
	
@Record(repeat = 50, revision = 2)										// <-- Reevaluate "testFoo" and check it's performance
@Constraint(allowedMedianDeviation = 10, repeat = 50, revision = 2)		//	   The constraint now also based on the second version
public void testFoo() {												
		// Call to a underlying method (e.g. Third Party)			
		someMethod();												
}
```
### Skipping
If you want to skip some method you can annotate this with `@Skip`.

### Initialization
Like *JUnit* it is possible to run several methods before the record/constraint phase.
``` Java
@BeforeRecord					// <-- Is called before any record/constraint
public void anotherMethod() {	//	   method. Here you can initialize field etc.
		// ...
}	
```

## Benchmarking methods
JCorridore measures 
* median and average
* lower quartile and upper quartile
* minimum and maximum runtime
* standard error
* variance

on each annotated method automatically and store this information inside a knowledge-base. It's easy to adapt this to use JCorridore also as handy benchmarking tool.

# License
This project is licensed under the terms of the GNU LESSER GENERAL PUBLIC LICENSE. See the LICENSE file.

# Contributing
1. Discuss about your idea in a new issue
2. Fork JCorridore (https://github.com/pinnecke/JCorridore)
3. Create your one new feature branch (git checkout -b my-feature)
4. Commit your changes (git commit -am 'Add some feature')
5. Push to the branch (git push origin my-feature)
6. Create a new Pull Request
