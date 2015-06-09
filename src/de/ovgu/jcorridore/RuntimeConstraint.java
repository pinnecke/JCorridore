package de.ovgu.jcorridore;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

public class RuntimeConstraint {

	final static Logger logger = LogManager.getLogger(RuntimeConstraint.class
			.getName());

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

		public Class<?> getCallerClass(final ClassLoader cl)
				throws ClassNotFoundException {
			return cl.loadClass(callerClassName);
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
			if (!Files.exists(Paths.get(fromPropertyFile))) {
				loadDefaultProperties(true);
				logger.warn("Unable to access file \"" + fromPropertyFile
						+ "\". Recording/Checking will run with file \""
						+ KEY_STORE_AT_DEFAULT + "\" in working directory.");
			} else {
				logger.debug("Property file:\"" + fromPropertyFile + "\"");

				try (FileReader reader = new FileReader(new File(
						fromPropertyFile))) {
					final Properties properties = new Properties();
					properties.load(reader);
					final String storeAt = (String) properties
							.get(KEY_STORE_AT);
					if (storeAt != null && new File(storeAt).canWrite()) {
						logger.debug("Database file:\"" + storeAt + "\"");
						storeRecordsPath = Paths.get(storeAt);
					} else {
						logger.warn("Database file not writable:\"" + storeAt
								+ "\"");
						loadDefaultProperties(true);
					}

				} catch (Exception e) {
					e.printStackTrace();
					logger.warn("Unable IOException for file \""
							+ fromPropertyFile
							+ "\". Recording/Checking will run with file \""
							+ KEY_STORE_AT_DEFAULT
							+ "\" in working directory. Message: "
							+ e.getMessage());
					loadDefaultProperties(true);
				}
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
			for (Method candidate : ci.getCallerClass(classLoader).getMethods()) {
				if (candidate.getName().equals(ci.callerMethod)) {
					logger.debug("Found method \"" + ci.callerMethod + "\"");
					return candidate;
				}
			}
		} catch (ClassNotFoundException e) {
			final String msg = STR_BAD_CLASS_NAME + "(" + ci.callerMethod + ")";
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		throw new RuntimeException(STR_BAD_METHOD);
	}

	private static CallerInfo getCaller() {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
		return new CallerInfo(ste.getClassName(), ste.getMethodName(),
				ste.getLineNumber(), ste.getFileName());
	}

	static ClassLoader classLoader;

	public static boolean inject(Class<?> clazz) {
		classLoader = clazz.getClassLoader();
		if (injectFlag) {
			if (++callCount > 1)
				throw new IllegalStateException();
			injectFlag = false;

			final Configuration config = loadConfiguration();
			final CallerInfo ci = getCaller();
			final Method m = findMethod(ci);

			try {
				runRuntimeConstraint(ci.getCallerClass(classLoader), m,
						config.storeRecordsPath);
			} catch (IllegalArgumentException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			injectFlag = true;
			callCount--;

		}
		return !injectFlag;
	}

	private static void runRuntimeConstraint(Class<?> classUnderTest,
			Method methodUnderTest, Path storeFilePath) {
		logger.info("Running on: " + classUnderTest.getName()
				+ ", method under test: " + methodUnderTest + ", file = "
				+ storeFilePath);

		List<String> failedMethods = new JCorridore(storeFilePath).run(
				classUnderTest, methodUnderTest);
		Assert.assertEquals(Utils.join("\n", failedMethods), 0,
				failedMethods.size());
	}

	private static Configuration loadConfiguration() {

		Configuration config = new Configuration();

		try {
			List<URL> propFileUrls = Collections.list(RuntimeConstraint.class
					.getClassLoader().getResources(FILE_CONFIG_FILENAME));
			if (!propFileUrls.isEmpty()) {
				final String configFile = propFileUrls.get(0).getFile();
				logger.info("Configuration file found: " + configFile);
				if (new File(configFile).canRead())
					config = new Configuration(configFile);
				else logger.info("Unable to read file (running local?): " + configFile);
			} else {
				logger.warn("Configuration file missing in class path. Default location for record database is used: "
						+ Configuration.KEY_STORE_AT_DEFAULT
						+ " at working directory.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return config;
	}

}
