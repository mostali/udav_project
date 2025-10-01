package zk_form.tree;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.ctxdb.*;
import mpc.str.sym.SYMJ;
import mpe.str.CN;
import mpu.func.FunctionV;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;
import zk_com.base.Dd;
import zk_com.base.Lb;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.listbox.CellEditableValue;
import zk_com.listbox.Listbox0;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_page.ZKC;
import zk_page.ZKM;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class CtxTreeView extends Div0 {
	public final String pathDb;
	private final boolean isModal;
	private final AtomicReference<String> activeTable = new AtomicReference<>();

	public static CtxTreeView openAsModalWindow(Path pathDb) {
		CtxTreeView ctxTreeView = new CtxTreeView(pathDb.toString(), true);
		ctxTreeView.init();
		return ctxTreeView;
	}

	public static CtxTreeView createView(Path pathDb) {
		CtxTreeView ctxTreeView = new CtxTreeView(pathDb.toString(), false);
		ctxTreeView.init();
		return ctxTreeView;
	}

	@Override
	protected void init() {
		super.init();

		Path path = Paths.get(pathDb);

		AtomicReference<ICtxDb> treeDb = new AtomicReference<>();
		if (activeTable.get() == null) {
			activeTable.set(ICtxDb.getTablename(path));
		}
		treeDb.set(ICtxDb.of(path, activeTable.get()));

		if (!treeDb.get().isExistDb()) {
			ZKI.alert("empty data", ZKI.Level.WARN);
			return;
		}

		final AtomicReference<Pare<Component, Listbox0>> holderCom = new AtomicReference<>();

		FunctionV closerOrCleaner = () -> {
			if (holderCom.get() != null) {
				Component winOrDiv = holderCom.get().key();
				if (winOrDiv instanceof Window) {
					((Window) winOrDiv).onClose();
				} else {
					holderCom.get().key().getChildren().forEach(Component::detach);
				}
			}
		};

		ICtxDb iCtxDb = treeDb.get();
		Lb label = Lb.of("Db View");
		Ln closeDbView = (Ln) Ln.of(SYMJ.FAIL_RED_THINK).onCLICK(e -> closerOrCleaner.apply());
		Ln putNew = Ln.of(SYMJ.PLUS);
		Ln putRemove = Ln.of(SYMJ.MINUS);
		Dd choiceTable = new Dd<String>(iCtxDb.toDb().getAllTableNames());
		Div0 cap = Div0.of(closeDbView, label, Xml.NBSP(3), putNew, Xml.NBSP(2), putRemove, Xml.NBSP(2), choiceTable);

		FunctionV rebuilder = () -> {
			Component parent = isModal ? null : this;
			closerOrCleaner.apply();
			Listbox0 listbox = Listbox0.fromCtxDb(path, activeTable.get());
			if (parent == null) {
				holderCom.set(Pare.of(ZKM.showModal(cap, listbox, ZKC.getFirstWindow(), new String[]{"100%", "6334px"}), listbox));
			} else {
				parent.appendChild(cap);
				parent.appendChild(listbox);
//				ZKS.HEIGHT((HtmlBasedComponent) parent, 6333);
				holderCom.set(Pare.of(parent, listbox));
			}
//			ZKS.HEIGHT(listbox, 6333);
		};

		rebuilder.apply();

		//
		putNew.onCLICK(pe -> {
			treeDb.get().put(null);
			rebuilder.apply();
		});

		putRemove.onCLICK(re -> {
			Listbox0 listbox0 = holderCom.get().val();
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
							ZKI_Quest.showMessageBoxBlueYN(msg, msg, y -> {
								if (y) {
									treeDb.get().removeById(id);
									ZKI.infoAfterPointer("Deleted row#" + id + "", ZKI.Level.INFO);
									rebuilder.apply();
								}
							});
						}
					}
				}
			}
		});


		choiceTable.onCHANGE(e -> {

			String tablename = choiceTable.getValue();

			Window window = null;
			switch (tablename) {
				case ICtxDb.D10:
					treeDb.set(Ctx10Db.of(path));
					break;
				case ICtxDb.D5:
					treeDb.set(Ctx5Db.of(path));
					break;
				case Ctx3Db.CtxModelCtr.TN_DATAWT:
					treeDb.set(Ctx3Db.of(path));
					break;
				default:
					window = ZKM.showModal("SqliteDb", Listbox0.fromSqliteDb(path, tablename), ZKM.WH100);
					break;
			}
			if (window == null) {
				activeTable.set(tablename);
				rebuilder.apply();
			}

		});

	}
}
