public class Reader extends Thread {
    private Monitor m;
    Reader(Monitor m) {
        this.m = m;
    }
    public void run() {
        int randomNum = (int)(Math.random()*(2-0+1)+0);
        try {
            if(randomNum == 0){
                m.read(1);
//                fr1.close();
            }else if(randomNum == 1){
                m.read( 2);
//                fr2.close();
            }else{
                m.read(3);
//                fr3.close();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
