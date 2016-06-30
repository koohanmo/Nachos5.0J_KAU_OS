package nachos.threads;

import java.util.Comparator;
import java.util.TreeSet;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
    	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * 
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    	Machine.interrupt().disable();
    	while(!waitQueue.isEmpty()){
    		if(waitQueue.first().time<=Machine.timer().getTime()){
    	//		System.out.println(Machine.timer().getTime()+"에 "+KThread.currentThread().getName()+"을 ready로 변환");
    			waitQueue.pollFirst().thread.ready();
    		}
    		else break; //아직 시간이 안됨
    	}
    	Machine.interrupt().enable();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	Machine.interrupt().disable();
	waitQueue.add(new ThreadSet(Machine.timer().getTime()+x, KThread.currentThread()));
	//System.out.println(Machine.timer().getTime()+x+"까지 "+KThread.currentThread().getName()+"을 sleep으로 변환");
	KThread.sleep();
	Machine.interrupt().enable();
    }
    
    private TreeSet<ThreadSet> waitQueue= new TreeSet<ThreadSet>(new Comparator<ThreadSet>() {
		@Override
		public int compare(ThreadSet o1, ThreadSet o2) {
			// TODO Auto-generated method stub
			if(o1.time<o2.time) return -1;
			else if(o1.time==o2.time) return 0;
			else return 1;
		}
	});
    
    
    private class ThreadSet{
    	long time;
    	KThread thread;
    	
    	public ThreadSet(long time, KThread thread)
    	{
    		this.time=time;
    		this.thread=thread;
    	}
    }


	public static void selfTest() {
		// TODO Auto-generated method stub
		//Project 3 Alarm Test
		System.out.println("알람테스트");
		AlarmTest test3 = new AlarmTest();
		test3.alarmTest();
	}
    
}
