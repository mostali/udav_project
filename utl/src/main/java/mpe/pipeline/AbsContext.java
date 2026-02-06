package mpe.pipeline;

import mpe.core.P;
import mpc.exception.RequiredRuntimeException;
import lombok.Getter;
import mpu.core.ARG;
import mpc.types.abstype.ExVar;
import mpc.rfl.RFL;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AbsContext {
	@Getter
	public final String id = UUID.randomUUID().toString();

	private String name;
	private final Class jobParent;

	public String name() {
		if (name != null) {
			return name;
		} else if (jobParent == null) {
			return name = UUID.randomUUID().toString().substring(28);
		}
		return name = RFL.fieldNameStRq(jobParent, this, true, false);
	}

	@Getter
	private final Map<String, Object> vars = new LinkedHashMap<>();
	@Getter
	private final Map<String, Object> init_vars = new LinkedHashMap<>();

	private final Map<String, Object> artifacts = new LinkedHashMap<>();

	public Map<String, Object> getArtifacts() {
		return artifacts;
	}

	public AbsContext(Class parentField, String... name) {
		this.jobParent = parentField;
		this.name = name.length == 0 ? null : name[0];
	}

	public AbsContext(String... name) {
		this(null, name);
	}

	public AbsContext addVar(String key, Object var) {
		getVars().put(key, var);
		return this;
	}

	public AbsContext addExVar(String key, Object var) {
		getVars().put(key, ExVar.ofAny(var));
		return this;
	}

	public Object getVar(String key, Object... defVarRq) {
		if (getVars().containsKey(key)) {
			return getVars().get(key);
		}
		if (ARG.isDef(defVarRq)) {
			return ARG.toDef(defVarRq);
		}
		throw new RequiredRuntimeException("Var [%s] is required", key);
	}

	public AbsContext addArtifact(String key, Object var) {
		getArtifacts().put(key, var);
		return this;
	}

	public Object getArtifact(String key, Object... defArtifactRq) {
		if (getArtifacts().containsKey(key)) {
			return getArtifacts().get(key);
		}
		if (ARG.isDef(defArtifactRq)) {
			return ARG.toDef(defArtifactRq);
		}
		throw new RequiredRuntimeException("Artifact [%s] is required", key);
	}

	public void printVarsAndArtifacts() {
		P.p(getVars());
		P.p(getArtifacts());
	}
}
