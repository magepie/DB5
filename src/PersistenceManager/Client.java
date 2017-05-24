package PersistenceManager;

public class Client extends Thread{
	
	public void run(){
		while(true){
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){
				return;
			}
		}
	}
}
