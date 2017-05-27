package PersistenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.*;

public class LogEntry extends WriteReq {
    private final Logger logger= Logger.getLogger(LogEntry.class.getName());
    private FileHandler fh= null;

    public LogEntry(){
        SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
        try{
            System.out.println(System.getProperty("user.dir"));
            fh= new FileHandler("ps/LogFile_"+format.format(Calendar.getInstance().getTime())+".log");
        } catch (Exception e){
            e.printStackTrace();
        }

        fh.setFormatter(new Formatter(){
            @Override
            public String format(LogRecord record){
                SimpleDateFormat logTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(record.getMillis());
                record.setSequenceNumber(000000001);
                return record.getLevel()
                        + logTime.format(cal.getTime())
                        + " || "
                        + record.getSourceClassName().substring(
                        record.getSourceClassName().lastIndexOf(".")+1,
                        record.getSourceClassName().length())
                        + "."
                        + record.getSourceMethodName()
                        + "() : "
                        + record.getMessage() + "\n";

            }
        });
        logger.addHandler(fh);

    }
    public void doLogging(){
        logger.info("testing");
    }
}
