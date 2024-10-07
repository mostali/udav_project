package zk_page;

import lombok.SneakyThrows;
import mpu.X;
import mpu.core.ARR;
import mpu.core.RW;
import mpc.fs.UF;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpc.fs.fd.RES;
import mpc.html.EHtml5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.Window;
import zk_form.WithHeadRsrc;
import zk_form.head.IHeadCom;
import zk_form.head.IHeadRsrc;
import zk_page.core.PageSP;
import zk_old_core.mdl.pageset.HeadFileModel;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.mdl.pageset.HeadFileType;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZkPage {

	public static final Logger L = LoggerFactory.getLogger(ZkPage.class);

	public static Window getWindow() {
		return ZKC.getFirstWindow();
	}

	public static void renderHeadRsrcs_Forms(Window window, List<Component> components) {
		if (X.empty(components)) {
			return;
		}
		PageCtrl pageCtrl = (PageCtrl) window.getPage();
		for (Component formModel : components) {
			if (!(formModel instanceof IHeadCom)) {
				continue;
			}
			IHeadCom headCom = IHeadCom.class.cast(formModel);
			renderHeadRsrc_Form(pageCtrl, headCom);
		}
	}

	public static void renderHeadRsrc_Form(PageCtrl pageCtrl, IHeadCom headCom) {
		renderHeadRsrcs(pageCtrl, headCom.getHeadRsrc());
	}

	public static void renderHeadRsrcs(Window window, IHeadRsrc... headRsrcs) {
		renderHeadRsrcs((PageCtrl) window.getPage(), headRsrcs);
	}

	public static void renderHeadRsrcs(PageCtrl pageCtrl, IHeadRsrc... headRsrcs) {
		if (X.empty(headRsrcs)) {
			return;
		}
		Page page = ZKC.getFirstPage();
		Map<String, Object> attributes = page.getAttributes();
		for (IHeadRsrc headRsrc : headRsrcs) {
			if (IHeadCom.isAppendRsrc(attributes, headRsrc)) {
				continue;
			}
			addHeadRsrcToPage(pageCtrl, headRsrc, attributes);
		}
	}

	private static void addHeadRsrcToPage(PageCtrl pageCtrl, IHeadRsrc headRsrc, Map<String, Object> attributes) {
		String headData = headRsrc.toHeadContent();
		pageCtrl.addBeforeHeadTags(headData);
		if (attributes != null) {
			IHeadCom.updateStateAppendRsrc(attributes, headRsrc, true);
		}
	}

	public static void addStyleTag(Page page, String tagData, Object... args) {
		((PageCtrl) page).addBeforeHeadTags(EHtml5.style.wrap(X.f(tagData, args)));
	}

	public static void addJsTag(Page page, String tagData, Object... args) {
		((PageCtrl) page).addBeforeHeadTags(EHtml5.script.wrap(X.f(tagData, args)));
	}

	public static void renderHeadPage_Static(Window window, List<HeadFileModel> headFileModels) {
		if (X.empty(headFileModels)) {
			return;
		}
		PageCtrl pageCtrl = (PageCtrl) window.getPage();
		//		PageImpl pageImpl = (PageImpl) pageCtrl;
		for (HeadFileModel headFileModel : headFileModels) {
			HeadFileType headFileType = headFileModel.getHeadType();
			String fileData = headFileModel.getFileData();
			if (X.empty(fileData)) {
				continue;
			}
			String headData = headFileType.toHeadData(fileData);
			pageCtrl.addBeforeHeadTags(headData);
		}
	}


	@SneakyThrows
	public static void renderHeadPage_Rsrc(String path) {
		Window window = ZKC.getFirstWindow();
		PageCtrl pageCtrl = (PageCtrl) window.getPage();
		String js = RES.of(path).cat();
		pageCtrl.addBeforeHeadTags(EHtml5.script.wrap(js));
	}

	@Deprecated
	public static void renderHeadPage_OLD(PageDirModel pageDir) {
		Window window = ZKC.getFirstWindow();
		PageCtrl pageCtrl = (PageCtrl) window.getPage();
//		pageCtrl.addBeforeHeadTags("<style type=\"text/javascript\">" + "function log(s){console.log(s);}" + "</style>");
//		pageCtrl.addBeforeHeadTags("<script type=\"text/javascript\">" + "function log2(s){console.log(s);}" + "</script>");

		List<Path> headComs = pageDir.getAllHeadFiles(null, Collections.EMPTY_LIST);
		for (Path path : headComs) {
			EFT type = EFT.of(path, null);
			switch (type) {
				case FILE:
					String ext = EXT.getExtFromFile(path, "").toLowerCase();
					switch (ext) {
						case "js": {
							if (path.getFileName().toString().equals(ZKJS.FN_PRETTIFY_JS)) {

//								String v2="<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js\"></script>";
//								String v2="<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?skin=sunburst&autorun=true;lang=java\"></script>";
//								pageCtrl.addBeforeHeadTags("<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js\"></script>");
//								String v2 = "<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;lang=java&amp;skin=sunburst\"></script>";
								String v2 = "<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;lang=kotlin&amp;skin=sunburst\"></script>";
//								String v2 = "<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;skin=sunburst\"></script>";
//								https://github.com/googlearchive/code-prettify/tree/master/src
								pageCtrl.addBeforeHeadTags(v2);

							}
							break;
						}
//						case "reload": {
//							String scriptReload = "setTimeout(function(){location.reload();},3000)";
//							pageCtrl.addBeforeHeadTags("<script type=\"text/javascript\">" + scriptReload + "</script>");
//							break;
//						}
						case "css":
							boolean isStatic = UF.getNameWoExt(path.getFileName().toString(), "").endsWith(".static");
							if (isStatic) {
								pageCtrl.addBeforeHeadTags("<style type=\"text/javascript\">" + "function log(s){console.log(s);}" + "</style>");
//								<link rel="stylesheet" type="text/css" href="/css/static-globalstyles.css"/>
							} else {
								String css = RW.readContent_(path, null, null);
								if (X.notEmpty(css)) {
									pageCtrl.addBeforeHeadTags("<style>" + css + "</style>");
								} else {
									if (L.isWarnEnabled()) {
										L.warn("Head Css Resource '{}' is empty", path);
									}
								}
							}
							break;
					}
			}
		}
	}


	public static void renderHeadPageAndForms(PageSP pageSP) {
		if (pageSP instanceof WithHeadRsrc) {
			WithHeadRsrc withHeadRsrc = (WithHeadRsrc) pageSP;
			ZkPage.renderHeadRsrcs(pageSP.window(), withHeadRsrc.getHeadRsrcs());
		}
		renderHeadForms(pageSP.window());
	}

	public static void renderHeadForms(Window window) {
		List<Component> headComs = window.getChildren().stream().filter(c -> c instanceof IHeadCom).collect(Collectors.toList());
		renderHeadRsrcs_Forms(window, headComs);
	}
	public static void renderAllHeadForms(Window window) {
		List allComs = ZKCF.findAllInWin(HtmlBasedComponent.class, true, ARR.EMPTY_LIST);
		List<Component> headComs = (List<Component>) allComs.stream().filter(c -> c instanceof IHeadCom).collect(Collectors.toList());
		renderHeadRsrcs_Forms(window, headComs);
	}

}
