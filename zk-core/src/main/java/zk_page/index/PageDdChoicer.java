package zk_page.index;

import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpu.pare.Pare;
import org.zkoss.zul.Window;
import udav_net.apis.zznote.ItemPath;
import zk_com.base.Dd;
import zk_form.notify.ZKI;
import zk_notes.node_state.FormState;
import zk_os.walkers.PagesWalker;
import zk_page.ZKC;

import java.nio.file.FileAlreadyExistsException;
import java.util.List;

@RequiredArgsConstructor
public abstract class PageDdChoicer extends BaseDdChoicer {

	final String sd3;

	public abstract void onChoicePage(String pagename);

	@Override
	protected void init() {
		super.init();

		List<Pare<String, FormState>> allPages = PagesWalker.doWalkToList(sd3, false);

		String setPage = "set page";
		List<String> choices = STREAM.mapToList(allPages, Pare::key);
		if (choices.contains(ItemPath.PAGE_INDEX_ALIAS)) {
			choices.add(0, ItemPath.PAGE_INDEX_ALIAS);
		}
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
}
