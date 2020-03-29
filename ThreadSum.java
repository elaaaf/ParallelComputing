
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadSum{
	
	static int THREAD_COUNT = 1000;
	
	static ExecutorService ex = Executors.newCachedThreadPool();
	static int sum_without_sync = 0, 
			   sum_with_sync = 0;
	
	static Lock lock = new ReentrantLock(true);
	static Semaphore semaphore = new Semaphore(1);
	
	static Scanner in = new Scanner(System.in);
	
	public static void main(String[] args)
	{
		System.out.println("This program makes 1,00 threads and add 1 with each thread to a global variable.\n"
				+ "It uses synchronization. \nSo, please enter the corresponding integer to the desired synchronization technique:"
				+ "\n0: Synchronize with lock\n" + 
				"1: Synchronize with the synchronized block.\n" + 
				"2: Synchronize with semaphore with one permit.\n" + 
				"Otherwise: Synchronize with lock.\nSynchronization technique number: ");
		int x = in.nextInt();
		
		
		double start_p,end_p, start_s, end_s;
		

		start_s = System.currentTimeMillis();
		Add a = new Add();
		a.add(1000);
		end_s = System.currentTimeMillis();
		
		start_p = System.currentTimeMillis();
		for	(int i = 0; i <THREAD_COUNT; i++)
		{
			ex.execute(new AddWithoutSync());
			ex.execute(new AddWithSync(x));
		}
		ex.shutdown();
		
		while(!ex.isTerminated());
		end_p = System.currentTimeMillis();
		
		
		
		double total_s = (end_s-start_s),
			   total_p = (end_p-start_p);	
		
		System.out.printf("\nSum without sync is %d, and with sync is %d", sum_without_sync, sum_with_sync);
		System.out.printf("\nSerial Elapsed Time = %f", total_s);
		System.out.printf("\nParallel Elapsed Time = %f", total_p);
		
		double speedup = (total_s/total_p);

		System.out.printf("\nSpeedup = %f", speedup);
		System.out.printf("\nEfficiency = %f\n", (speedup/THREAD_COUNT));
		
	}
	

	
	static class AddWithoutSync implements Runnable
	{
		public void run()
		{
			sum_without_sync++;
		}
	}
	
	
	static class AddWithSync implements Runnable
	{
		boolean lock_ = false, 
				sync = false, 
				semaphore_ = false;
		AddWithSync(int x)
		{
			switch(x)
			{
				case (0):
					lock_ = true;
					break;
				case (1):
					sync = true;
					break;
				case (2):
					semaphore_ = true;
					break;
				default:
					lock_ = true;
			}
		}
		
		public void run()
		{
			if (lock_) 
			{
				try
				{
					lock.lock();
					sum_with_sync++;
				}
				finally
				{
					lock.unlock();
				}
			}
			
			else if (sync)
			
				synchronized(ex) 
				{
					sum_with_sync++;
				}
				
			
			
			else
			{
				try
				{
					semaphore.acquire();
					sum_with_sync++;
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				finally
				{
					semaphore.release();
				}
			}
		}
	}
	
	static class Add
	{
		void add(int n)
		{
			int sum= 0;
			for	(int i = 0; i < n; i++)
				sum++;
			System.out.printf("Serial sum = %d", sum);
		}
	}
}



