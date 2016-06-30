package nachos.threads;

import java.util.LinkedList;

import nachos.machine.Lib;
import nachos.machine.Machine;

/**
 * A scheduler that chooses threads based on their priorities.
 *
 * <p>
 * A priority scheduler associates a priority with each thread. The next thread
 * to be dequeued is always a thread with priority no less than any other
 * waiting thread's priority. Like a round-robin scheduler, the thread that is
 * dequeued is, among all the threads of the same (highest) priority, the
 * thread that has been waiting longest.
 *
 * <p>
 * Essentially, a priority scheduler gives access in a round-robin fassion to
 * all the highest-priority threads, and ignores all other threads. This has
 * the potential to
 * starve a thread if there's always a thread waiting with higher priority.
 *
 * <p>
 * A priority scheduler must partially solve the priority inversion problem; in
 * particular, priority must be donated through locks, and through joins.
 */
public class PriorityScheduler extends Scheduler {
    /**
     * Allocate a new priority scheduler.
     */
    public PriorityScheduler() {
    }
    
    /**
     * Allocate a new priority thread queue.
     *
     * @param	transferPriority	<tt>true</tt> if this queue should
     *					transfer priority from waiting threads
     *					to the owning thread.
     * @return	a new priority thread queue.
     */
    public ThreadQueue newThreadQueue(boolean transferPriority) {
	return new PriorityQueue(transferPriority);
    }

    public int getPriority(KThread thread) {
	Lib.assertTrue(Machine.interrupt().disabled());
		       
	return getThreadState(thread).getPriority();
    }

    public int getEffectivePriority(KThread thread) {
	Lib.assertTrue(Machine.interrupt().disabled());
		       
	
	return getThreadState(thread).getEffectivePriority();
    }

    public void setPriority(KThread thread, int priority) {
//	Lib.assertTrue(Machine.interrupt().disabled());
		       
	Lib.assertTrue(priority >= priorityMinimum &&
		   priority <= priorityMaximum);
	 
	getThreadState(thread).setPriority(priority);
    }

    public boolean increasePriority() {
	boolean intStatus = Machine.interrupt().disable();
		       
	KThread thread = KThread.currentThread();

	int priority = getPriority(thread);
	if (priority == priorityMaximum)
	    return false;

	setPriority(thread, priority+1);

	Machine.interrupt().restore(intStatus);
	return true;
    }

    public boolean decreasePriority() {
	boolean intStatus = Machine.interrupt().disable();
		       
	KThread thread = KThread.currentThread();

	int priority = getPriority(thread);
	if (priority == priorityMinimum)
	    return false;

	setPriority(thread, priority-1);

	Machine.interrupt().restore(intStatus);
	return true;
    }

    /**
     * The default priority for a new thread. Do not change this value.
     */
    public static final int priorityDefault = 1;
    /**
     * The minimum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMinimum = 0;
    /**
     * The maximum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMaximum = 7;    

    /**
     * Return the scheduling state of the specified thread.
     *
     * @param	thread	the thread whose scheduling state to return.
     * @return	the scheduling state of the specified thread.
     */
    protected ThreadState getThreadState(KThread thread) {
	if (thread.schedulingState == null)
	    thread.schedulingState = new ThreadState(thread);

	return (ThreadState) thread.schedulingState;
    }

    /**
     * A <tt>ThreadQueue</tt> that sorts threads by priority.
     */
    protected class PriorityQueue extends ThreadQueue {
	PriorityQueue(boolean transferPriority) {
	    this.transferPriority = transferPriority;
	}

	public void waitForAccess(KThread thread) {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    getThreadState(thread).waitForAccess(this);
	}

	public void acquire(KThread thread) {
	    Lib.assertTrue(Machine.interrupt().disabled());
//	    System.out.println("----------acquire----------");
	    
	    ThreadState current =getThreadState(thread);
	    
	    if(holder !=null && transferPriority)
	    	holder.myResources.remove(this);
	    
	    holder=current;
	    current.acquire(this);
	}


	public KThread nextThread() {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    if(waitQueue.isEmpty()) return null;
	    if(holder !=null && transferPriority)
	    	holder.myResources.remove(this);
	   
	    KThread nextKThread = pickNextThread().thread;
	    if(nextKThread!=null){
	    	waitQueue.remove(nextKThread);
	    	getThreadState(nextKThread).acquire(this);
	    }
	    return nextKThread;
	    
	}

	/**
	 * Return the next thread that <tt>nextThread()</tt> would return,
	 * without modifying the state of this queue.
	 *
	 * @return	the next thread that <tt>nextThread()</tt> would
	 *		return.
	 */
	
	//다음 우선순위인 ThreadState를 반환 
	protected ThreadState pickNextThread() {
		
		ThreadState nKThreadState=null;
		for(KThread kt : waitQueue){
			//(ThreadState)kt.schedulingState = getThreadState(kt)와 같은 문장		
			int curPriority  = getThreadState(kt).getEffectivePriority();
			if(nKThreadState==null) nKThreadState = getThreadState(kt);
			else if(nKThreadState.getEffectivePriority()<curPriority) nKThreadState = getThreadState(kt);	
		}
	    return nKThreadState;
	}
	
	//TODO
	public void print() {
	    Lib.assertTrue(Machine.interrupt().disabled());
	    // implement me (if you want)
	}
	
	//HOMEWORK
	public int getEffectivePriority(){
		//waitQueue에 들어가 있는 KThread 중에 가장 우선 순위가 높은 것은 effective 변수에 cache
		if(!transferPriority) return priorityMinimum;
		if(dirty){
			effective = priorityMinimum;
			for(KThread kt : waitQueue){
				int ep = getThreadState(kt).getEffectivePriority();
				effective = effective< ep? ep : effective;
			}
			dirty=false;
		}
		return effective;
	}
	

	/**
	 * <tt>true</tt> if this queue should transfer priority from waiting
	 * threads to the owning thread.
	 */
	
	
	public boolean transferPriority;
	
	//New Variables
	private ThreadState holder;
	private LinkedList<KThread> waitQueue = new LinkedList<KThread>();
	private boolean dirty = false;
	private int effective;
	
	
	public void setDirty() {
		if(transferPriority){
			dirty=true;
			if(holder!=null) holder.setDirty();
		}
	}
	
    } //End of PriorityQueue

    /**
     * The scheduling state of a thread. This should include the thread's
     * priority, its effective priority, any objects it owns, and the queue
     * it's waiting for, if any.
     *
     * @see	nachos.threads.KThread#schedulingState
     */
    protected class ThreadState {
	/**
	 * Allocate a new <tt>ThreadState</tt> object and associate it with the
	 * specified thread.
	 *
	 * @param	thread	the thread this state belongs to.
	 */
	public ThreadState(KThread thread) {
	    this.thread = thread;
	    setPriority(priorityDefault);
	}

	/**
	 * Return the priority of the associated thread.
	 *
	 * @return	the priority of the associated thread.
	 */
	public int getPriority() {
	    return priority;
	}

	/**
	 * Return the effective priority of the associated thread.
	 *
	 * @return	the effective priority of the associated thread.
	 */
	
	//HOMEWORK
	public int getEffectivePriority() {
		//나와 관련된 Resources 중 가장 높은 우선순위를 effective 변수에 cache화
	    if(dirty){
	    	int curEffective =effective= this.priority;
	    	for(PriorityQueue pq : myResources){
	    		int e = pq.getEffectivePriority();
	    		effective = curEffective<e? e : curEffective;
	    	}
	    }
	    return effective;
	}

	/**
	 * Set the priority of the associated thread to the specified value.
	 *
	 * @param	priority	the new priority.
	 */
	
	public void setPriority(int priority) {
	    if (this.priority != priority)
	    {
		    this.priority = priority;
		    setDirty();
	    }
	    
	}

	/**
	 * Called when <tt>waitForAccess(thread)</tt> (where <tt>thread</tt> is
	 * the associated thread) is invoked on the specified priority queue.
	 * The associated thread is therefore waiting for access to the
	 * resource guarded by <tt>waitQueue</tt>. This method is only called
	 * if the associated thread cannot immediately obtain access.
	 *
	 * @param	waitQueue	the queue that the associated thread is
	 *				now waiting on.
	 *
	 * @see	nachos.threads.ThreadQueue#waitForAccess
	 */
	
	/*
	 * HOMEWORK
	 * This method is only called if the associated thread cannot immediately obtain access
	 */
	public void waitForAccess(PriorityQueue waitQueue) {
		//인터럽트 불가능 한지 확인
	    Lib.assertTrue(Machine.interrupt().disabled());
	    //자신이 기다리고 있는 것인지 확인
	    Lib.assertTrue(!waitQueue.waitQueue.contains(thread));
	    waitQueue.waitQueue.add(this.thread);
	    waitQueue.setDirty();
	    waitingOn = waitQueue;
	    
	    //이미 나의 리소스 리스트에 존재한다면.
	    
	    if(myResources.contains(waitQueue)){
	    	myResources.remove(waitQueue);
	    	waitQueue.holder=null;//기다려야 하기 때문에
	    }
	}

	/**
	 * Called when the associated thread has acquired access to whatever is
	 * guarded by <tt>waitQueue</tt>. This can occur either as a result of
	 * <tt>acquire(thread)</tt> being invoked on <tt>waitQueue</tt> (where
	 * <tt>thread</tt> is the associated thread), or as a result of
	 * <tt>nextThread()</tt> being invoked on <tt>waitQueue</tt>.
	 *
	 * @see	nachos.threads.ThreadQueue#acquire
	 * @see	nachos.threads.ThreadQueue#nextThread
	 */
	
	//HOMEWORK
	public void acquire(PriorityQueue waitQueue) {
		 Lib.assertTrue(Machine.interrupt().disabled());
	    myResources.add(waitQueue);
	    if(waitQueue ==waitingOn) waitingOn=null;	    
	    setDirty();
	}
	
	/*
	 * HOMEWORK
	 * Set the dirty flag, then call setDirty() on each thread of priorityQueue that the thread is waiting for
	 */
	public void setDirty(){
		if(!dirty){
			dirty = true;
			if(waitingOn!=null) waitingOn.setDirty();
		}
	}
	

	/** The thread with which this object is associated. */	   
	protected KThread thread;
	/** The priority of the associated thread. */
	protected int priority;
	
	//New Variables
	protected LinkedList<PriorityQueue> myResources = new LinkedList<PriorityQueue>();
	protected PriorityQueue waitingOn;
	protected int effective;
	private boolean dirty = false;
    }
}
