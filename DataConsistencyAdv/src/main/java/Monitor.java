import java.util.ArrayList;

public class Monitor {
    private ConnectToDB allDatabase;
    private int readerCount=0;
    private boolean busy=false,print=true;
    Monitor(ConnectToDB allDatabase){
        this.allDatabase = allDatabase;
    }
    /*
     * Every monitor has a set of procedures
     * */
    private void acquire() { this.busy=true; }
    private void release() { this.busy=false; }
    private void acquirePrint() { this.print=false; }
    private void releasePrint() { this.print=true; }
    private void incread() { this.readerCount++; }
    private void decread() { this.readerCount--; }
    void read(int dbNum)throws Exception {

        while(this.busy || this.readerCount > 1)
            Thread.sleep(500);
        if(this.readerCount==0)
            this.acquire();
        this.incread();
        ArrayList<String> arrayList;
        if(dbNum == 1){
            arrayList = allDatabase.getAllFromDB1();
        } else if (dbNum == 2) {
            arrayList = allDatabase.getAllFromDB2();
        }else{
            arrayList = allDatabase.getAllFromDB3();
        }
        if(!this.print)
            Thread.sleep(100);
        this.acquirePrint();
        System.out.print("\nReading Data from DB:"+ dbNum +"\nData Read is : ");
        for(int i = 0 ; i < arrayList.size() ; i++) {
            System.out.print(arrayList.get(i) + " ");
        }
        System.out.println();
        this.releasePrint();
        this.decread();
        if(this.readerCount==0)
            this.release();
    }
    void write(int uid)throws Exception {
        while(this.busy || this.readerCount > 0)
            Thread.sleep(500);
        this.acquire();
//        char x = (char)(random.nextInt(26)+65);

        try {
            this.allDatabase.write(uid);
        }
        catch(Exception e) {
            e.printStackTrace(); }
        this.release();
    }
}
