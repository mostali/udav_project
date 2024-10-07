package zk_old_core.std;

import org.zkoss.sound.AAudio;
import org.zkoss.zul.Audio;
import zk_com.base.Xml;
import zk_old_core.std_core.CType;
import zk_old_core.mdl.FdModel;

import java.nio.file.Path;
import java.util.List;

public class Mp3VF extends AbsVF {

	public Mp3VF(FdModel fdm) {
		super(fdm);
	}

	public static final String[] EXTS = {"mp3"};

	@Override
	public String[] getAllowedExt() {
		return EXTS;
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
					Audio audio = new Audio();
					audio.setContent(new AAudio(child.toFile()));
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
