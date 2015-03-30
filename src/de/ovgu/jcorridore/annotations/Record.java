package de.ovgu.jcorridore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Record {

	public @interface MyRecord2 {

	}

	public static class NoSubject {
		public NoSubject() {
			
		}
	}
	
	/**
	 * Adds a author information about this method.
	 */
	String author() default "";
	
	
	/**
	 * Adds a comment to the first run of this method.
	 */
	String comment() default "";
	
	/**
	 * Adds a contact information about to method.
	 */
	String contact() default "";
	
	
	/**
	 * Specify how often this method is rerun before a stable result is expected and reported for the
	 * template generator for the current revision.
	 */
	float maximumStandardError() default Float.MAX_VALUE;
	
	/**
	 * Specify how often this method is rerun before a stable result is expected and reported for the
	 * template generator for the current revision.
	 */
	int samples();
	
	int revision() default 1;
	
	/**
	 * The class which contains the method to be measured. This could be different from the class
	 * where the benchmark actually starts. Imaging a JUnit class A where a method tests the performance
	 * of another class B. Class B should be the subject.
	 */	
	Class<?> subject() default NoSubject.class;
	
	/**
	 * Specify the amount of milliseconds before the method measurement is skipped. This 
	 * @return
	 */	
	int timeOut() default Integer.MAX_VALUE;
	
	/**
	 * The topic is a meta information to provide a filtering over a bunch of methods in order
	 * to cluster methods in a semantic style.
	 */	
	String topic() default "default";

	int historySize() default 20;
}
