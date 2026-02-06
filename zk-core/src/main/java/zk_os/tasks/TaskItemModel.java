package zk_os.tasks;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpe.core.ERR;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.str.STR;

@RequiredArgsConstructor
public class TaskItemModel {
	public static final String OK = "OK";
	final Ctx10Db.CtxModel10 model;

	public static TaskItemModel of(Ctx10Db.CtxModel10 model) {
		return new TaskItemModel(model);
	}

	public TaskManager.TaskType getTaskType(TaskManager.TaskType... defRq) {
		return ENUM.valueOf(model.getO10(), TaskManager.TaskType.class, defRq);
	}

	public String getStatus() {
		return model.getO1();
	}

	public void setStatus(String status, boolean... skipSave) {
		model.setO1(status);
		if (ARG.isDefNotEqTrue(skipSave)) {
			saveModelAsUpdate();
		}
	}

	public String getErrorValue() {
		return model.getExt();
	}

	public Boolean getJobState() {
		return X.notEmpty(getErrorValue()) ? (Boolean) false : (X.notEmpty(getStatus()) ? (Boolean) true : null);
	}

	public String getStatusEmoj() {
		Boolean jobState = getJobState();
//		return isOk == null ? SYMJ.THINK : (isOk ? SYMJ.OK_GREEN : SYMJ.FAIL_STOP);
		return jobState == null ? SYMJ.POINT_GREEN_PULSAR : (jobState ? SYMJ.OK_GREEN : SYMJ.FAIL_STOP);
	}

	public void setResult(CharSequence result, boolean append, boolean... skipSave) {
		model.setValue(append ? STR.concat(model.getValue(), result) : X.toStringNN(result, ""));
		if (ARG.isDefNotEqTrue(skipSave)) {
			saveModelAsUpdate();
		}
	}

	public void setError(Throwable throwable, boolean... skipSave) {
		String errValue = ERR.getStackTraceShort(throwable, 3);
		model.setExt(errValue);
		if (ARG.isDefNotEqTrue(skipSave)) {
			saveModelAsUpdate();
		}
	}

	public void saveModelAsUpdate() {
		db(this).saveModelAsUpdate(model);
	}

	public static ICtxDb db(TaskItemModel model) {
		TaskManager.TaskType taskType = model.getTaskType();
		if (TaskManager.TaskType.ASYNC == taskType) {
			return TaskManager.dbAsync();
		}
		throw new WhatIsTypeException(taskType);
	}

	public String getName() {
		return model.getKey();
	}

	public String getValue() {
		return model.getValue();
	}
}
