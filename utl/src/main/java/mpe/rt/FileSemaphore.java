package mpe.rt;

import lombok.SneakyThrows;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//https://habr.com/ru/articles/277669/
public class FileSemaphore {

	public final Path semaphorePath;

	public FileSemaphore(Class classLock) {
		this(Paths.get(classLock.getSimpleName() + ".lock"));
	}

	public FileSemaphore(Path filePath) {
		this.semaphorePath = filePath;
	}

	@SneakyThrows
	public boolean tryAcquire() {
		if (isExistsLockFile()) {
			return false;
		}
		try {
			// Попытка создать временный файл
			Files.createFile(semaphorePath);
			return true; // Успешно захвачена блокировка
		} catch (FileAlreadyExistsException e) {
			// Файл уже существует, значит ресурс занят
			return false; // Не удалось захватить блокировку
		}
	}

	@SneakyThrows
	public void release() {
		// Удаляем файл семафора, если он существует
		if (isExistsLockFile()) {
			Files.delete(semaphorePath);
		}
	}

	public boolean isExistsLockFile() {
		return Files.exists(semaphorePath);
	}

	public static void main(String[] args) {
		FileSemaphore semaphore = new FileSemaphore(Path.of("semaphore.lock"));

		// Пробуем захватить блокировку
		if (semaphore.tryAcquire()) {
			System.out.println("Блокировка захвачена.");
			// Выполняем операции, защищенные блокировкой
			try {
				Thread.sleep(5000); // Имитация работы
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Блокировка освобождена.");
			semaphore.release(); // Освобождаем блокировку
		} else {
			System.out.println("Не удалось захватить блокировку.");
		}
	}

	public BusyException throwBusyException(String msg) {
		return new FileSemaphore.BusyException(semaphorePath + ":LOCKED:" + msg);
	}

	public static class BusyException extends RuntimeException {
		public BusyException(String msg) {
			super(msg);
		}
	}
}
