package PersistenceManager;

import java.util.*;

public class Manager {
	
	private static final int WRITE = 0;
	private static final int COMMIT = 1;

    ArrayList <WriteReq> writeBuffer;
    List<LogEntry> adm;

    static final private Manager singleton; 
    
    static {
        try {
            singleton = new Manager();
        }
        catch (Throwable e) {
            throw new RuntimeException(e.getMessage()); }
    }

    private Manager() {
        this.adm=new ArrayList<>();
        this.writeBuffer = new ArrayList<WriteReq>();
    }

    public String beginTransaction(){
        String TAID = UUID.randomUUID().toString();
        return TAID;
    }

    public void write(WriteReq entry){
        List<WriteReq> thingsToBeAdded = new ArrayList<WriteReq>();
        List<WriteReq> thingsToBeRemoved = new ArrayList<WriteReq>();

        long ts = System.currentTimeMillis();
    	entry.setTs(ts);
    	int pageid= entry.getPageId();
        boolean exists= false;
        boolean entryIn=false;

        //System.out.println("Inserting write for page "+pageid+" into the Buffer.");
        if(writeBuffer.size()!=0){ // checking if the buffer is not empty to check for duplicate entries
            for(Iterator<WriteReq> w = writeBuffer.iterator(); w.hasNext(); ) {
                WriteReq value = w.next();
                if (value != null){
                	if ((value.getPageId()==pageid) && (value.equals(entry))) {   //checking if page already exists in the buffer
	                    thingsToBeRemoved.add(value);                  //removing old entry
	                    thingsToBeAdded.add(entry);       //adding new entry
	                    LogEntry lg = new LogEntry();
	                    lg.setOpType(WRITE);
	                    lg.setEntry(entry);
	                    adm.add(lg);
	                    //break;
	                    exists=true;
                	 }
                }
            }

        } else {    //if empty write its first entry
            writeBuffer.add(entry);
            LogEntry lg = new LogEntry();
            lg.setOpType(WRITE);
            lg.setEntry(entry);
            adm.add(lg);
            entryIn=true;
        }
        //Not duplicate entry
        if(!exists && !entryIn){
            thingsToBeAdded.add(entry);
            LogEntry lg = new LogEntry();
            lg.setOpType(WRITE);
            lg.setEntry(entry);
            adm.add(lg);
        }

        if (thingsToBeAdded.size()>0){
            writeBuffer.addAll(thingsToBeAdded);
        }
        
        if (thingsToBeRemoved.size()>0){
            writeBuffer.removeAll(thingsToBeRemoved);
        }

    	LogEntry log = new LogEntry();
    	log.setEntry(entry);
    	log.setOpType(WRITE);

    	FileSystem fs = FileSystem.getInstance();
    	fs.writeToLog(log);

        if(writeBuffer.size()>5) {
             houseKeeping();
        }
    }

    public void commit(String tid){
        long ts = System.currentTimeMillis();
        LogEntry log = new LogEntry();
        WriteReq req = new WriteReq();
        req.setTid(tid);
        req.setTs(ts);
        log.setEntry(req);
        log.setOpType(COMMIT);
        
        adm.add(log);

        FileSystem fs = FileSystem.getInstance();
        fs.writeToLog(log);
    }

    static public Manager getInstance() {
        return singleton;
    }


    public void houseKeeping() {

        List<LogEntry> thingsToBeRemoved = new ArrayList<>();

        if (adm.size() > 5) {
            FileSystem fs = FileSystem.getInstance();

            //System.out.println("Crowed buffer of un-committed data");
            List<Integer> commit_idx;
            commit_idx = admCommits(adm);

            if(commit_idx.size()>0) {
                for (int i = 0; i < commit_idx.size(); i++) {
                    int idx = commit_idx.get(i); //commit index in adm list
                    LogEntry commit = adm.get(idx); //get the full commit object to extract the tid
                    String tid = commit.getEntry().getTid();

                    thingsToBeRemoved.add(adm.get(idx));

                    for(Iterator<LogEntry> s = adm.iterator(); s.hasNext(); ) {
                    	LogEntry value = s.next();
                        if ((value.getOpType() == WRITE) && value.getEntry().getTid().equals(tid)) {
                            fs.writeToPage(value.getEntry());
                            thingsToBeRemoved.add(value);
                        }
                    }

                }
            }

        }
        if(thingsToBeRemoved.size()>0)
            adm.removeAll(thingsToBeRemoved);
    }

    private List<Integer> admCommits(List<LogEntry> adm){
        List<Integer> idx =  new ArrayList<Integer>();
        for (Iterator<LogEntry> s = adm.iterator(); s.hasNext();) {
        	LogEntry value = s.next();
        	if (value != null){
        		if (value.getOpType() == COMMIT){ //find all commits and store their indexes
                idx.add(adm.indexOf(value));
        		}
        	}
            
        }
        return idx;
    }


}
