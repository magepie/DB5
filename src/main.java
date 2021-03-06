import PersistenceManager.Client;
import PersistenceManager.Manager;
import PersistenceManager.FileSystem;

public class main {

	private static void clientsInit(){
		//initializing the range of pages per client
		int [] c1_pages= {10,19};
		int [] c2_pages= {20,29};
		int [] c3_pages= {30,39};
		int [] c4_pages= {40,49};
		int [] c5_pages= {50,59};

		Manager persistentManager= Manager.getInstance();

		//instantiating the clients
		Client c1  = new Client("Client1",c1_pages);
		Thread t1  = c1;
		Client c2  = new Client("Client2",c2_pages);
		Thread t2= c2;
		Client c3  = new Client("Client3",c3_pages);
		Thread t3= c3;
		Client c4  = new Client("Client4",c4_pages);
		Thread t4= c4;
		Client c5  = new Client("Client5",c5_pages);
		Thread t5= c5;

		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) { }

		t1.interrupt();
		t2.interrupt();
		t3.interrupt();
		t4.interrupt();
		t5.interrupt();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		clientsInit();
		
	}

}
