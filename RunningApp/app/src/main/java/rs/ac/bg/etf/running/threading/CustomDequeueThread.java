package rs.ac.bg.etf.running.threading;

import java.util.concurrent.LinkedBlockingDeque;

public class CustomDequeueThread extends Thread {

    private LinkedBlockingDeque<Runnable> runnableDequeue = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        while(true) {
            try {
                Runnable runnable = runnableDequeue.takeFirst();
                runnable.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public LinkedBlockingDeque<Runnable> getRunnableDequeue() {
        return runnableDequeue;
    }
}
