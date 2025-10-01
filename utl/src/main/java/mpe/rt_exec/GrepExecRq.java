package mpe.rt_exec;

import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpc.exception.NI;
import mpc.log.L;
import mpu.str.TKN;
import mpu.str.STR;
import mpe.rt.core.ExecRq;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GrepExecRq {

	public static final String EXT_GREP = ".grep";

	public static void main(String[] args) throws ExecRq {

		List<String> strings = execGrepStringInDir(Paths.get("/home/dav/pjbf_tasks/67/server.log/"), "NFO  \\[                   save-thread");
//		List<String> strings = execGrepStringInDir(Paths.get("/home/dav/pjbf_tasks/69/log по первичной обработке/server.log"), "NFO  \\[                   save-thread");
//		P.exit(strings);
//		execGrepStringInDir("EDC is not valid",)
	}

	public static Map<String, String> toMapWithFiles(List<String> linesWithFiles) {
		return linesWithFiles.stream().map(l -> {
			String[] two = TKN.two(l, ":");
			String path = two[0];
			return new String[]{path, two[1]};
		}).filter(Objects::nonNull).collect(Collectors.toMap(two -> two[0], two -> two[1], (existValue, newValue) -> existValue + STR.NL + newValue));
	}

	//	public static List<String> execGrepStringInDir0(String needle, String inDir, List<String> excludeDir, List<String> include, List<String> exclude) throws ExecRq {
//		try {
//			return GrepExecRq.execGrepStringInDir(needle, inDir, excludeDir, include, exclude);
//		} catch (ExecRq e) {
//			if (e.status == 1 && e.getOut(false).isEmpty()) {
//				Sys.p("empty:" + module[0]);
//			} else {
//				X.throwException(e);
//			}
//		}
//	}
	public static List<String> execGrepStringInDir(Path inDir, String needle) throws ExecRq {
		return execGrepStringInDir(inDir.toString(), needle, null, null, null);
	}

//	public static List<String> execGrepStringInFile(String inFile, String needle, List<String> excludeDir, List<String> include, List<String> exclude) throws ExecRq {
//
//	}
	public static List<String> execGrepStringInDir(String inDir, String needle, List<String> excludeDir, List<String> include, List<String> exclude) throws ExecRq {
		IT.notEmpty(needle);
//		ERR.isDirExist(Paths.get(inDir));
		List cmd = ARR.asAL("grep", "-rsi");
		{
			{
				if (X.notEmpty(excludeDir)) {
					excludeDir.forEach(excDir -> {
						cmd.add("--exclude-dir");
						cmd.add(excDir);
					});
				}
			}
			{
				if (X.notEmpty(include)) {
					NI.stop("include");
//						includeDir.forEach(incDir -> {
//							cmd.add("--include-dir");
//							cmd.add(incDir);
//						});
				}
			}
			{
				if (X.notEmpty(exclude)) {
					NI.stop("exclude");
//						includeDir.forEach(incDir -> {
//							cmd.add("--include-dir");
//							cmd.add(incDir);
//						});
				}
			}
		}

		cmd.add("-e");
		cmd.add(needle);
		cmd.add(inDir);

//			ExecRq execRq = ExecRq.exec(false, "grep", "-sr", "--exclude-dir", "target", "--exclude-dir", "src", needle, inDir);
		String[] array = (String[]) cmd.toArray(new String[0]);
		if (L.isTraceEnabled()) {
			L.trace(" exec command:" + ARR.as(array));
		}
		ExecRq execRq = ExecRq.exec(false, array);
		return execRq.getOut(true);
	}
}
