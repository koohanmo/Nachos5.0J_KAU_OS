package nachos.threads;

import nachos.threads.*;

public class KThreadTest {
	public void startTest()
	{
		//Test Case 1
		KThread parentThread1 = new KThread(joinTest1);
		parentThread1.fork();
		
		parentThread1.join();
		System.out.println("--------------------------");
		
		//Test Case 2
		KThread parentThread2 = new KThread(joinTest2);
		parentThread2.fork();
		
		
	}
	//쓰레드가 child쓰레드를 fork한 후 child가 종료전에 child.join();
    private Runnable joinTest1 = new Runnable(){    
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("***Project 1 JOIN() TEST1 START ***");
			KThread child = new KThread(joinTestChild);
			child.fork();
			child.join();
			
			System.out.println("***Project 1 JOIN() TEST1 END ***");
		}
    };
    
  //쓰레드가 child쓰레드를 fork한 후 child 종료 후에 child.join();
    private Runnable joinTest2 = new Runnable()
    {
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("***Project 1 JOIN() TEST2 START ***");
			KThread child = new KThread(joinTestChild);
			child.fork();
			System.out.println("***MAIN THREAD YIELD TO CHILD ***");
			System.out.println("***Wait until child finish ***");
			child.join();
			System.out.println("***Call join***");
			child.join();
			System.out.println("***Project 1 JOIN() TEST2 END ***");
		}
    };
    
    //Join Test에서 사용할 Child Thread
    private Runnable joinTestChild = new Runnable()
    {
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("***I'm CHILD THREAD***");
			for(int i=0;i<10;i++)
			{
				System.out.println("***CHILD THREAD IS RUNNING***");
			}
			System.out.println("***CHILD THREAD END***");
		}
    	
    };
	
}
