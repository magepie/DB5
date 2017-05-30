package PersistenceManager;

public class LogEntry {
	
	private WriteReq entry;
	private int opType;
	
	public WriteReq getEntry() {
		return entry;
	}

	public void setEntry(WriteReq entry) {
		this.entry = entry;
	}

	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}
}
