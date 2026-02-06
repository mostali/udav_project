package mpc.ui;

import lombok.RequiredArgsConstructor;
import mpu.IT;
import mpu.Sys;

import java.nio.file.Path;

@RequiredArgsConstructor
public enum AppGui {
	BROWSER("chromium"), CODE("code"), IDEA("idea");
	public final String name;

	public void exe(Path file) {
		Sys.exec_UnsafeSpace(code(file));
	}

	public String code(Path file) {
		switch (this) {
			default:
				return name + " " + IT.isFileWithContent(file);
		}
	}
}
