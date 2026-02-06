package zk_com.base;


import lombok.SneakyThrows;
import org.zkoss.sound.AAudio;
import org.zkoss.zul.Audio;
import org.zkoss.zul.Div;
import zk_com.core.IZCom;
import zk_page.ZKC;

import java.io.File;
import java.nio.file.Path;

public class Mp3 extends Audio implements IZCom {

	public Mp3(String src) {
		super(src);
	}

	public Mp3(Path file) {
		this(file.toFile());
	}

	@SneakyThrows
	public Mp3(File file) {
		setContent(new AAudio(file));
		setControls(true);
	}

	public Mp3() {
	}

	public static Div wrapDiv(Mp3 mp3) {
		return ZKC.newDiv(mp3);
	}

	public static Div wrapDiv(String src) {
		return ZKC.newDiv(new Mp3(src));
	}
}
