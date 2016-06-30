package nachos.threads;


public class PrioritySchedulerTest {
	//PriorityScheduler Test
	
	public void selfTest() {
		System.out.println("-------------TEST1------------------");
		Test1();
		System.out.println("------------------------------------");
		System.out.println("-------------TEST2------------------");
		Test2();
		System.out.println("------------------------------------");
		System.out.println("-------------TEST3------------------");
		Test3();
		System.out.println("------------------------------------");
	
	}
	
	public void Test1(){
		//기본 작동 테스트
		for(int i=1;i<=10;i++){
			KThread t = new KThread(new threadTest(i));
			t.fork();
		}
		KThread.yield();
		
	}
	public void Test2(){
		//우선순위 변경 확인
		
		for(int i=1;i<=10;i++){
			KThread t = new KThread(new threadTest(i));
			if(i==5){
				ThreadedKernel.scheduler.setPriority(t, 5);				
			}else if(i==8){
				ThreadedKernel.scheduler.setPriority(t, 4);				
			}
			t.fork();
		}
		KThread.yield();
	}
	public void Test3(){
		//Lock 복합 테스트
		
		Lock lock1 = new Lock();
		Lock lock2 = new Lock();
		
		KThread holder = new KThread(new lockTestMinPriority(lock1, lock2));
		ThreadedKernel.scheduler.setPriority(holder, 1);
		holder.fork();
		KThread.yield();
		
		for(int i=1;i<=3;i++){
			KThread t = new KThread(new lockTest(lock1, i));
			ThreadedKernel.scheduler.setPriority(t, i+1);
			t.fork();
		}
		for(int i=4;i<=7;i++){
			KThread t = new KThread(new lockTest(lock2, i));
			ThreadedKernel.scheduler.setPriority(t, i);
			t.fork();
		}

		KThread.yield();
		
	}
	private class threadTest implements Runnable{
		
		int num;
		public threadTest(int number){
			num=number;
		}
		@Override
		public void run() {
			System.out.println(num+"번 KThread 시작");
			System.out.println(num+"번 KThread 종료");
		}
	}

	private class lockTest implements Runnable{
		
		int num;
		Lock lock;
		public lockTest(Lock l, int number){
			lock=l;
			num=number;
		}
		@Override
		public void run() {
			System.out.println(num+"번 KThread 시작");
			lock.acquire();
			System.out.println(num+"번 KThread Lock 획득");
			lock.release();
			System.out.println(num+"번 KThread 종료");
		}
	}
	private class lockTestMinPriority implements Runnable{
		
		Lock lock1,lock2;
		public lockTestMinPriority(Lock l, Lock l2){
			lock1=l;
			lock2=l2;
		}
		@Override
		public void run() {
			System.out.println("Holder KThread 시작");
			lock1.acquire();
			System.out.println("Holder KThread Lock1 획득");
			lock2.acquire();
			System.out.println("Holder KThread Lock2 획득");
			KThread.yield(); //Lock1, Lock2를 가지고 양보
			lock2.release();
			System.out.println("Holder KThread Lock2 방출");
			KThread.yield(); //Lock2를 가지고 양보
			lock1.release();
			System.out.println("Holder KThread Lock1 방출");
			System.out.println("Holder KThread 종료");
		}
	}
}
