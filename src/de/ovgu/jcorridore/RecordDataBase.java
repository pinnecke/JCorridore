package de.ovgu.jcorridore;

import java.util.List;

public interface RecordDataBase {
	
	public void storeRecord(RecordEntry record);
	
	public boolean containsRecordFor(String methodIdentifier);
	
	public List<RecordEntry> get(String methodIdentier);

}
