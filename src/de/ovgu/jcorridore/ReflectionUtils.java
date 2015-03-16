package de.ovgu.jcorridore;

import java.lang.reflect.Method;

public class ReflectionUtils {

	public static String makeMethodIdentifier(Object instance, Method method) {
		return String.format("%s:%s", instance.getClass().getName(), ReflectionUtils.toString(method));
	}

	public static String toString(Class<?>[] parameterTypes) {
		if (parameterTypes.length == 0)
			return "";
		else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < parameterTypes.length - 1; i++)
				sb.append(parameterTypes[i] + ", ");
			sb.append(parameterTypes[parameterTypes.length - 1] + ", ");
			return sb.toString().trim();
		}
	}

	public static String toString(final Method method) {
		return method.getReturnType() + " " + method.getName() + "(" + toString(method.getParameterTypes()) + ")";
	}

}
