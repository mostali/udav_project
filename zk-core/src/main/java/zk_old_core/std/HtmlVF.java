package zk_old_core.std;

import mpu.core.RW;
import zk_com.base.Xml;
import zk_com.editable.EditableValueFilePrettyPrint;
import zk_old_core.std_core.CType;
import zk_old_core.mdl.FdModel;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

public class HtmlVF extends AbsVF {

	public HtmlVF(FdModel fdm) {
		super(fdm);
	}

	public static final String[] EXTS = {"html"};

	@Override
	public String[] getAllowedExt() {
		return EXTS;
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}

	@Override
	public void invalidatePartial() {
		super.invalidatePartial();
	}

	@Override
	protected void redrawChildren(Writer out) throws IOException {
		super.redrawChildren(out);
	}

	@Override
	public void redraw(Writer out) throws IOException {
		super.redraw(out);
	}

	@Override
	public String getHtmlData() {
		return RW.readContent(getRootChilds().get(0));
	}

	@Override
	protected void initImpl() throws Exception {

		CType ctype = ctype();
		Path path = path();

		List<Path> mainChilds = getRootChilds();
		for (Path child : mainChilds) {
			switch (getViewMode()) {
				case view:
					appendChild(Xml.buildComponentFromFile(child));
					break;
				case edit:
					EditableValueFilePrettyPrint ed = EditableValueFilePrettyPrint.build(child);
					appendChild(ed);
					break;
				case error:
				default:
					appendLb("Empty:" + name());
					break;

			}
		}


	}

//	//
//	//
//	//
//	private void addContextMenu(SpanCtx parent) {
//		addContextItem_REMOVE(parent);
//		addContextItem_HIGHLIGHT(parent);
//	}
//
//	public void addContextItem_REMOVE(SpanCtx parent) {
//		Menuitem rmm = new Menuitem("Remove");
//		rmm.addEventListener(Events.ON_CLICK, new EventRmmForm(path()));
//		parent.addContextMenuItem(rmm);
//	}
//
//	public void addContextItem_HIGHLIGHT(SpanCtx parent) {
//		Menuitem higlight = new Menuitem("Highlight");
//		higlight.addEventListener(Events.ON_CLICK, new EventHighlightForm(parent));
//		parent.addContextMenuItem(higlight);
//	}

}
