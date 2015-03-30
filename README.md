<h3 align="center">
JCorridore
</h3>

###### A proof of concept of a lightweight framework for testing that method's performances are inside a given performance corridore. If you want to **test** that your code runs **inside a certain range of performance** after updateing it, just write:
``` Java
@Test
@Record(samples = 50)    
@Constraint(allowedMedianDeviation = 10, samples = 15)
public void foo() {
  if (RuntimeConstraint.inject()) {
  	// Your regular test code goes here
  }
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

Before you can start, you will once need to 
* add `jcorridore.jar` to your project's **build path**
* add a file called `range.properties` to your **build path** which contains a path to the file in which *JCorridore* should store all the results. Assume you want to use `/temp/myproject.csv` as file, the content of `range.properties` is `store_records_path = /temp/myproject.csv`

Furthermore, *JCorridore* depends on the following libraries 
 * `commons-math3-3.4.1`, see [Apache Commons Math](http://commons.apache.org/proper/commons-math/download_math.cgi)
 * `junit-4.12`, see  [JUnit](http://www.http://junit.org)
 * `log4j-api-2.2` and log4j-core-2.2, see [Apache Log4J](http://logging.apache.org/log4j/2.x/)


From now on, you can add `@Record` and `@Constraint` annotations to a *JUnit* `@Test`annotated method. Please ensure to surround your regular code with 
```Java
if (RuntimeConstraint.inject()) {
	// your regular code goes here
}
```
in order to run recording and constraint checking automatically when *JUnit* runs the method.

**Example**:
``` Java
public class JCorridoreTest {
	
	private void someMethod() {
		final int upperBound = 100 + new Random().nextInt(1000000);	// <-- Here again the old performance					
		//...
	}
	
	@Test
	@Record(samples = 50)	
	@Constraint(allowedMedianDeviation = 10, samples = 50)
	public void testFoo() {		  
		if (RuntimeConstraint.inject()) {
			someMethod();             
		}
	}			                 

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

@Test	
@Record(samples = 50, revision = 2)										// <-- Reevaluate "testFoo" and check it's performance
@Constraint(allowedMedianDeviation = 10, samples = 50, revisionRef = 2)		//	   The constraint now also based on the second version
public void testFoo() {	
    	if (RuntimeConstraint.inject()) {
		// Call to a underlying method (e.g. Third Party)			
		someMethod();	
	}
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
Please note that *all* methods annotated with `@BeforeRecord` will be run for each Method annotated with `@Record`.

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
