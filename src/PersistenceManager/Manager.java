package PersistenceManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Manager {
	
	private static final int WRITE = 0;
	private static final int COMMIT = 1;
	
    String fs_dir="fs/";
    String logLocation = new File("").getAbsolutePath() + "/ps/";
    File LOG = new File("");
    
    ArrayList <WriteReq> writeBuffer = new ArrayList<WriteReq>();
    
    static final private Manager singleton; 
    
    static {
        try {
            singleton = new Manager();
        }
        catch (Throwable e) {
            throw new RuntimeException(e.getMessage()); }
    }

    private Manager() {}

    public String beginTransaction(){
        String TAID = UUID.randomUUID().toString();
        return TAID;
    }

    public void write(WriteReq entry){
/*        File page=new File(fs_dir+pageid+".txt");
        
        if(page.exists()){
            try {
                FileWriter fw= new FileWriter(page);
                fw.write(taid+"\n"+data);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else System.out.println("Page with id "+pageid+" does not exist..");*/
    	
    	long ts = System.currentTimeMillis();
    	entry.setTs(ts);
    	
    	writeBuffer.add(entry);
    	
    	LogEntry log = new LogEntry();
    	log.setEntry(entry);
    	log.setOpType(WRITE);
    	
    	FileSystem fs = FileSystem.getInstance();
    	fs.writeToLog(log);
    }

    public void commit(String taid){}

    static public Manager getInstance() {
        return singleton;
    }
}
