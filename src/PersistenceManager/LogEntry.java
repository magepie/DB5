package PersistenceManager;

public class LogEntry {
	
	private WriteReq entry;
	private int opType;
	private String taid;

	public WriteReq getEntry() {
		return entry;
	}

	public void setEntry(WriteReq entry) {
		this.entry = entry;
	}
	public String getTaid() {return taid; }
	public void setTaid(String taid){this.taid=taid;}

	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}
}
