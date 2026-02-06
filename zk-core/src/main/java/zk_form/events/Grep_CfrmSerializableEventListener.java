package zk_form.events;

import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.fs.UFS;
import mpu.str.TKN;
import mpe.rt_exec.GrepExecRq;
import mpe.rt.core.ExecRq;
import org.jetbrains.annotations.NotNull;
import zk_com.base.Mi;
import zk_notes.ANI;
import zk_form.notify.ZKI;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Grep_CfrmSerializableEventListener extends Tbx_CfrmSerializableEventListener {

	public Grep_CfrmSerializableEventListener(String title, String initText, @NotNull Function handlerInputValue) {
		super(title, initText, null, handlerInputValue);
	}

	public static Mi toMenuItemComponent(Path path, String initValue, Function<List<String>, Path>... successGrepCallback) {
		String title_cap_com = ANI.SEARCH_TEXT + "Grep " + (UFS.isDir(path) ? "dir" : "file") + " '" + path + "'";
		return toMenuItemComponent(title_cap_com, path, initValue, successGrepCallback);
	}

	public static Mi toMenuItemComponent(Object title_cap_com, Path path, String initValue, Function<List<String>, Path>... successGrepCallback) {
		boolean isDir = UFS.isDir(path);
		String label = ANI.SEARCH_TEXT + "Grep " + (isDir ? "dir.." : "file..");
		Mi menuItemComponent = Grep_CfrmSerializableEventListener.toMenuItemComponent(title_cap_com, label, initValue, "Search phrase", (String s) -> {
			try {
				List<String> lines = GrepExecRq.execGrepStringInDir(path, s);

				if (false) { //with log handle
					//LogGetterDate logGetterDate = LogGetterDate.buildByDefault();
					//Set<String> logslines = lines.stream().map(l -> TKN.lastGreedy(l, ':', "").trim()).filter(X::NE).collect(Collectors.toSet());
					////Date[] minMax = LogGetterDate.findFirstLastDateViaSet(logslines, logGetterDate);
					//Date[] minMax = LogGetterDate.findFirstLastDate(new ArrayList<>(logslines), logGetterDate, null);
				}

				Set<String> uniqFiles = lines.stream().map(l -> isDir ? TKN.first(l, ':', "").trim() : l).filter(X::NE).collect(Collectors.toSet());
				List<String> mergedLines = ARR.mergeToList( //
						uniqFiles, ARR.as("---------------first & last date------------"), //
//						ARR.as(logGetterDate.toString(minMax[0], null), logGetterDate.toString(minMax[1], null)), //
						ARR.as("---------------with content------------"), //
						lines //
				);

				ZKI.infoEditorDark(mergedLines);

				if (ARG.isDefNNF(successGrepCallback)) {
					Path pathTmpGrepLines = successGrepCallback[0].apply(mergedLines);
				}

			} catch (ExecRq e) {
				ZKI.alert(e);
			}
			return null;
		});
		return menuItemComponent;
	}

//	public static Mi toMenuItemComponentWithExclude_NW(Path path, String initValue) {
//		String titleWindow = ALI.SEARCH_TEXT + "Grep " + (UFS.isDir(path) ? "dir" : "file") + " '" + path + "'";
//		String label = ALI.SEARCH_TEXT + "Grep " + (UFS.isDir(path) ? "dir.." : "file..");
//		Mi menuItemComponent = Grep_CfrmSerializableEventListener.toMenuItemComponentWithExclude(titleWindow, label, initValue, (String in, String ex) -> {
//			try {
//				List<String> strings = GrepExecRq.execGrepStringInDir(path, s);
//				ZKL.infoEditorBw(strings);
//			} catch (ExecRq e) {
//				ZKL.alert(e);
//			}
//			return null;
//		});
//		return menuItemComponent;
//	}
}
