package PersistenceManager;

import java.util.*;

public class Manager {
	
	private static final int WRITE = 0;
	private static final int COMMIT = 1;

    ArrayList <WriteReq> writeBuffer = new ArrayList<>();
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
    }

    public String beginTransaction(){
        String TAID = UUID.randomUUID().toString();
        return TAID;
    }

    public void write(WriteReq entry){
        List<WriteReq> thingsToBeAdded = new ArrayList<>();
        List<WriteReq> thingsToBeRemoved = new ArrayList<>();

        long ts = System.currentTimeMillis();
    	entry.setTs(ts);
    	int pageid= entry.getPageId();
        boolean exists= false;
        boolean entryIn=false;

        //System.out.println("Inserting write for page "+pageid+" into the Buffer.");
        if(writeBuffer.size()!=0){ // checking if the buffer is not empty to check for duplicate entries
            for(Iterator<WriteReq> w = writeBuffer.iterator(); w.hasNext(); ) {
                WriteReq value = w.next();
                if ((value.getPageId()==pageid) && (value.getEntryData().equals(entry.getEntryData()))) {   //checking if page already exists in the buffer
                    thingsToBeRemoved.add(value);                  //removing old entry
                    thingsToBeAdded.add(entry);       //adding new entry
                    LogEntry write_info = new LogEntry();
                    write_info.setOpType(WRITE);
                    write_info.setEntry(entry);
                    adm.add(write_info);
                    //break;
                    exists=true;
                }
            }

        } else {    //if empty write its first entry
            writeBuffer.add(entry);
            LogEntry write_info = new LogEntry();
            write_info.setOpType(WRITE);
            write_info.setEntry(entry);
            adm.add(write_info);
            entryIn=true;
        }
        //Not duplicate entry
        if(!exists && !entryIn){
            thingsToBeAdded.add(entry);
            LogEntry write_info = new LogEntry();
            write_info.setOpType(WRITE);
            write_info.setEntry(entry);
            adm.add(write_info);
        }

        if (thingsToBeAdded.size()>0 && thingsToBeRemoved.size()>0){
            writeBuffer.removeAll(thingsToBeRemoved);
            writeBuffer.addAll(thingsToBeAdded);
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

    public void commit(WriteReq entry){
        LogEntry commit_info = new LogEntry();
        commit_info.setOpType(COMMIT);
        commit_info.setEntry(entry);
        adm.add(commit_info);

        LogEntry log = new LogEntry();
        log.setEntry(entry);
        log.setOpType(COMMIT);

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
                        if (value.getOpType()==WRITE && value.getEntry().getTid().equals(tid)) {
                            WriteReq r= new WriteReq();
                            r.setEntryData(value.getEntry().getEntryData());
                            r.setTid(value.getEntry().getTid());
                            r.setPageId(value.getEntry().getPageId());
                            fs.writeToPage(r);
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
        for (LogEntry s:adm) {
            if (s.getOpType()==COMMIT){ //find all commits and store their indexes
                idx.add(adm.indexOf(s));
            }
        }
        return idx;
    }


}
