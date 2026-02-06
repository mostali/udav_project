package zk_page.index;

import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpu.core.ARG;
import mpu.pare.Pare;
import org.zkoss.zul.Window;
import mpe.call_msg.core.NodeID;
import zk_com.base.Dd;
import zk_form.notify.ZKI;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.PageState;
import zk_os.walkers.PagesWalker;
import zk_page.ZKC;

import java.nio.file.FileAlreadyExistsException;
import java.util.List;

@RequiredArgsConstructor
public abstract class PageDdChoicer extends BaseDdChoicer {

	final String sd3;

	private boolean withIndex = true;

	public PageDdChoicer withIndex(boolean... withIndex) {
		this.withIndex = ARG.isDefNotEqFalse(withIndex);
		return this;
	}

	public abstract void onChoicePage(String pagename);

	@Override
	protected void init() {
		super.init();

		List<Pare<String, PageState>> allPages = PagesWalker.doWalkToList(sd3, false);

		String setPage = "set page";
		List<String> choices = STREAM.mapToList(allPages, Pare::key);

		normalizeList(choices);

		Dd dd = new Dd(setPage, choices);
		dd.onSELECTION(e -> {
			String value = dd.getValue();
			if (!value.equals(setPage)) {
				try {
					onChoicePage(value);
				} catch (Exception ex) {
					L.error("onChoiceSd3Page", ex);
					if (ex instanceof FileAlreadyExistsException) {
						ZKI.alert("Note already exist in destination '%s'", value);
					} else {
						ZKI.alert(ex);
					}
					ZKC.removeTargetParentComponent(PageDdChoicer.this, Window.class);
				}
			}
		});
		appendChild(dd);

	}

	private void normalizeList(List<String> choices) {
		int i = choices.indexOf(NodeID.PAGE_INDEX_ALIAS);
		if (i < 0) {
			if (withIndex) {
				choices.add(0, NodeID.PAGE_INDEX_ALIAS);
			}
			return; //ok
		}
		if (i == 0) {
			if (!withIndex) {
				choices.remove(i);
			}
			return; //ok
		}
		choices.remove(i);
		choices.add(0, NodeID.PAGE_INDEX_ALIAS);
	}

	public Window openDefaultModalWindow(String winTitle) {
		return _title(winTitle)._closable()._modal()._showInWindow();
	}


}
