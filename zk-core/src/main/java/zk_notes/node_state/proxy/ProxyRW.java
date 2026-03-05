package zk_notes.node_state.proxy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.core.RW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_com.base.Tbx;
import zk_com.base.TbxmChild;
import zk_notes.node_state.EntityState;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.FormState;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public abstract class ProxyRW {

	public static final Logger L = LoggerFactory.getLogger(ProxyRW.class);

	private final @Getter String originalSrcPathStr;

	public static ProxyRW of(Tbx tbx) {
		ObjState formState = tbx.getFormState();
		if (tbx instanceof TbxmChild) {
			TbxmChild tbxmNext = (TbxmChild) tbx;
			formState = tbxmNext.getMasterChild(tbxmNext).getFormState();
		}
		return StateProxyRW.of(tbx.getPathStr(), formState);
	}

	static String FK_FROM_FILE(EntityState state, String... defRq) {
		return state.get(FormState.FK_FROM_FILE, defRq);
	}

	public static String FK_FROM_DIR(EntityState state, String... defRq) {
		return state.get(FormState.FK_FROM_DIR, defRq);
	}

	public abstract boolean hasProxyPath_FILE(boolean... existedFile);

	public abstract boolean hasProxyPath_DIR(boolean... existedFile);

	public String readContentOrEmpty() {
		return readContent("");
	}

	public String readContent(String... defRq) {
		return RW.readString(getTargetPath_FILE(), defRq);
	}

	@SneakyThrows
	public void writeContent(String content) {
		RW.write_(getTargetPath_FILE(), content, true);
	}

	public abstract Path getTargetPath_FILE(Path... defRq);

	public abstract Path getTargetPath_DIR(Path... defRq);

	public Path getOriginalPath() {
		return Paths.get(getOriginalSrcPathStr());
	}

	//
	//
	//

}
