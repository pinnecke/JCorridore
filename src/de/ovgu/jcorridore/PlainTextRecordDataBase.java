package de.ovgu.jcorridore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PlainTextRecordDataBase implements RecordDataBase {

	private String filename;
	private Path fpath;
	Map<String, List<RecordEntry>> storedRecords = new HashMap<>();

	public PlainTextRecordDataBase(String path, String filename) {		
		fpath = Paths.get(path, filename);
		filename = (path.endsWith("/") ? path : path + "/") + filename;
		this.filename = filename;
		
		if (Files.exists(fpath)) {
			try {
				Stream<String> lines = Files.lines(fpath);
				lines.forEach(line -> importToDataBase(line));
				lines.close();
			} catch (IOException e) {
				e.printStackTrace();
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

		storeRecord(new RecordEntry(methodIdentifier, revision, subject, topic, comment, author, contact, repeat, maximumStandardError, average, lowerQuartile, maximumRuntime, median, minimumRuntime, standardError, upperQuartile, variance));
	}

	public void save() throws IOException  {
		if (Files.exists(fpath)) {
			File f = new File(filename);
			f.renameTo(new File(filename + ".old"));
		}
		FileWriter ps = new FileWriter(new File(filename));
		for (List<RecordEntry> re : storedRecords.values())
			for (RecordEntry e : re) {
				ps.write(toString(e) + "\n");
			}
		ps.close();
	}

	@Override
	public void storeRecord(RecordEntry record) {
		if (!storedRecords.containsKey(record.versionedMethodIdentifier)) {
			storedRecords.put(record.versionedMethodIdentifier, new ArrayList<>());
		}
		storedRecords.get(record.versionedMethodIdentifier).add(record);
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
		return entry.author.replace(";", ",") + ";" + entry.average + ";" + entry.comment.replace(";", ",") + ";" + entry.contact.replace(";", ",") + ";" + entry.lowerQuartile + ";" + entry.maximumRuntime + ";" + entry.maximumStandardError + ";" + entry.median + ";" + entry.methodIdentifier + ";" + entry.minimumRuntime + ";" + entry.repeat + ";" + entry.revision + ";" + entry.standardError + ";" + entry.subject + ";" + entry.topic + ";" + entry.upperQuartile + ";" + entry.variance;
	}

}
