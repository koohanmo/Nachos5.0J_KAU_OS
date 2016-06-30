package nachos.threads;

import nachos.machine.Machine;

public class AlarmTest {

	public void alarmTest(){
		
		KThread k1 = new KThread(new sleepRunnable(4000));
		k1.setName("첫번째 쓰레드");
		KThread k2 = new KThread(new sleepRunnable(3000));
		k2.setName("두번째 쓰레드");
		KThread k3 = new KThread(new sleepRunnable(2000));
		k3.setName("세번째 쓰레드");
		KThread k4 = new KThread(new sleepRunnable(1000));
		k4.setName("네번째 쓰레드");
		KThread k5 = new KThread(new sleepRunnable(40000));
		k5.setName("다섯번째 쓰레드");
		KThread k6 = new KThread(new sleepRunnable(3000));
		k6.setName("여섯번째 쓰레드");
		KThread k7 = new KThread(new sleepRunnable(2000));
		k7.setName("일곱번째 쓰레드");
		KThread k8 = new KThread(new sleepRunnable(1000));
		k8.setName("여덟번째 쓰레드");
		
		k1.fork();
		k2.fork();
		k3.fork();
		k4.fork();
		k5.fork();
		k6.fork();
		k7.fork();
		k8.fork();
		
		k5.join();
	}
	
	private class sleepRunnable implements Runnable{
		
		private long waitTime;
		
		public sleepRunnable(long t){
			waitTime = t;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println(waitTime+"+"+Machine.timer().getTime()+
					" 을 기다립니다.");
			ThreadedKernel.alarm.waitUntil(waitTime);
			System.out.println(KThread.currentThread().getName() 
					+"가  "+waitTime+"을 기다리고 깨어났습니다."+"("+Machine.timer().getTime()+")");
		}
		
	}
	
}
