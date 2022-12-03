public class Writer extends Thread {
    private Monitor m;
    int uid;
    Writer(Monitor m, int uid) {
        this.m=m;
        this.uid = uid;
    }
    public void run() {
        try {
            m.write(uid);
        }
        catch (Exception e) { e.printStackTrace(); }
    }
}