package PersistenceManager;

public class Client extends Thread{
	String name;
	String taid;
	int[] pageRange=new int[2];

	public Client(String name, int pageRange[]){
		this.name=name;
		this.pageRange=pageRange;
	}
	public void run(){
		while(true){
			//System.out.println("Client with name "+name+" has been initiated!");
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){
				return;
			}
		}
	}

	public void setTaid(String taid){
		this.taid = taid;
	}

	public String getTaid(){
		return taid;
	}

	public void setPageRange(int[] pageRange){
		this.pageRange=pageRange;
	}

	public int[] getPageRange(){
		return pageRange;
	}
}
