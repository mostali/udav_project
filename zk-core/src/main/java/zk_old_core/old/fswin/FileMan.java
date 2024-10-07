//package zk_form.fswin;
//
//import com.google.gson.JsonSyntaxException;
//import mpc.UC;
//import mpc.fs.ext.EXT;
//import mpc.fs.ext.GEXT;
//import mpc.fs.UDIR;
//import mpc.fs.fd.EFT;
//import mpc.json.UGson;
//import mpc.string.sym.FD_ICON;
//import mpc.string.sym.SYMJ;
//import mpc.string.US;
//import org.jetbrains.annotations.NotNull;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.SerializableEventListener;
//import zk_com.window.EventShowModal;
//import zk_com.base.*;
//import zk_com.ext.DivWith;
//import zk_os.notify.ZKNotify;
//import zk_page.ZKS;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Collections;
//import java.util.List;
//
//public class FileMan extends DivWith {
//
//	private final String dir;
//	private transient Path path;
//
//	public Path path() {
//		return path == null ? (path = Paths.get(dir)) : path;
//	}
//
//	public FileMan(Path dir) {
//		this.dir = dir.toString();
//		this.path = dir;
//	}
//
//	public static Component of(Path dir) {
//		UC.isDirExist(dir);
//		return new FileMan(dir);
//	}
//
//	@Override
//	protected void init() {
//		super.init();
//		appendLabelBlock(SYMJ.DIR_OPEN + path());
////		appendChild(new DirView(path()));
//	}
//
//
//
//
//}
