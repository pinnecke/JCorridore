package de.ovgu.jcorridore;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ovgu.jcorridore.annotations.BeforeRecord;
import de.ovgu.jcorridore.annotations.Constraint;
import de.ovgu.jcorridore.annotations.Record;
import de.ovgu.jcorridore.annotations.Skip;

public class JCorridoreCore {
	
	final static Logger logger = LogManager.getLogger(JCorridoreCore.class.getName());
	
	private static List<Method> extractAnnotatedMethods(Class<?> subject, Class<? extends Annotation> annotation) throws SecurityException {
		List<Method> result = new ArrayList<>();
		for (Method method : subject.getMethods()) {
				if (method.isAnnotationPresent(annotation)) {
					result.add(method);
				}
		}
		return result;
	}

	public static boolean checkConstraints(Class<?> subject, Method methodUnderTest) {
		List<Method> methodConstraints = extractAnnotatedMethods(subject, Constraint.class);
		return methodConstraints.contains(methodUnderTest);		
	}
	
	private static void runBeforeRecordMethods(Object instance, Collection<Method> beforeRecordMethods) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logger.debug("Run methods before recording...");
		
		for (Method m : beforeRecordMethods)
			m.invoke(instance);
	}

	public static RecordResult runRecord(Class<?> subject, Method method) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logger.debug("Running on " + subject.getName());
		
		final Collection<Method> beforeRecordMethods = extractAnnotatedMethods(subject, BeforeRecord.class);
		final List<Method> recordMethods = extractAnnotatedMethods(subject, Record.class);
		final List<Method> skipMethods = extractAnnotatedMethods(subject, Skip.class);
		
		logger.debug("Skipping " + skipMethods.size() + " annotated methods (@Skip)");
		
		recordMethods.removeAll(skipMethods);	
		beforeRecordMethods.removeAll(skipMethods);
		if (!recordMethods.contains(method))
			return null;
		
		Object instance = subject.newInstance();
		
		runBeforeRecordMethods(instance, beforeRecordMethods);
		
		return Recorder.createRecord(instance, method);
	}


}
