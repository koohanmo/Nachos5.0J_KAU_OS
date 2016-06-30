package nachos.threads;

import java.util.LinkedList;

public class Condition2Test {
	
	private Lock lock = new Lock();
	private Condition2 conditionVariable = new Condition2(lock);
	
	//Contition2.java Test	
	public void startTest()
	{
		System.out.println("Condition Variable 2 Test Start!!!!");
		//sleep Test
		for(int i=1;i<=3;i++){
			System.out.println("Fork Thread  "+(i));
			KThread tempThread = new KThread(testRunnable);
			tempThread.setName(i+"번");
			tempThread.fork();
		}
		KThread.yield(); 
		
		//wake Test
		KThread wakeThread = new KThread(testWake);
		wakeThread.fork();
		KThread.yield();
		
		//wakeAll Test
		KThread wakeAllThread = new KThread(testWakeAll);
		wakeAllThread.fork();
	}
	
	//sleep() Test를 위해서
	Runnable testRunnable = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			lock.acquire();
			System.out.println(KThread.currentThread().getName() + "의 Critical Section");
			conditionVariable.sleep(); //critical section에서의 sleep();
			System.out.println(KThread.currentThread().getName() + "이 wakeUP");
			lock.release();
			
		}
	};
	
	//wake() Test를 위해서
	Runnable testWake = new Runnable()
	{
		@Override
		public void run(){
			lock.acquire();
			System.out.println("*****sleep중인 KThread 한개를  WAKE*****");
			conditionVariable.wake();
			lock.release();
		}
	};
	
	//wakeAll() Test를 위해서
	Runnable testWakeAll = new Runnable()
	{
		@Override
		public void run(){
			lock.acquire();
			System.out.println("*****sleep중인 KThread 전체를 WAKEALL*****");
			conditionVariable.wakeAll();
			lock.release();
		}
	};
	
}
