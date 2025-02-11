package zk_notes.events;

import mpc.fs.ext.EXT;
import mpu.X;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Messagebox;
import zk_notes.ANI;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_os.core.Sdn;
import zk_page.core.SpVM;
import zk_page.index.RSPath;
import zk_page.index.Sd3DdChoicer;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class AppPageEvents extends AppEvents {

	public static Pare<String, SerializableEventListener> applyEvent_EditPageProps(Component com, Path pageState, String... event) {
		return apply(Pare.of(ANI.OPEN_SE_EDITOR + " Page Props", (Event e) -> ZKI.infoEditorBw(pageState, EXT.JSON)), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_MovePageToOtherPlane(Component com, Pare<String, String> sdn, String... event) {
		if (!Sdn.existPage(sdn)) {
			return null;
		}
		return apply(Pare.of(ANI.MOVE_FILE + " Move to other plane", (Event e) -> {

			AtomicReference<Window> winRef = new AtomicReference<>();

			String title = X.f("Move page '%s'", sdn.val());

			Sd3DdChoicer child = new Sd3DdChoicer() {
				@Override
				public void onChoicePath(String dstSd3) {
					String sd3 = sdn.key();
					String pagename = sdn.val();
					FunctionV1<Boolean> func = (rslt) -> {
						if (rslt) {
							NodeFileTransferMan.movePageToSd3(sd3, pagename, dstSd3);
							if (Sdn.isEmptySd3()) {
								RSPath.toPlane_Redirect(dstSd3);
							} else {
								RSPath.toPlanPage_Redirect(dstSd3, pagename);
							}
						}
					};
					String message = X.f("Move page '%s' to plane '%s'", pagename, dstSd3);
					ZKI_Messagebox.showMessageBoxBlueYN(title, message, func);
					winRef.get().onClose();
				}
			};


			Window window = child._title(title)._closable()._modal()._showInWindow();
			winRef.set(window);

		}), com, event);
	}
}
