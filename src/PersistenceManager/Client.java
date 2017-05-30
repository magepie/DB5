package PersistenceManager;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class Client extends Thread {
    private String name;
    private int taid;
    private int[] pageRange = new int[2];


    public Client(String name, int pageRange[]) {
        this.name = name;
        this.pageRange = pageRange;
    }

    public void run() {
        //Manager persistentManager= Manager.getInstance();

        Manager persistentManager = Manager.getInstance();
        System.out.println("Client with name " + name + " has been initiated!");
        while (true) {

            WriteReq entry; //it has to be initiated inside the while, else the entire buffer is overwritten
            String t_id = persistentManager.beginTransaction();

            System.out.println(name + " Transaction initiated with transaction id " + t_id);

            //write 4 times

            for (int i = 0; i < 3; i++) {
                entry = new WriteReq();
                entry.setTid(t_id);
                entry.setEntryData(name);
                entry.setPageId(ThreadLocalRandom.current().nextInt(this.pageRange[0], this.pageRange[1] + 1));
                persistentManager.write(entry);
            }
            //and commit
            System.out.println(name + " Committing transaction " + t_id);

            persistentManager.commit(t_id);
            try {
				Thread.sleep((long)(Math.random() * 1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public void setTaid(int taid) {
        this.taid = taid;
    }

    public int getTaid() {
        return taid;
    }

    public void setPageRange(int[] pageRange) {
        this.pageRange = pageRange;
    }

    public int[] getPageRange() {
        return pageRange;
    }

}
