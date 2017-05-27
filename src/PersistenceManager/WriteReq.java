package PersistenceManager;

public class WriteReq {
	
	private String tid;
	private int pageId;
	private String entryData;
	private long ts;
	
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public String getEntryData() {
		return entryData;
	}
	public void setEntryData(String entryData) {
		this.entryData = entryData;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}

}
