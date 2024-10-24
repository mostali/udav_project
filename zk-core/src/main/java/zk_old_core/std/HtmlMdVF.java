package zk_old_core.std;

import mp.utilspoi.UMd2Html;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import zk_com.base.Md;
import zk_com.base.Xml;
import zk_com.base_ctr.SpanCtx;
import zk_com.editable.EditableValueFilePrettyPrint;
import zk_old_core.std_core.CType;
import zk_old_core.mdl.FdModel;
import zk_old_core.events.EventRmmForm;

import java.nio.file.Path;
import java.util.List;

public class HtmlMdVF extends AbsVF {

	public HtmlMdVF(FdModel fdm) {
		super(fdm);
	}

	@Override
	public String[] getAllowedExt() {
		return new String[]{"md"};
	}

	@Override
	protected void initImpl() throws Exception {

		CType ctype = ctype();
		Path path = path();

		List<Path> mainChilds = getRootChilds();
		for (Path child : mainChilds) {
			switch (getViewMode()) {
				case view: {
					String htmlData = UMd2Html.buildHtml(child);
					appendChild(Xml.ofXml(htmlData));
					break;
				}
				case edit:
					EditableValueFilePrettyPrint ed = EditableValueFilePrettyPrint.build(child, Md.class);
					appendChild(ed);
				{
//					String htmlData = UMd2Html.buildHtml(child);
//					appendChild(Xml.of(htmlData));
					break;

				}
				case error:
				default:
					appendLb("Empty:" + name());
					break;

			}
		}


	}

	private void addContextItem_REMOVE(SpanCtx parent) {
		Menuitem rmm = new Menuitem("Remove");


		rmm.addEventListener(Events.ON_CLICK, new EventRmmForm(path()));
		parent.addContextMenuItem(rmm);
	}

//	private void addContextItem_HIGHLIGHT(SpanCtx parent) {
//		Menuitem higlight = new Menuitem("Highlight");
//		higlight.addEventListener(Events.ON_CLICK, new EventHighlightForm(parent, false));
//		parent.addContextMenuItem(higlight);
//
//	}

}
