package PersistenceManager;
import java.util.concurrent.ThreadLocalRandom;

public class Client extends Thread{
	private String name;
	private int taid;
	private int[] pageRange=new int[2];

	public Client(String name, int pageRange[]){
		this.name=name;
		this.pageRange=pageRange;
	}
	
	public void run(){
		//Manager persistentManager= Manager.getInstance();
		WriteReq entry = new WriteReq();
		Manager persistentManager = Manager.getInstance();
		System.out.println("Client with name "+name+" has been initiated!");
		while(true){
			String t_id= persistentManager.beginTransaction();
			entry.setTid(t_id);
			System.out.println("Transaction initiated with transaction id " + entry.getTid());
			entry.setEntryData("Test data");
			entry.setPageId(ThreadLocalRandom.current().nextInt(this.pageRange[0], this.pageRange[1] + 1));
			persistentManager.write(entry);
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){
				return;
			}
		}
	}

	public void setTaid(int taid){
		this.taid = taid;
	}

	public int getTaid(){
		return taid;
	}

	public void setPageRange(int[] pageRange){
		this.pageRange=pageRange;
	}

	public int[] getPageRange(){
		return pageRange;
	}
}
