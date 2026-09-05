package mpe.rt;

import lombok.extern.slf4j.Slf4j;
import mpu.X;

import java.util.concurrent.*;
import java.util.function.Function;

@Slf4j
public class ThreadPool {

	public static void main(String[] args) {
		ThreadPool threadPool = new ThreadPool(4); // Создаем пул из 4 потоков

		// Пример добавления задач
		for (int i = 0; i < 30; i++) {
			int taskId = i;
			threadPool.submit((Void v) -> {
				// Имитация работы
				System.out.println("Task " + taskId + " is running.");
				if (taskId == 5) { // Пример задачи, которая вызывает ошибку
//					throw new RuntimeException("Error in task " + taskId);
				}
				try {
					Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				return null;
			});
		}

		try {
			threadPool.joinAndWaitEndWork(3000); // Ожидаем завершения всех задач
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Thread was interrupted: " + e.getMessage());
		}
	}

	private final ThreadPoolExecutor executorService;
	private final CompletionService<Void> completionService;
	private final BlockingQueue<Future<Void>> futureQueue;

	public ThreadPool(int numberOfThreads) {
		this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
		this.completionService = new ExecutorCompletionService<>(executorService);
		this.futureQueue = new LinkedBlockingQueue<>();
	}

	public void submit(Function<Void, Void> task) {
		Future<Void> future = completionService.submit(() -> task.apply(null));
		futureQueue.offer(future);
	}

	public boolean isWork() {
		return !futureQueue.isEmpty() || executorService.getActiveCount() > 0;
	}

	public void joinAndWaitEndWork(long checkEvery) throws InterruptedException {
		try {
			while (isWork()) {
//				Thread.sleep(checkEvery);
				log.info("wait result..");
				Future<Void> peek = futureQueue.take();
				if (peek == null) {
					break;
				}
				Future<Void> future = completionService.take();
				try {
					future.get(); // Wait for the task to complete and check for exceptions
				} catch (ExecutionException e) {
					X.throwException(e);
				}
			}
		} finally {
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
	}

}
