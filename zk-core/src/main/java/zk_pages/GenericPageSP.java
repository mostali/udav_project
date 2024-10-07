package zk_pages;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.exception.WhatIsTypeException;
import mpc.rfl.RFL;
import mpu.str.Rt;
import mpu.X;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.impl.PageImpl;
import org.zkoss.zul.Window;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_com.base_ext.SimpleBorderLayout;
import zk_com.sun_editor.SeWinOLD;
import zk_old_core.old.WithAgna;
import zk_form.WithLogo;
import zk_form.control.QuickCmdRunner;
import zk_old_core.control_old.TopAdminMenu;
import zk_old_core.old.fswin.FsWin;
import zk_old_core.old.mwin.MWin;
import zk_old_core.std.AbsVF;
import zk_page.events.ZKE;
import zk_old_core.sd.Sd3EE;
import zk_os.sec.MatrixAccess;
import zk_page.ZKC;
import zk_page.ZKS;
import zk_page.ZkPage;
import zk_old_core.events.EventChoiceFormInMWin;
import zk_page.behaviours.EventHighlightForm;
import zk_old_core.events.PageBehaviours;
import zk_old_core.events.PageRootProps;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_old_core.GenericViewPageComponent;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.mdl.PageLayout;
import zk_old_core.mdl.pageset.IFormModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dav 07.01.2022   19:24
 */
public class GenericPageSP extends PageSP implements WithLogo, WithAgna {

	private final PageDirModel pdm;

	protected boolean rebuild = false;

	public GenericPageSP rebuild(boolean... rebuild) {
		this.rebuild = ARG.isDefNotEqFalse(rebuild);
		return this;
	}

	private GenericPageSP(Window window, SpVM spVM) throws Sd3EE {
		super(window, spVM);
		this.pdm = spVM().findPageDirModel();
		pdm.setAttributeTo(window);
	}

	private GenericPageSP(Window window, SpVM spVM, Path file) {
		super(window, spVM);
		this.pdm = PageDirModel.of(file);
		pdm.setAttributeTo(window);
	}

	private GenericPageSP(Window window, PageDirModel pdm) {
		super(window, null);
		this.pdm = pdm;
	}

	public static GenericPageSP buildPage(Window window, PageDirModel pdm, boolean rebuild) {
		return (GenericPageSP) new GenericPageSP(window, pdm).rebuild(rebuild).buildPage();
	}

	public static GenericPageSP buildPage(Window window, SpVM spVM) throws Sd3EE {
		return (GenericPageSP) new GenericPageSP(window, spVM).buildPage();
	}

	public static GenericPageSP buildPage(Window window, SpVM spVM, Path file) throws Sd3EE {
		return (GenericPageSP) new GenericPageSP(window, spVM, file).buildPage();
	}

	@SneakyThrows
	public void buildPageImpl() {

		GenericViewPageComponent viewPage = new GenericViewPageComponent(window, pdm, rebuild);

		window.appendChild(viewPage);

		ZKS.PADDING0(window);

	}

	@Override
	protected void initAndAdd_AdgnaCom() {
		new TopAdminMenu().appendTo(window);
		new QuickCmdRunner().appendTo(window);
	}

	public static List<Component> getOrBuildComponents(List<IFormModel> pageForms, AbsVF.ViewMode viewMode) {
		if (L.isDebugEnabled()) {
			L.debug(">>>Build '{}' form-model's:\n" + Rt.buildReport(pageForms), X.sizeOf(pageForms));
		}
		List<Component> pageComponents = new ArrayList<>();
		for (IFormModel form : pageForms) {
			Component com = buildComponent(form, viewMode);
			if (com != null) {
				pageComponents.add(com);
			}
		}
		if (L.isDebugEnabled()) {
			L.debug("<<<Build '{}' form-components:\n" + Rt.buildReport(pageComponents), X.sizeOf(pageForms));
		}
		return pageComponents;
	}

	private static Component buildComponent(IFormModel form, AbsVF.ViewMode viewMode) {
		Exception errBuildForm = null;
		try {
			return AbsVF.build(form, viewMode);
		} catch (Exception ex) {
			errBuildForm = new IllegalStateException("Form:" + form.fd().name() + ":" + ex.getMessage(), ex);
		}

		if (viewMode == AbsVF.ViewMode.edit) {
			Lb e = new Lb("ErrorBC:" + form.ctype(null) + ":" + (errBuildForm == null ? null : errBuildForm.getMessage()));
			return ZKS.BLOCK(e);
		} else if (L.isErrorEnabled()) {
			L.error(X.fl("ErrorBC: {}", form), errBuildForm);
		}

		return null;
	}

	//
	//
	//
	public static void openStatableForms(PageDirModel pdm, boolean rebuild) {
		MWin.openOnLoadPage(pdm);
		FsWin.openOnLoadPage(pdm);
		if (!rebuild) {
			SeWinOLD.openOnLoadPage(pdm);
		}

	}

	public static void applyPageControl(PageDirModel pdm, List<Component> pageForms) {
		if (!MatrixAccess.EDITOR_FULL.hasAccess()) {
			return;
		}

		if (PageBehaviours.isEnable_Def0(pdm.getRootProps(), PageBehaviours.HIGHLIGHT_ON_OVER_OUT)) {
			pageForms.forEach(EventHighlightForm::applyOnOff_MouseOverOut);
		}

		if (PageBehaviours.isEnable_Def0(pdm.getRootProps(), PageBehaviours.OPEN_FORM_ON_DBL_CLICK)) {
			pageForms.forEach(EventChoiceFormInMWin::applyOnDblClick);
		}
		if (PageBehaviours.isEnable_Def0(pdm.getRootProps(), PageBehaviours.SHOW_CONTEXT_MENU)) {
			for (Component pageForm : pageForms) {
				if (pageForm instanceof AbsVF) {
					AbsVF.appendContextMenu((AbsVF) pageForm, true, true, true, true);
				}
			}
		}
	}

	public static void applyHeadMetaPage(Window window, Map<String, ?> props) {


		{ //HEAD
			PageImpl pageImpl = (PageImpl) window.getPage();
			String title = (String) props.get("title");
			if (title != null) {
				pageImpl.setTitle(title);
			}

			//		String description = props.getString("desc", null);
			//		if (description != null) {
			//			pageImpl.addBeforeHeadTags("<meta name=\"description\" content=\"" + description + "\">");
			//		}
			//		String charset = props.getString("charset", null);
			//		if (description != null) {
			//			pageImpl.addBeforeHeadTags("<meta charset = \"" + charset + "\" >");
			//		}
		}

	}

	public static HtmlBasedComponent buildLayoutContainer(Map<String, ?> props, List<Component> pageForms) {

		PageLayout pageLayoutType = PageRootProps.getPagePropAs((Map) props, PageRootProps.PK_LAYOUT, PageLayout.class, PageLayout.ABS);

		HtmlBasedComponent pageLayoutContainer;

		switch (pageLayoutType) {
			case BL: {

				SimpleBorderLayout b = SimpleBorderLayout.buildCom(true);
				ZKS.VFLEX_MIN(b);
				ZKS.VFLEX_MIN(b.getNorth(true));
				ZKS.VFLEX_MIN(b.getCenter(true));
				ZKS.VFLEX_MIN(b.getSouth(true));
//				ZKS.VFLEX_MIN(b.getWest(true));
//				ZKS.VFLEX_MIN(b.getEast(true));

				b.getWest(true).setFlex(true);
				b.getEast(true).setFlex(true);

				Div0 CENTER = b.CENTER();

				Div0 WEST = b.WEST();

//				ZKS.of(b.getWest()).bgcolor("lightgray").width(20);

				CENTER.appendChilds(pageForms);

				pageLayoutContainer = b;

				break;
			}
			case ABS: {
				pageLayoutContainer = Div0.of(pageForms);
				break;
			}

			default:
				throw new WhatIsTypeException(pageLayoutType);
		}

		for (Map.Entry<String, ?> entry : props.entrySet()) {
			String key = entry.getKey();
			switch (key) {
				case "class":
					pageLayoutContainer.setClass((String) entry.getValue());
					break;
				case "style":
					pageLayoutContainer.setStyle((String) entry.getValue());
					break;
				default:
					if (ZKE.isZkEventName(key)) {
						ZkPage.addJsTag(ZKC.getFirstPage(), (String) entry.getValue());
						break;
					}
					if (L.isWarnEnabled()) {
						L.warn("Page key '{}' with value '{}' was skipped", entry.getKey(), entry.getValue());
					}
			}
		}

		return pageLayoutContainer;
	}

	//
	//
	//
	@RequiredArgsConstructor
	public static class JsonApply {
		final Object src;

		private List<Method> methods;

		public List<Method> getMethods() {
			return methods == null ? (methods = ARR.as(src.getClass().getMethods())) : methods;
		}

//		public Object apply(JsonElement jsonElement) throws InvocationTargetException, IllegalAccessException {
//			jsonElement.getAsJsonPrimitive().issonPrimitive().get
//			return apply(methodName, AR.of(parameterType), AR.of(parameterValue), defRq);
//		}

		public Object apply(String methodName, Class parameterType, Object[] parameterValue, Object... defRq) throws InvocationTargetException, IllegalAccessException {
			return apply(methodName, ARR.of(parameterType), ARR.of(parameterValue), defRq);
		}

		public Object apply(String methodName, Class[] parameterTypes, Object[] parameterValues, Object... defRq) throws InvocationTargetException, IllegalAccessException {
			Method m;
			try {
				m = RFL.method_(src.getClass(), methodName, parameterTypes, false, true, false);
				return m.invoke(src, parameterValues);
			} catch (InvocationTargetException ite) {
				return X.throwException(ite.getCause());
			} catch (Exception ex) {
				return ARG.toDefThrow(ex, defRq);
			}
		}
	}
}
