package nachos.threads;


import java.util.LinkedList;
import nachos.machine.Lib;
import nachos.machine.Machine;


/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
	this.conditionLock = conditionLock;
	//waitQueue 샏성
	waitQueue =new LinkedList<KThread>(); 
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    
    public void sleep() {
    	
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	conditionLock.release();
	//Homework 2
	Machine.interrupt().disable();
	//현재 쓰레드를 Condition2의 waitQueue에 저장
	waitQueue.add(KThread.currentThread());
	//현재 쓰레드의 state를  Blocked로 변경
	KThread.sleep();	
	Machine.interrupt().enable();
	//Homework 2
	conditionLock.acquire();
	
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake(){
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	//Homework 2
	Machine.interrupt().disable();
	//waitQueue가 비어있지 않다면 가장 먼저 들어온 쓰레드를 state ready로 변경(readyQueue에 넣음)
	if(!waitQueue.isEmpty())waitQueue.removeFirst().ready();
	Machine.interrupt().enable();
	//Homework 2
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
    	
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	//Homework 2
	Machine.interrupt().disable();
	//waitQueue에 잇는 모든 쓰레드들을 readyQueue에 넣음
	while(!waitQueue.isEmpty()) waitQueue.removeFirst().ready();
	Machine.interrupt().enable();
	//Homework 2
    }
    
    public static void selfTest() {
    	System.out.println("Condition2 TEST");
//    	Project 2 Condition2 Test
    	Condition2Test test2 = new Condition2Test();
    	test2.startTest();
    }
    

    private Lock conditionLock;
    private LinkedList<KThread> waitQueue;
    
    
}
