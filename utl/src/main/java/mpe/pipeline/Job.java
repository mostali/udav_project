package mpe.pipeline;

import mpe.core.U;
import mpc.str.sym.SEP;
import mpc.exception.RequiredRuntimeException;
import lombok.Getter;
import lombok.Setter;
import mpu.core.ARG;
import mpu.IT;
import mpc.types.abstype.ExVar;
import mpc.log.L;

import java.util.Map;

public abstract class Job extends AbsContext {
	public Job(Class parentJob, String... name) {
		super(parentJob, name);
	}

	public Job(String... name) {
		super(name);
	}

	public Pipline setFakePipline() {
		Pipline pip = null;
		setPipline(pip = new Pipline("fake parent for job [" + getId() + "]"));
		pip.addJob(this);
		return pip;
	}

	public static class RestartJobException extends RuntimeException {
	}

	public void executeJob() throws Exception {
		int tc = 0;
		while (true) {
			tc++;
			try {
				if (L.isInfoEnabled()) {
					L.info(SEP.COLON.__str1__("JOB/tc:%s/%s", true, tc, name()));
				}
				executeSinglyJob();
				U.merge(getPiplineArtifacts(), getArtifacts(), true);
				break;
			} catch (RestartJobException ex) {
				if (L.isInfoEnabled()) {
					L.info("RestartJobException:{}", ex.getMessage());
				}
				continue;
			}
		}
	}

	public <T> T getVar(String key, Class<T> asClass, Object... defRq) {
		Object var = getVar(key, defRq);
		if (var == null) {
			return (T) var;
		} else if (var.getClass().isAssignableFrom(asClass)) {
			return asClass.cast(var);
		}
		throw new RequiredRuntimeException("Var has class [%s], but you need [%s]", var.getClass(), asClass);
	}

	public ExVar getExVar(String key, ExVar... defRq) {
		Object var = getVar(key, defRq);
		if (var != null) {
			return IT.isType0(var, ExVar.class, "ExtVar class", key);
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("ExtVar [%s] is required", key);
	}

	@Override
	public Object getVar(String key, Object... defRq) {
		if (getVars().containsKey(key)) {
			return getVars().get(key);
		}
		if (getPipline().getVars().containsKey(key)) {
			return getPipline().getVars().get(key);
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Var [%s] is required", key);
	}

	@Override
	public Object getArtifact(String key, Object... defArtifactRq) {
		if (getArtifacts().containsKey(key)) {
			return getArtifacts().get(key);
		}
		if (getPiplineArtifacts().containsKey(key)) {
			return getPiplineArtifacts().get(key);
		}
		if (ARG.isDef(defArtifactRq)) {
			return ARG.toDef(defArtifactRq);
		}
		throw new RequiredRuntimeException("Artifact [%s] is required", key);
	}

	public Map<String, Object> getPiplineArtifacts() {
		Pipline pip = getPipline();
		if (pip == null) {
			throw new IllegalStateException("job need pipline");
		}
		return getPipline().getArtifacts();
	}

	public abstract void executeSinglyJob() throws Exception;

	@Getter
	@Setter
	private Pipline pipline;

}
