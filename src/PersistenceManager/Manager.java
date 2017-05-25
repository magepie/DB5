package PersistenceManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class Manager {
    String fs_dir="fs/";
    static final private Manager singleton; static {
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

    public void write(String taid, int pageid, String data){
        File page=new File(fs_dir+pageid+".txt");
        
        if(page.exists()){
            try {
                FileWriter fw= new FileWriter(page);
                fw.write(taid+"\n"+data);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else System.out.println("Page with id "+pageid+" does not exist..");
    }

    public void commit(String taid){}

    static public Manager getInstance() {
        return singleton;
    }
}
