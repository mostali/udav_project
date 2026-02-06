package zk_page;

import lombok.SneakyThrows;
import mpc.fs.fd.RES;
import mpc.html.EHtml5;
import mpu.X;
import mpu.core.ARR;
import mpu.core.RW;
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

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZKPage {

	public static final Logger L = LoggerFactory.getLogger(ZKPage.class);

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
			renderHeadRsrc((IHeadCom) formModel);
		}
	}

	public static void renderHeadRsrcs(Window window, IHeadRsrc... headRsrcs) {
		renderHeadRsrcs((PageCtrl) window.getPage(), headRsrcs);
	}

	public static void renderHeadRsrc(IHeadCom... headCom) {
		Arrays.stream(headCom).forEach(c -> renderHeadRsrcs(ZKC.getFirstPageCtrl(), c.getHeadRsrc()));
	}

	public static void renderHeadRsrc(IHeadRsrc... headRsrcs) {
		renderHeadRsrcs(ZKC.getFirstPageCtrl(), headRsrcs);
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
			IHeadCom.updateStateAppendRsrc(attributes, headRsrc);
		}
	}

	public static void addStyleTag(Page page, String tagData, Object... args) {
		((PageCtrl) page).addBeforeHeadTags(EHtml5.style.with(tagData, args));
	}

	public static void addJsTag(Page page, String tagData, Object... args) {
		((PageCtrl) page).addBeforeHeadTags(EHtml5.script.with(tagData, args));
	}

	@Deprecated //What is import?
	@SneakyThrows
	public static void renderHeadPage_Rsrc(String path) {
		String js = RES.of(path).cat();
		ZKC.getFirstPageCtrl().addBeforeHeadTags(EHtml5.script.with(js));
	}

	@SneakyThrows
	public static void renderHeadPage(Path fileRsrc) {
		ZKC.getFirstPageCtrl().addBeforeHeadTags(RW.readString(fileRsrc));
	}

//	@Deprecated
//	public static void renderHeadPage_OLD(PageDirModel pageDir) {
//		Window window = ZKC.getFirstWindow();
//		PageCtrl pageCtrl = (PageCtrl) window.getPage();
////		pageCtrl.addBeforeHeadTags("<style type=\"text/javascript\">" + "function log(s){console.log(s);}" + "</style>");
////		pageCtrl.addBeforeHeadTags("<script type=\"text/javascript\">" + "function log2(s){console.log(s);}" + "</script>");
//
//		List<Path> headComs = pageDir.getAllHeadFiles(null, Collections.EMPTY_LIST);
//		for (Path path : headComs) {
//			EFT type = EFT.of(path, null);
//			switch (type) {
//				case FILE:
//					String ext = EXT.getExtFromFile(path, "").toLowerCase();
//					switch (ext) {
//						case "js": {
//							break;
//						}
////						case "reload": {
////							String scriptReload = "setTimeout(function(){location.reload();},3000)";
////							pageCtrl.addBeforeHeadTags("<script type=\"text/javascript\">" + scriptReload + "</script>");
////							break;
////						}
//						case "css":
//							boolean isStatic = UF.getNameWoExt(path.getFileName().toString(), "").endsWith(".static");
//							if (isStatic) {
//								pageCtrl.addBeforeHeadTags("<style type=\"text/javascript\">" + "function log(s){console.log(s);}" + "</style>");

	/// /								<link rel="stylesheet" type="text/css" href="/css/static-globalstyles.css"/>
//							} else {
//								String css = RW.readContent_(path, null, null);
//								if (X.notEmpty(css)) {
//									pageCtrl.addBeforeHeadTags("<style>" + css + "</style>");
//								} else {
//									if (L.isWarnEnabled()) {
//										L.warn("Head Css Resource '{}' is empty", path);
//									}
//								}
//							}
//							break;
//					}
//			}
//		}
//	}
	public static void renderHeadPageAndForms(PageSP pageSP) {
		if (pageSP instanceof WithHeadRsrc) {
			WithHeadRsrc withHeadRsrc = (WithHeadRsrc) pageSP;
			ZKPage.renderHeadRsrcs(pageSP.window(), withHeadRsrc.getHeadRsrcs());
		}
		renderHeadForms(pageSP.window());
	}

	public static void renderHeadForms(Window window) {
		List<Component> headComs = window.getChildren().stream().filter(c -> c instanceof IHeadCom).collect(Collectors.toList());
		renderHeadRsrcs_Forms(window, headComs);
	}

	public static void renderAllHeadForms(Window window) {
		List allComs = ZKCFinderExt.findAllInFirstWin0(HtmlBasedComponent.class, true, ARR.EMPTY_LIST);
		List<Component> headComs = (List<Component>) allComs.stream().filter(c -> c instanceof IHeadCom).collect(Collectors.toList());
		renderHeadRsrcs_Forms(window, headComs);
	}

}
