package zk_old_core.std;

import mp.utilspoi.UDoc2Html;
import zk_com.base.Xml;
import zk_old_core.std_core.CType;
import zk_old_core.mdl.FdModel;

import java.nio.file.Path;
import java.util.List;

public class HtmlDocxVF extends AbsVF {

	public HtmlDocxVF(FdModel fdm) {
		super(fdm);
	}

	@Override
	public String[] getAllowedExt() {
		return new String[]{"docx"};
	}

	@Override
	protected void initImpl() throws Exception {

		CType ctype = ctype();
		Path path = path();

		List<Path> mainChilds = getRootChilds();
		for (Path child : mainChilds) {
			switch (getViewMode()) {
				case view: {
					String htmlData = UDoc2Html.buildHtml(child.toFile());
					appendChild(Xml.ofXml(htmlData));
					break;
				}
				case edit:
//					EditableValueFilePrettyPrint ed = EditableValueFilePrettyPrint.build(child);
//					appendChild(ed);
				{
					String htmlData = UDoc2Html.buildHtml(child.toFile());
					appendChild(Xml.ofXml(htmlData));
					break;

				}
				case error:
				default:
					appendLb("Empty:" + name());
					break;

			}
		}

	}

//	private void addContextItem_REMOVE(SpanCtx parent) {
//		Menuitem rmm = new Menuitem("Remove");
//
//
//		rmm.addEventListener(Events.ON_CLICK, new EventRmmForm(path()));
//		parent.addContextMenuItem(rmm);
//	}

//	private void addContextItem_HIGHLIGHT(SpanCtx parent) {
//		Menuitem higlight = new Menuitem("Highlight");
//		higlight.addEventListener(Events.ON_CLICK, new EventHighlightForm(parent, false));
//		parent.addContextMenuItem(higlight);
//
//	}

}
