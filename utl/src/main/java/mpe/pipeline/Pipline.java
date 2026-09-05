package mpe.pipeline;

import mpc.str.sym.SEP;
import lombok.Getter;
import mpc.log.L;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Pipline extends AbsContext {

	public static class RestartPiplineException extends RuntimeException {
	}

	@Getter
	private final List<Job> jobs = new ArrayList<>();

	public Pipline(Job... jobs) {
		this(null, Pipline.class.getSimpleName() + ":" + UUID.randomUUID(), jobs);
	}

	public Pipline(String name, Job... jobs) {
		this(null, name);
	}

	public Pipline(Class parentPip, String name, Job... jobs) {
		super(parentPip, name);
		for (Job j : jobs) {
			addJob(j);
		}
	}

	public Pipline addJob(Job j) {
		this.jobs.add(j);
		j.setPipline(this);
		return this;
	}

	public Pipline executePipeline() throws Exception {
		getInit_vars().putAll(getVars());
		while (true) {
			try {
				executeSinglyPipeline();
				break;
			} catch (RestartPiplineException ex) {
				if (L.isInfoEnabled()) {
					L.info("RestartPiplineException:{}", ex.getMessage());
				}
				getVars().clear();
				getVars().putAll(getInit_vars());
				getArtifacts().clear();
				continue;
			}
		}
		return this;
	}

	public Pipline executeSinglyPipeline() throws Exception {
		String title = "PIPLINE:/" + name() + "/" + getId();
		if (L.isInfoEnabled()) {
			L.info(SEP.DOG.__str2__(title,true));
		}
		for (Job job : jobs) {
			job.executeJob();
		}
		if (L.isInfoEnabled()) {
			L.info(
					SEP.EQ._str("PIPLINE SUCCESS") +
							SEP.EQ._str("Vars:%s", getVars().size()) +
							SEP.EQ._str(getVars()) +
							SEP.EQ._str("Artifacts:%s", getArtifacts().size()) +
							SEP.EQ._str(getArtifacts())
			);
		}

		return this;
	}


}
