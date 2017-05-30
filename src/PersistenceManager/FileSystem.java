package PersistenceManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileSystem {
	
	private static final int PAGE1_lo = 10;
	private static final int PAGE_N = 10;
	private static final int PAGE_BLOCK_N = 5;
	
    static final private FileSystem singleton; 
    
    static {
        try {
            singleton = new FileSystem();
            singleton.init();
        }
        catch (Throwable e) {
            throw new RuntimeException(e.getMessage()); }
    }

    protected void init() {
		File f;
		String loc =  new File("").getAbsolutePath() + "/fs/";
		for (int i = 0; i < PAGE_BLOCK_N; i++) // for each page block
		{
			for (int j = 0; j < PAGE_N; j++)
			{
				f = new File(loc + "/PAGE" + (int)((PAGE1_lo * (i + 1))/10) + j + ".txt");
				if(!f.exists()) { // create the file if it doesn't exist 
					try {
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		loc = new File("").getAbsolutePath() + "/ps/";
		f = new File(loc);
		if(f.isDirectory() && f.list().length == 2)
		{
			f = new File (loc + "/log.txt");
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    public void writeToPage(WriteReq wrRequest)
	{
    	String loc =  new File("").getAbsolutePath() + "/fs/PAGE" + wrRequest.getPageId() + ".txt";
    	File page = new File(loc);
    	try {
    		FileWriter writer = new FileWriter(page, false);
    		writer.write(wrRequest.getTs() + " " + wrRequest.getEntryData());
    		writer.close();
		}catch (IOException e) {
		    System.err.println(e);
		}
	}
	
	public void writeToLog(LogEntry logData)
	{
		String loc =  new File("").getAbsolutePath() + "/ps/log.txt";
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(loc, true)))) {
			if (logData.getOpType() == 0) { // write entry
				out.println(logData.getEntry().getTs() + " " + logData.getOpType() + " " + logData.getEntry().getTid() + " " + logData.getEntry().getPageId()+ " " + logData.getEntry().getEntryData());
			}
			else if (logData.getOpType() == 1){ // commit entry
				out.println(logData.getEntry().getTs() + " " + logData.getOpType() + " " + logData.getEntry().getTid());
			}
			else if (logData.getOpType() == 2){ // redo entry
				out.println(logData.getEntry().getTs() + " " + logData.getOpType());
			}
		}catch (IOException e) {
		    System.err.println(e);
		}
	}
	
	static public FileSystem getInstance() {
        return singleton;
    }
	
	
}
