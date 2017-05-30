package PersistenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RecoveryTool {
	
	private static final int WRITE = 0;
	private static final int COMMIT = 1;
	private static final int REDO = 2;

	ArrayList <WriteReq> winnerList = new ArrayList<WriteReq>();
	ArrayList <String> winnerTid = new ArrayList<String>();
	
	private void analyze()
	{
		String loc =  new File("").getAbsolutePath() + "/ps/log.txt";
		winnerTid.clear(); //clear the winner list
		winnerList.clear();
		
		FileSystem fs = FileSystem.getInstance();
		
		try (BufferedReader br = new BufferedReader(new FileReader(loc))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String[] items = line.split(" ");
		    	if (Integer.parseInt(items[1]) == COMMIT){
		    		// commit operation detected, save the TID
		    		winnerTid.add(items[2]);
		    	}
		    	else if (Integer.parseInt(items[1]) == REDO){
		    		winnerTid.clear(); // clear the accumulated list so far because those entries have been redone already
		    	}
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(loc))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String[] items = line.split(" ");
		    	for (String winnerTaid : winnerTid){
		    		if ((Integer.parseInt(items[1]) == WRITE) && winnerTaid.equals(items[2])){
		    			// winner TA detected
		    			WriteReq entry = new WriteReq();
		    			entry.setTs(Long.parseLong(items[0]));
		    			entry.setTid(items[2]);
		    			entry.setPageId(Integer.parseInt(items[3]));
		    			entry.setEntryData(items[4]);
		    			winnerList.add(entry);
		    		}
		    	}
		    	
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void redo()
	{
		File page;
		String pageLoc = new File("").getAbsolutePath() + "/fs";
		
		analyze();
		
		FileSystem fs = FileSystem.getInstance();
		for (WriteReq redoWr : winnerList){
	        //for each winner transaction open the corresponding page
			String pageFile = pageLoc + "/PAGE" + redoWr.getPageId() + ".txt";
			page = new File(pageFile);
			try (BufferedReader br = new BufferedReader(new FileReader(page))) {
			    String line;
			    if ((line = br.readLine()) != null) {
			    	// if some data is already present, check the TS
			    	String[] items = line.split(" ");
			    	if (Long.parseLong(items[0]) < redoWr.getTs()){
			    		// the data in the page is out of date, rewrite
			    		fs.writeToPage(redoWr);
			    	}
			    }
			    else{
			    	// no data was written before the fault, fill in the page
			    	fs.writeToPage(redoWr);
			    }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		// create a REDO entry in the log
		LogEntry log = new LogEntry();
		log.setOpType(REDO);
		WriteReq wrR = new WriteReq();
		wrR.setTs(System.currentTimeMillis());
		log.setEntry(wrR);
		fs.writeToLog(log);
		
		// clear the redo lists
		winnerTid.clear(); // winner TA ID list
		winnerList.clear(); // winner transactions
		
	}
}
