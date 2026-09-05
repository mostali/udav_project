package zk_os.tasks;

import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mpc.env.APP;
import mpc.log.LogTailReader;
import mpe.rt.SLEEP;
import mpu.X;
import mpu.str.JOIN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_form.notify.ZKI;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TaskManager {

	public static final Logger L = LoggerFactory.getLogger(TaskManager.class);

	public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 10, 10, 25, 10, 0};

	public static Ctx10Db dbAsync() {
		return (Ctx10Db) Ctx10Db.of(APP.TREE_TASKS()).withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
	}

	public enum TaskType {
		ASYNC,
	}


	public static <T> void addTaskAsyncUx(String taskName, Supplier<T> supplier) {
		addTaskAsync(taskName, supplier);
		ZKI.infoAfterPointer(X.f("Task %s created", taskName), ZKI.Level.INFO);
	}

	public static <T> void addTaskAsync(String taskName, Supplier<T> supplier) {
		LogTailReader logTailReader = LogTailReader.newLoggedTask();
		L.info("Starting task [{}]", taskName);

		TaskItemModel taskItemModel = TaskPanel.addTaskAsync(taskName);

		CompletableFuture.supplyAsync(() -> {
			L.info("Start main task [{}]", taskName);
//			SLEEP.sec(5);
//			X.throwException("aaaasdasdasd");
			T t = supplier.get();
			L.info("End main task [{}]", taskName);
			return t;
		}).thenAccept(result -> {
			try {
				L.info("Finish phase OK with result: {}", X.toStringLog(result));
			} finally {
				List<String> logOut = logTailReader.readNextTailLogLines();
				String newPart = JOIN.allByNL(logOut);
//				taskItemModel.putAppend(taskName, newPart, true);
//				taskItemModel.put(taskName, CKey.O9.of("OK"));
//				taskItemModel.setError(throwable);
				taskItemModel.setStatus(TaskItemModel.OK, true);
				taskItemModel.setResult(newPart, true);
			}
		}).exceptionally(throwable -> {
			L.info("Finish phase with error", throwable);
			taskItemModel.setError(throwable);
			return null;
		});

	}


}
