package de.ovgu.jcorridore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Charsets;

public class PlainTextRecordDataBase implements RecordDataBase {

	final static Logger logger = LogManager
			.getLogger(PlainTextRecordDataBase.class.getName());

	private Path fpath;
	Map<String, List<RecordEntry>> storedRecords = new HashMap<>();

	public PlainTextRecordDataBase(Path filePath) {
		fpath = filePath;

		if (Files.exists(fpath)) {
			try {
				for (String line : Files.readAllLines(fpath, Charsets.UTF_8)) {
					logger.info("Loading records from \"" + fpath.toUri()
							+ "\"...");
					importToDataBase(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Failed loading records from \"" + fpath.toUri()
						+ "\", message: " + e.getMessage());
			}
		}
	}

	@Override
	public boolean containsRecordFor(String versionedMethodIdentifier) {
		return storedRecords.containsKey(versionedMethodIdentifier);
	}

	@Override
	public List<RecordEntry> get(String versionedMethodIdentifier) {
		return storedRecords.get(versionedMethodIdentifier);
	}

	private void importToDataBase(String line) {
		String[] components = line.split(";");
		String methodIdentifier = components[8];
		int revision = Integer.valueOf(components[11]);
		String subject = components[13];
		String topic = components[14];
		String comment = components[2];
		String author = components[0];
		String contact = components[3];
		int repeat = Integer.valueOf(components[10]);
		int maximumStandardError = Integer.valueOf(components[6]);
		double average = Double.valueOf(components[1]);
		double lowerQuartile = Double.valueOf(components[4]);
		double maximumRuntime = Double.valueOf(components[5]);
		double median = Double.valueOf(components[7]);
		double minimumRuntime = Double.valueOf(components[9]);
		double standardError = Double.valueOf(components[12]);
		double upperQuartile = Double.valueOf(components[15]);
		double variance = Double.valueOf(components[16]);
		long timestamp = Long.valueOf(components[17]);
		int historySize = Integer.valueOf(components[18]);

		storeRecord(new RecordEntry(methodIdentifier, revision, subject, topic,
				comment, author, contact, repeat, maximumStandardError,
				average, lowerQuartile, maximumRuntime, median, minimumRuntime,
				standardError, upperQuartile, variance, timestamp, historySize));
	}

	public void save() throws IOException {
		FileWriter writer = new FileWriter(fpath.toFile());
		for (List<RecordEntry> recordEntryList : storedRecords.values())
			for (RecordEntry singleEntry : recordEntryList) {
				writer.write(toString(singleEntry) + "\n");
			}
		writer.close();

		logger.info("Saved data base to \"" + fpath.toUri());
	}

	@Override
	public void storeRecord(RecordEntry record) {
		if (!storedRecords.containsKey(record.versionedMethodIdentifier)) {
			storedRecords.put(record.versionedMethodIdentifier,
					new ArrayList<RecordEntry>());
		}
		storedRecords.get(record.versionedMethodIdentifier).add(record);

		logger.debug("Added \"" + record.versionedMethodIdentifier
				+ "\" to database (in memory).");

		cleanRecordHistory(record);
	}
	
	static Comparator<RecordEntry> RECORD_ENTRY_COMPARATOR = new Comparator<RecordEntry>() {
		
		@Override
		public int compare(RecordEntry o1, RecordEntry o2) {
			return Long.valueOf(o1.timestamp).compareTo(Long.valueOf(o2.timestamp));
		}
	};

	private void cleanRecordHistory(RecordEntry record) {
		if (storedRecords.get(record.versionedMethodIdentifier).size() > record.historySize) {
			logger.info("Method history for \"" + record.versionedMethodIdentifier + "\" will be cleaned.");
			List<RecordEntry> recordHistory = storedRecords.get(record.versionedMethodIdentifier);			
			Collections.sort(recordHistory, RECORD_ENTRY_COMPARATOR);
			
			
			
			while (recordHistory.size() > record.historySize) {
				logger.info("Removed record from date \"" + record.timestamp);
				recordHistory.remove(0);
			}
			storedRecords.put(record.versionedMethodIdentifier, recordHistory);
		}
		
	}

	@Override
	public String toString() {
		String s = "";
		for (String id : storedRecords.keySet()) {
			s += "\n" + id + "\n";
			for (RecordEntry e : storedRecords.get(id)) {
				s += "\t" + e.toString() + "\n";
			}
		}
		return s;
	}

	private String toString(RecordEntry entry) {
		return entry.author.replace(";", ",") + ";" + entry.average + ";"
				+ entry.comment.replace(";", ",") + ";"
				+ entry.contact.replace(";", ",") + ";" + entry.lowerQuartile
				+ ";" + entry.maximumRuntime + ";" + entry.maximumStandardError
				+ ";" + entry.median + ";" + entry.methodIdentifier + ";"
				+ entry.minimumRuntime + ";" + entry.repeat + ";"
				+ entry.revision + ";" + entry.standardError + ";"
				+ entry.subject + ";" + entry.topic + ";" + entry.upperQuartile
				+ ";" + entry.variance + ";" + entry.timestamp + ";"
				+ entry.historySize;
	}

}
