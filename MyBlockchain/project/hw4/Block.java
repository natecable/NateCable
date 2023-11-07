package project.hw4;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
public class Block {
    
    public String preHead;
    public String root;
    public long time;
    public double diffTarget = 0.5;
    public int nonce;

    public Block(String preHead, String root){
        this.preHead = preHead;
        this.root = root;
        this.time = timeSince70();
    }


    private long timeSince70(){
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        TemporalAccessor t = f.parse("01/01/1970");
        LocalDateTime fro = LocalDateTime.from(t);
        return (long)fro.getYear()*31556926 + (long)fro.getDayOfYear()*86400 + (long)fro.getHour()*3600 + (long)fro.getMinute()*60 + (long)fro.getSecond();
    }

    public long getTime(){
        return time;
    }

}
