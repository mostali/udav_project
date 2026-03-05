package zk_page.index;

import mpc.fs.UFS;
import mpu.core.ARRi;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;
import zk_form.xform.Xform;
import zk_notes.node.NodeDir;
import zk_os.coms.SpaceType;
import zk_os.core.Sdn;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public abstract class ItemDdChoicer extends BaseDdChoicer {

//	final String plane, page;

	//	public ItemDdChoicer(String plane, String page) {
//		this.plane = plane;
//		this.page = page;
//	}
	private Predicate<Path> filterItems = null;

	public ItemDdChoicer withFilterItems(Predicate<Path> filterItems) {
		this.filterItems = filterItems;
		return this;
	}

	public abstract void onChoiceItem(Event onItemSubmitEvent, String plane, String page, Map<NodeDir, Boolean> items);

	@Override
	protected void init() {
		super.init();

		AtomicReference<Window> planeWin = new AtomicReference<>();
		AtomicReference<Window> pageWin = new AtomicReference<>();

		PlaneDdChoicer planeChoice = new PlaneDdChoicer() {
			@Override
			public void onChoicePlane(Event onPlaneSubmitEvent, String plane) {

				planeWin.get().onClose();

				PageDdChoicer pageChoice = new PageDdChoicer(plane) {
					@Override
					public void onChoicePage(Event onPageSubmitEvent, String pagename) {

						Sdn sdnWithPage = Sdn.ofPlane(plane).toSdnWithPage(pagename);
						Set<Path> plylistsFromPage = SpaceType.NODES.lsView(sdnWithPage, filterItems);

						pageWin.get().onClose();

						Xform f = Xform.ofItemsCb(UFS.toFileNames(plylistsFromPage));
						Window window = f._modal()._title("Choice items..")._closable()._showInWindow();
						FunctionV1<Event> eventFunctionV1 = submitCallbackEvent -> {
							Map<String, Boolean> xformItems = (Map) f.getMapModel();
							window.onClose();
							Map<NodeDir, Boolean> added = new HashMap<>();
							xformItems.forEach((node, is) -> {
								Path choiceNodeDir = ARRi.first(plylistsFromPage).getParent().resolve(node);
								added.put(NodeDir.ofDir(sdnWithPage, choiceNodeDir), is);
							});
							onChoiceItem(submitCallbackEvent, plane, pagename, added);
						};

						f.withCallback(eventFunctionV1);

					}
				};
//				Window newValue = pageChoice._modal(Window.Mode.EMBEDDED)._showInWindow(ItemDdChoicer.this);
				Window newValue = pageChoice.openDefaultModalWindow("Choice page..");
				pageWin.set(newValue);
			}
		};


		Window newValue = planeChoice._modal(Window.Mode.EMBEDDED)._showInWindow(ItemDdChoicer.this);
//		Window newValue = planeChoice.openDefaultModalWindow("Choice plane..");
		planeWin.set(newValue);

	}


	public Window openDefaultModalWindow(String winTitle) {
		return _title(winTitle)._closable()._modal()._showInWindow();
	}
}
