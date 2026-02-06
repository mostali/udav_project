package zk_notes.events;

import mpc.fs.ext.EXT;
import mpu.IT;
import mpu.X;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_notes.ANI;
import zk_notes.node.NodeDir;
import zk_notes.fsman.NodeFileTransferMan;
import zk_os.core.Sdn;
import zk_page.index.PageDdChoicer;
import zk_page.index.RSPath;
import zk_page.index.PlaneDdChoicer;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class AppEventsPage extends AppEvents {

	public static Pare<String, SerializableEventListener> applyEvent_EditPageProps(Component com, Path pageState, String... event) {
		return apply(Pare.of(ANI.PROPS_ZZZ+ " Page Props", (Event e) -> ZKI.infoEditorDark(pageState, EXT.JSON)), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_MovePageToOtherPlane(Component com, Pare<String, String> sdn, String... event) {
		if (!Sdn.existPage(sdn)) {
			return null;
		}
		return apply(Pare.of(ANI.MOVE_FILE + " Move to other plane", (Event e) -> {
			AtomicReference<Window> winRef = new AtomicReference<>();
			String title = X.f("Move page '%s'", sdn.val());
			PlaneDdChoicer sd3Choicer = new PlaneDdChoicer() {
				@Override
				public void onChoiceSd3(String dstSd3) {
					String sd3 = sdn.key();
					String pagename = sdn.val();
					FunctionV1<Boolean> func = (rslt) -> {
						if (rslt) {
							NodeFileTransferMan.movePageToSd3(sd3, pagename, dstSd3);
							if (Sdn.isEmptyPlane_WoCheckIndex()) {
								RSPath.toPlane_Redirect(dstSd3);
							} else {
								RSPath.toPage_Redirect(dstSd3, pagename);
							}
						}
					};
					String message = X.f("Move page '%s' to plane '%s'", pagename, dstSd3);
					ZKI_Quest.showMessageBoxBlueYN(title, message, func);
					winRef.get().onClose();
				}
			};
			Window window = sd3Choicer.openDefaultModalWindow(title);
			winRef.set(window);

		}), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_MoveNode2Page(Component com, Pare<String, String> sdn, String formname, String... event) {
		NodeDir srcNode = NodeDir.ofNodeName(sdn, formname);
		if (!srcNode.existNode(false)) {
			return null;
		}
		return apply(Pare.of(ANI.MOVE_FILE + " Move to other page", (Event e) -> {

			AtomicReference<Window> planeWinRef = new AtomicReference<>();
			AtomicReference<Window> pageWinRef = new AtomicReference<>();

			String title = X.f("Move note '%s' - choice destination plane", formname);

			Pare<String, Boolean> opt = Pare.of("Redirect to destination", false);

			PlaneDdChoicer sd3DdChoicer = new PlaneDdChoicer() {

				@Override
				protected void init() {
					super.init();
					getOpts().add(opt);
				}

				@Override
				public void onChoiceSd3(String dstSd3) {

					FunctionV1<Boolean> func = (yesPlane) -> {
						if (yesPlane) {

							planeWinRef.get().onClose();

							String titleSd3 = X.f("Move note '%s' - choice destination page", formname);


							PageDdChoicer choicerPage = new PageDdChoicer(dstSd3) {

								@Override
								protected void init() {
									super.init();
									super.getOpts().add(opt);
								}

								@Override
								public void onChoicePage(String dstPagename) {

									NodeDir dstNode = NodeDir.ofNodeName(Sdn.of(dstSd3, dstPagename), formname);
									FunctionV1<Boolean> func = (yesPage) -> {
										if (yesPage == null) {
											return;
										}
										IT.state(!dstNode.existNode(false), "Note %s already exited", dstNode);
										NodeFileTransferMan.moveItemNote(srcNode, dstNode);
										(yesPage ? dstNode : srcNode).sdn().redirectTo();
									};

									String message = X.f("Move note '%s' to '%s' (with redirect=no)", formname, dstNode);
//									ZKI_Messagebox.showMessageBoxBlueYN(titleSd3, message, func);
									ZKI_Quest.showMessageBoxYNC_ofLevel(titleSd3, message, func);
									pageWinRef.get().onClose();
								}
							};

							Window window = choicerPage.openDefaultModalWindow(titleSd3);
							pageWinRef.set(window);

						}
					};

//					ZKI_Messagebox.showMessageBoxBlueYN(title, title, func);
					func.apply(true);

				}
			};

			PlaneDdChoicer choicerSd3 = sd3DdChoicer;
			Window window = choicerSd3._title(title)._closable()._modal()._showInWindow();
			planeWinRef.set(window);
		}), com, event);
	}
}
