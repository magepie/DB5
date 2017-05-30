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
        WriteReq[] writeBuffer_backup =writeBuffer.toArray(new WriteReq[writeBuffer.size()]);

        long ts = System.currentTimeMillis();
    	entry.setTs(ts);
    	int pageid= entry.getPageId();
        boolean exists= false;
        boolean entryIn=false;
        int buffersize=writeBuffer.size();

        System.out.println("Inserting write for page "+pageid+" into the Buffer. "+entry.getTid());
        if(buffersize!=0){ // checking if the buffer is not empty to check for duplicate entries
            for(WriteReq w : writeBuffer_backup) {
              //  System.out.println("w.pageId "+w.getPageId()+" page id "+pageid+" w.getEntryData "+w.getEntryData()+" entry.Data "+entry.getEntryData());
                if ((w.getPageId()==pageid) && (w.getEntryData().equals(entry.getEntryData()))) {   //checking if page already exists in the buffer
                    thingsToBeRemoved.add(w);                  //removing old entry
                    thingsToBeAdded.add(entry);       //adding new entry
                    LogEntry write_info = new LogEntry();
                    write_info.setOpType(WRITE);
                    write_info.setEntry(entry);
                    adm.add(write_info);
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

        if (thingsToBeAdded.size()>0)
            writeBuffer.addAll(thingsToBeAdded);

        if(thingsToBeRemoved.size()>0)
            writeBuffer.removeAll(thingsToBeRemoved);

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
        LogEntry[] adm_backup=adm.toArray(new LogEntry[adm.size()]);

        int adm_size= adm.size();

        if (adm_size > 5) {
            FileSystem fs = FileSystem.getInstance();

            //System.out.println("Crowed buffer of un-committed data");
            List<Integer> commit_idx;
            commit_idx = admCommits(adm_backup);

            if(commit_idx.size()>0) {
                for (int i = 0; i < commit_idx.size(); i++) {
                    int idx = commit_idx.get(i); //commit index in adm list
                    LogEntry commit = Arrays.asList(adm_backup).get(idx); //get the full commit object to extract the tid
                    String tid = commit.getEntry().getTid();

                    thingsToBeRemoved.add(Arrays.asList(adm_backup).get(idx));

                    for(LogEntry s : adm_backup) {
                        if (s.getOpType()==WRITE && s.getEntry().getTid().equals(tid)) {
                            WriteReq r= new WriteReq();
                            r.setEntryData(s.getEntry().getEntryData());
                            r.setTid(s.getEntry().getTid());
                            r.setPageId(s.getEntry().getPageId());
                            fs.writeToPage(r);
                            thingsToBeRemoved.add(s);
                        }
                    }

                }
            }

        }
        if(thingsToBeRemoved.size()>0)
            adm.removeAll(thingsToBeRemoved);
    }

    private List<Integer> admCommits(LogEntry[] adm){
        List<Integer> idx =  new ArrayList<Integer>();
        for (LogEntry s : adm) {
            if (s.getOpType()==COMMIT){ //find all commits and store their indexes
                int index= Arrays.asList(adm).indexOf(s);
                idx.add(index);
            }
        }
        return idx;
    }


}
