package zk_notes.events;

import mp.utl_odb.tree.CtxtDb;
import mp.utl_odb.tree.UTree;
import mpc.arr.STREAM;
import mpc.env.APP;
import mpc.env.Env;
import mpc.exception.NotifyMessageRtException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.str.sym.SYMJ;
import mpe.str.CN;
import mpu.Sys;
import mpu.X;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;
import zk_com.base.Lb;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.listbox.CellEditableValue;
import zk_com.listbox.Listbox0;
import zk_form.dirview.FileView;
import zk_form.dirview.SimpleDirView;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Messagebox;
import zk_notes.ANI;
import zk_page.ZKM;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AppEventsFD extends AppEvents {


	//
	//

	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR_OS(Component com, Path path, String... event) {
		if (!UFS.exist(path) || !Env.isLocalDevMashine()) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir OS", SimpleDirView.getEventOpenSimpleMenu_OS(path)), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR(Component com, Path path, String... event) {
		if (!UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir", SimpleDirView.getEventOpenDirViewWithSimpleMenu(path)), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENFILE(Component com, Path path, String... event) {
		if (!UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir", FileView.getEventShowComInModal(path)), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENTREE(Component com, Path path, String... event) {
		if (!UFS.existFile(path)) {
			return null;
		}
		Pare<String, SerializableEventListener> enventDesc = Pare.of(ANI.OS_OPEN + " Open Tree " + UF.sfn(path).toUpperCase(), e -> {
			Lb label = Lb.of("Global Node Context ");
			Ln putNew = Ln.of(SYMJ.PLUS);
			Ln putRemove = Ln.of(SYMJ.MINUS);
			Div0 cap = Div0.of(label, Xml.NBSP(3), putNew, Xml.NBSP(2), putRemove);
			AtomicReference<Listbox0> listboxAtomic = new AtomicReference<>();
			Supplier<Window> windowsShow = () -> {
				if (UTree.tree(path).isEmptyDb()) {
					ZKI.infoAfterPointer("empty data", ZKI.Level.WARN);
					return null;
				}
				Listbox0 modalCom = Listbox0.fromTreeDb(path);
				listboxAtomic.set(modalCom);
				return ZKM.showModal(cap, modalCom);
			};
			Window window = windowsShow.get();
			//
			putNew.onCLICK(pe -> {
				UTree.tree(path).put(null);
				window.onClose();
				windowsShow.get();
			});
			putRemove.onCLICK(re -> {
				Listbox0 listbox0 = listboxAtomic.get();
				List<Component> children = listbox0.getChildren();
				for (Component child : children) {
					if (child instanceof Listitem) {
						boolean selected = ((Listitem) child).isSelected();
						if (selected) {
							CellEditableValue cellEditableValue = (CellEditableValue) child.getChildren().get(0).getFirstChild();
							String name = cellEditableValue.getVal().name();
							if (CN.ID.equals(name)) {
								Long id = (Long) cellEditableValue.getVal().getValue();
								String msg = "Remove row #" + id + " ?";
								ZKI_Messagebox.showMessageBoxBlueYN(msg, msg, y -> {
									if (y) {
										UTree.tree(path).deleteById(id);
										ZKI.infoAfterPointer("Deleted row#" + id + "", ZKI.Level.INFO);
										window.onClose();
										windowsShow.get();
									}
								});
							}
						}
					}
				}
			});
		});
		return apply(enventDesc, com, event);
	}


	public static Pare<String, SerializableEventListener> applyEvent_OPEN_IN_CODE(Component com, Path path, String... event) {
		return apply(Pare.of(ANI.OS_OPEN + " Open in Code", e -> Sys.open_Code(path)), com, event);
	}

}
