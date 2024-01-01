package mpc.ui;

import lombok.RequiredArgsConstructor;
import mpc.ERR;
import mpc.Sys;

import java.nio.file.Path;

@RequiredArgsConstructor
public enum AppGui {
	BROWSER("chromium"), CODE("code"), IDEA("idea");
	public final String name;

	public void exe(Path file) {
		Sys.runQuicklyExecUnsafeSpace(code(file));
	}

	public String code(Path file) {
		switch (this) {
			default:
				return name + " " + ERR.isFileWithContent(file);
		}
	}
}
