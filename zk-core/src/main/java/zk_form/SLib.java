package zk_form;

import lombok.RequiredArgsConstructor;
import mpe.db.Db;
import mp.utl_odb.tree.UTree;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.map.MAP;
import mpc.rfl.RFL;
import mpu.IT;
import mpu.X;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.*;
import zk_com.base_ctr.Div0;
import zk_com.ext.Ddl;
import zk_com.listbox.Listbox0;
import zk_com.win.EventShowFileComInModal;
import zk_form.notify.ZKI;
import zk_notes.coms.SingleNodeVideo;
import zk_notes.search.NoteBandbox;
import zk_notes.search.engine.NoteSearchEngine;
import zk_os.coms.AFCC;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class SLib {
	public final Path path;
	public final int code;

	public static final int UNDEFINED = 0;
	public static final int TEXT = 1;
	public static final int JSON = 10;
	public static final int IMG = 100;
	public static final int AUDIO = 101;
	public static final int VIDEO = 102;
	public static final int SQLITE = 200;

	public static final int SEARCHDD = 201;

	public static void main(String[] args) {
		X.exit(name(1));
	}

	private static String name(int code) {
		Map<String, Integer> codes = RFL.fieldValuesMap(SLib.class, null, int.class, false);
		return MAP.getKey(codes, code);
	}

	public static SLib of(Path path) {
		return new SLib(path, asCode(path));
	}

	public static Integer asCode(Path path) {
		if (Files.isDirectory(path)) {
			return SEARCHDD;
		}
		EXT ext = EXT.of(path);
		if (GEXT.IMG.has(ext)) {
			return IMG;
		} else if (GEXT.EDITABLE.has(ext)) {
			return TEXT;
		} else if (path.getFileName().toString().endsWith(".props..")) {
			return JSON;
		} else if (GEXT.AUDIO.has(ext)) {
			return AUDIO;
		} else if (GEXT.VIDEO.has(ext)) {
			return VIDEO;
		} else if (ext == EXT.SQLITE) {
			return SQLITE;
		}
		return UNDEFINED;
	}

	public EventShowFileComInModal toEventShowInModal() {

		String pathStr = AFCC.relativizeAppFile(path, null);
		if (pathStr == null) {
			pathStr = path.getFileName().toString();
		}
		String title = pathStr;

		EventShowFileComInModal eventOpenFileInModal;

		switch (code) {
			case JSON:
				eventOpenFileInModal = new EventShowFileComInModal(title, () -> toCom(), true);
				break;
			case SQLITE:
				if (isTreeDbType()) {
					eventOpenFileInModal = new EventShowFileComInModal(title, () -> toCom());
				} else {
					Ddl ddl = new Ddl(Db.getAllSqliteTableNames(path)) {
						@Override
						public boolean onHappensClickItem(MouseEvent e, Object value) {
							ZKI.infoAfterPointer("Show:" + value);
//							ZKM.showModal( Listbox0.fromSqliteDb(path))
							return super.onHappensClickItem(e, value);
						}
					};
					eventOpenFileInModal = new EventShowFileComInModal(ddl, () -> toCom());
				}

				break;

			case TEXT:
			case IMG:
			case AUDIO:
			case VIDEO:
			case SEARCHDD:
			case UNDEFINED:
				eventOpenFileInModal = new EventShowFileComInModal(title, () -> toCom());
				break;
			default:
				throw new WhatIsTypeException(name(code) + ":" + path);
		}

		if (eventOpenFileInModal != null) {
			return eventOpenFileInModal;
		}
		ZKI.alert("Not found handler");
		String finalPathStr = pathStr;
		return new EventShowFileComInModal(title, () -> new Lb("Not found handler:" + finalPathStr));

	}

	private boolean isTreeDbType() {
		return UTree.isExistTreeModel(path);
	}

	public HtmlBasedComponent toCom() {
		switch (code) {
			case SEARCHDD:
				return NoteBandbox.of(IT.isDirExist(path), NoteSearchEngine.SearchNoteMode.FILE);
			case TEXT:
				return new Tbxm(path, Tbx.DIMS.WH100).saveble();
			case JSON:
				return new Tbxm(path, Tbx.DIMS.WH100).prettyjson(true).saveble();
			case IMG:
				return new Img(path);
			case AUDIO:
				return new Mp3(path);
			case VIDEO:
				return new SingleNodeVideo(path.toFile());
			case SQLITE:
				if (isTreeDbType()) {
					return Listbox0.fromCtxDb(path);
				}
				return Listbox0.fromSqliteDb(path);

			case UNDEFINED:
				Supplier<HtmlBasedComponent> getter = () -> {
					Div0 divWith = new Div0();
					divWith.setVflex("1");
					Lb lb = new Lb("What is type '%s'?", path);
					Bt btOpen = new Bt("Open");
					btOpen.onCLICK((SerializableEventListener<Event>) event -> {
						Tbx writable = new Tbxm(path, Tbx.DIMS.WH100).saveble();
						divWith.appendChild(writable);
						btOpen.detach();
						lb.detach();
					});
					divWith.appendChild(lb);
					divWith.appendChild(btOpen);
					return divWith;
				};
				return getter.get();

			default:
				throw new WhatIsTypeException(code + ":" + path);
		}
	}
}
