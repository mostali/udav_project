package mpf.zcall;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.env.Env;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.map.BootContext;
import mpc.types.opts.SeqOptions;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.pare.PareEntry;
import mpu.str.Rt;
import mpu.str.SPLIT;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ZEnv {

	public static void main(String[] args) {

//		BootContext.init(args);

//		BootContext bootContext = BootContext.get();
//		bootContext.get("");

//		ZEnv zEnv = ZEnv.ofFileJarOrDir(Path.of("/opt/appVol/.bin/jira-mod.jar"));
		ZEnv zEnv = ZEnv.ofGlobalAppVol();
		List<ZJar> zJars = zEnv.getZJars();
		ZJar first = ARRi.first(zJars);
//		X.exit(first.getZTypeApps());

//		Multimap<String[], ZType> zEnvApps = zEnv.getZEnvApps();
//		X.exit(zEnvApps);

		ZApp jiraZApp = zEnv.getZApp("jira");

//		List<ZType> zTypes = jiraZApp.getZTypes();

//		ZType lastVersionZType = jiraZApp.getLastVersionZType();

		X.exit(jiraZApp.invokeWithArgs1("invokeLines", ARR.of("-task", "*", "-projects", "EXP,TSE,SUP","--elp","-zpacks","mp.jira")));
	}

	public ZApp getZApp(String name) {
		List<ZType> zTypes = getZEnvApps().entries().stream().filter(t -> t.getKey()[0].equals(name)).map(r -> r.getValue()).collect(Collectors.toList());
		return new ZApp(IT.NE(zTypes, "app '%s' need type", name));
	}

	public Multimap<String[], ZType> getZEnvApps() {
		Multimap mm = ArrayListMultimap.create();
		List<ZJar> zJars = getZJars();
		zJars.stream().map(zJar -> zJar.getZTypeApps()).forEach(zTypeMap -> {
			zTypeMap.entrySet().forEach(e -> mm.put(e.getKey(), e.getValue()));
		});
		return mm;
	}

	public final String zDirOrJar;
	public final boolean isDir;

	@Getter
	final List<ZJar> zJars;

	public ZEnv(Path zDirOrJar) {
		isDir = UFS.isDir(zDirOrJar);
		if (isDir) {
			zJars = ZJar.ls(zDirOrJar);
		} else if (UFS.isFile(zDirOrJar)) {
			zJars = ARR.as(ZJar.of(zDirOrJar));
		} else {
			throw new WhatIsTypeException("Except existed dir or file by path %s", zDirOrJar);
		}
		this.zDirOrJar = zDirOrJar.toString();
	}

	@Override
	public String toString() {
		return "ZEnv{" +
				"zDirOrJar='" + zDirOrJar + '\'' +
				", " + Rt.buildReport(zJars, "zJars:") +
				'}';
	}

	public static ZEnv ofGlobalAppVol() {
		return ofFileJarOrDir(Env.APPVOL_BIN_DIR);
	}

	public static ZEnv ofFileJarOrDir(Path zJarFileOrDir) {
		return new ZEnv(zJarFileOrDir);
	}

	public static final String ZMETHOD = "zmethod";
	public static final String ZPACKS = "zpacks";
	public static final String ZJAR = "zjar";
	public static final String ZDIR = "zdir";

	public static boolean hasArgs(String[] args) {
		return ARR.contains(args, ZMETHOD);
	}

	public static Object apply(String[] args) {
		SeqOptions opts = SeqOptions.of(args);
		String zMethod = opts.getSingleAs(ZMETHOD, String.class);
		Path zJarPath = opts.getSingleAs(ZJAR, Path.class);
		String[] zScanPacks = SPLIT.argsByComma(opts.getSingleAs(ZPACKS, String.class, ""));
		ZJar.of(zJarPath, zScanPacks).invokeWithArgs1(zMethod, args);
		return ARR.contains(args, ZMETHOD);
	}
}
