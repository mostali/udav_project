package zk_old_core.std;

import mpc.exception.NI;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.map.UMap;
import org.zkoss.sound.AAudio;
import org.zkoss.video.AVideo;
import org.zkoss.video.Video;
import org.zkoss.zul.Audio;
import zk_com.base.Xml;
import zk_old_core.std_core.CType;
import zk_old_core.mdl.FormDirModel;
import zk_page.ZulLoader;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class VideoVF extends AbsVF {

	public VideoVF(FormDirModel fdm) {
		super(fdm);
	}

	public static final String[] EXTS = {"mp4"};

	@Override
	public String[] getAllowedExt() {
		return EXTS;
	}

	@Override
	protected void initImpl() throws Exception {

		CType ctype = ctype();
		Path path = path();

		NI.stop("video unsupported");

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

		//https://www.zkoss.org/wiki/ZK_Component_Reference/Multimedia_and_Miscellaneous/Video
		//https://zkfiddle.org/tag/video;jsessionid=CDEDE5402A178FD751BC863874ED18E9
		if (true) {
			throw new NI(ctype);
		}
		Video video = (Video) ZulLoader.loadComponent("<video src=\"zk.mp4\" controls=\"true\" autoplay=\"true\" loop=\"true\"/>");
		Map<Path, EXT> fxMap = null;//fdModel.getMapExt();
		Path videoFile = UMap.getKey(fxMap, GEXT.SET_VIDEO, null);
		if (videoFile == null) {
			throw new IllegalStateException("Component need reinit c-type" + ctype + ": from file://" + null);
		}
		AVideo aVideo = new AVideo(videoFile.toFile());
		//Video video=new
		//audio.setContent(new AAudio(videoFile.toFile()));
		//dirComs.add(video);

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
