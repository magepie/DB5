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

	public FileSystem(){
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
	}
	
	public void writeToFs(int pageIndex, int id, String data)
	{
		String loc =  new File("").getAbsolutePath() + "/fs/PAGE" + pageIndex + ".txt";
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(loc, true)))) {
		    out.println(id + " " + data);
		}catch (IOException e) {
		    System.err.println(e);
		}
	}
	
	
	
}
