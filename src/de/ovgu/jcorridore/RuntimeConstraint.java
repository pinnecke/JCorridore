package de.ovgu.jcorridore;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;

import de.ovgu.jcorridore.JCorridore;

public class RuntimeConstraint {

	public static class CallerInfo {

		final String callerClassName;

		final String callerFileName;

		final int callerLineNumber;

		final String callerMethod;

		public CallerInfo(String callerClassName, String callerMethod,
				int callerLineNumber, String callerFileName) {
			this.callerClassName = callerClassName;
			this.callerMethod = callerMethod;
			this.callerLineNumber = callerLineNumber;
			this.callerFileName = callerFileName;
		}

		public Class<?> getCallerClass() throws ClassNotFoundException {
			return ClassLoader.getSystemClassLoader()
					.loadClass(callerClassName);
		}

		public String getCallerFileName() {
			return callerFileName;
		}

		public int getCallerLineNumber() {
			return callerLineNumber;
		}

		public String getCallerMethod() {
			return callerMethod;
		}

		@Override
		public String toString() {
			return "CallerInfo [callerClass=" + callerClassName
					+ ", callerMethod=" + callerMethod + ", callerLineNumber="
					+ callerLineNumber + ", callerFileName=" + callerFileName
					+ "]";
		}

	}

	private static class Configuration {

		private static final String KEY_STORE_AT = "store_records_path";

		private static final String KEY_STORE_AT_DEFAULT = "stored_records.csv";

		private Path storeRecordsPath;

		public Configuration() {
			loadDefaultProperties(false);
		}

		public Configuration(final String fromPropertyFile) {

			try (FileReader reader = new FileReader(new File(fromPropertyFile))) {
				final Properties properties = new Properties();
				properties.load(reader);
				final String storeAt = (String) properties.get(KEY_STORE_AT);
				if (storeAt != null || !Files.exists(Paths.get(storeAt)))
					storeRecordsPath = Paths.get(storeAt);
				else
					loadDefaultProperties(true);

			} catch (Exception e) {
				e.printStackTrace();
				loadDefaultProperties(true);
			}
		}

		public Path getStoreRecordsPath() {
			return storeRecordsPath;
		}

		private void loadDefaultProperties(boolean warning) {
			if (warning)
				System.err.println("Unable to locate \"" + KEY_STORE_AT_DEFAULT
						+ "\" in class path. Load default properties now.");
			storeRecordsPath = Paths.get(KEY_STORE_AT_DEFAULT);
		}
	}

	public static int callCount = 0;

	private static String FILE_CONFIG_FILENAME = "range.properties";

	public static boolean injectFlag = true;

	private static String STR_BAD_CLASS_NAME = "Unable to load class via class loader.";

	private static String STR_BAD_METHOD = "Unable to find requested method.";

	private static Method findMethod(final CallerInfo ci) {
		try {
			for (Method candidate : ci.getCallerClass().getMethods()) {
				System.out.println(candidate.getName());
				if (candidate.getName().equals(ci.callerMethod)) {
					return candidate;
				}
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(STR_BAD_CLASS_NAME);
		}
		throw new RuntimeException(STR_BAD_METHOD);
	}
	private static CallerInfo getCaller() {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
		return new CallerInfo(ste.getClassName(), ste.getMethodName(),
				ste.getLineNumber(), ste.getFileName());
	}

	public static boolean inject() {
		if (injectFlag) {
			if (++callCount > 1)
				throw new IllegalStateException();
			injectFlag = false;
			
			final Configuration config = loadConfiguration();
			final CallerInfo ci = getCaller();
			final Method m = findMethod(ci);

			try {
				runRuntimeConstraint(ci.getCallerClass(), m, config.storeRecordsPath);
				m.invoke(ci.getCallerClass().newInstance(), new Object[0]);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | InstantiationException
					| ClassNotFoundException e) {
				e.printStackTrace();
			}
			injectFlag = true;
			callCount--;

		}
		return !injectFlag;
	}

	private static void runRuntimeConstraint(Class<?> classUnderTest, Method methodUnderTest, Path storeFilePath) {
		List<String> failedMethods = new JCorridore(storeFilePath).run(classUnderTest, methodUnderTest);			
		prettryPrintIfFail(failedMethods);									
		Assert.assertEquals(0, failedMethods.size());		
	}
	
	// Just pretty printing
		private static void prettryPrintIfFail(List<String> failedMethods) {
			if (!failedMethods.isEmpty())
				System.err.println(Utils.join("\n", failedMethods));
		}
		
	private static Configuration loadConfiguration() {

		Configuration config = new Configuration();

		try {
			List<URL> propFileUrls = Collections.list(RuntimeConstraint.class
					.getClassLoader().getResources(FILE_CONFIG_FILENAME));
			if (!propFileUrls.isEmpty())
				config = new Configuration(propFileUrls.get(0).getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return config;
	}

}
