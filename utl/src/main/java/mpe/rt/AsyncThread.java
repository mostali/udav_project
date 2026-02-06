package mpe.rt;

import lombok.SneakyThrows;
import mpu.X;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class AsyncThread {

	@SneakyThrows
	public static void main(String[] args) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> SLEEP.sec(5, "1:wait 5 sec")).thenAccept((result) -> {
			X.p("1::::Inner next:");

		});
		CompletableFuture<Void> futureErr = CompletableFuture.runAsync(() -> {
			SLEEP.sec(3, "2:wait 3 sec");
			X.throwException("2:errAfter3SEC");
		});

		SLEEP.sec(4, "M:wait other");

		X.p("Result1:" + future.get());
		X.p("Result2:" + futureErr.get());

	}

	public static class TestApp {

		public static void main(String[] args) {
			Path fileLock = Paths.get("/tmp/testlock");
			FileSemaphore sem = new FileSemaphore(fileLock);

			new Thread(new Hello(sem)).start();
			new Thread(new Hello(sem)).start();
			new Thread(new Hello(sem)).start();
			new Thread(new Hello(sem)).start();
			new Thread(new Hello(sem)).start();
			SLEEP.sec(4);
			new Thread(new Hello(sem)).start();
			new Thread(new Hello(sem)).start();
		}
	}

	static class Hello implements Runnable {
		//		private Semaphore semaphore;
		private FileSemaphore semaphore;

		//		Hello(Semaphore semaphore) {
		Hello(FileSemaphore semaphore) {
			this.semaphore = semaphore;
		}

		public void run() {
			if (!semaphore.tryAcquire()) {
				System.out.println(Thread.currentThread().getName() + "- BLOOOOOOOOOOOCKED ALLL IS WORK");
				return;
			}
			try {
				System.out.println(Thread.currentThread().getName() + "-ALLOWED");
				Thread.sleep(3000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			} finally {
				System.out.println(Thread.currentThread().getName() + "-RELEASED");
				semaphore.release();
			}
		}
	}
}
