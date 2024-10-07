package zk_old_core.old.per_win;

import mpu.Sys;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.GEXT;
import mpc.html.STYLE;
import mpc.rfl.RFL;
import mpc.str.condition.StringConditionType;
import mpc.str.sym.SYMJ;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.*;
import zk_com.base_ctr.Div0;
import zk_com.base_ext.EnumSwitcher;
import zk_com.sun_editor.IPerState;
import zk_com.sun_editor.SeWinOLD;
import zk_form.events.IPerDndEvent;
import zk_page.*;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PerWin extends Window implements IPerDndEvent {

	public static final String DEFAULT_BGCOLOR = "#f1f1f1";
	private static int ZINDEX = 9999;

	enum EyesOptions {
		RESET, PROPS, NOTHING
	}

	public static void reset(Class winClazz, Path fileState) {
		List<String> keys = getAllViewProps(winClazz);
		IPerWinStateRw stateRw = IPerWinStateRw.loadStateRw(fileState);
		for (String key : keys) {
			String val = stateRw.read(key, null);
			if (val == null) {
				stateRw.write(key, "1");
			}
		}
		stateRw.write(IPerWinStateRw.STATE.state, IPerWinStateRw.STATE_VALUE.def.name());
		SeWinOLD.writeDefaultPositionAndDims(stateRw, getDefaultBgColor(winClazz));
	}

	public static String getDefaultBgColor(Class clazz) {
		return (String) RFL.fieldValueSt(clazz, "DEFAULT_BGCOLOR", true);
	}

	public static String getMainViewName(Class clazz) {
		return (String) RFL.fieldValueSt(clazz, "VIEW_MAIN", true);
	}

	public static List<String> getAllViewProps(Class clazz) {
		return RFL.fieldValuesSt(clazz, String.class, StringConditionType.STARTS.buildCondition("VIEW_"), false);
	}

	public static <W extends Component> List<W> findAll(Class<W> clazz, List<W>... defRq) {
		return ZKCF.rootsByClass(clazz, true, defRq);
	}

	@Override
	public IPerWinStateRw getStateRw() {
		return IPerWinStateRw.loadStateRw(pathState());
	}

	private final String fileState;
	private transient Path pathState;

	public Path pathState() {
		return pathState == null ? (pathState = Paths.get(fileState)) : pathState;
	}

	public String nameState() {
		return pathState().getFileName().toString();
	}

	public static PerWin ofFileState(Path fileState) {
		return new PerWin(fileState);
	}

	public PerWin() {
		if (this instanceof IPerState) {
			IPerState iPerState = (IPerState) this;
			this.pathState = iPerState.getPathState(false, null, null, null);
			this.fileState = pathState.toString();
		} else {
			throw new RequiredRuntimeException("State is required, class '%s'", getClass());
		}
	}

	public PerWin(Path fileState) {
		this.fileState = fileState.toString();
		this.pathState = fileState;
	}


	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	public static void writeDefaultPositionAndDims(IPerWinStateRw stateRw, String bgcolor) {
		stateRw.write(IPerWinStateRw.LTWH.WIDTH, "1080px");
		stateRw.write(IPerWinStateRw.LTWH.HEIGHT, "360px");
		String left = stateRw.read(IPerWinStateRw.LTWH.LEFT, null);
		if (left == null) {
			stateRw.write(IPerWinStateRw.LTWH.LEFT, "40%");
		}
		String top = stateRw.read(IPerWinStateRw.LTWH.TOP, null);
		if (top == null) {
			stateRw.write(IPerWinStateRw.LTWH.TOP, "60%");
		}
		stateRw.write("bgcolor", bgcolor);
	}

	@Override
	public void onClose() {
		super.onClose();
		getStateRw().write(getMainViewName(getClass()), "0");
	}


	//https://forum.zkoss.org/question/64488/problem-in-styling-window-title/
	protected void init() {

		IPerWinStateRw stateRw = getStateRw();
		if (stateRw.isEmpty()) {
			stateRw.touchIfNotExist();
			reset(getClass(), pathState());
			stateRw.reset();
		} else {
			stateRw.write(getMainViewName(getClass()), "1");
		}

		getHeaderCaption().addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				ZKS.ZINDEX(PerWin.this, ZINDEX >= 999999 ? ZINDEX = 9999 : ++ZINDEX);
			}
		});
		ZKS.ZINDEX(this, ++ZINDEX);
//		setTitle("goo");
		setSizable(true);
		setContentStyle("padding:0px");
		//setStyle("padding:0px");

		IPerDndEvent.recoveryState(this, pathState());

		appendChild(getHeaderCaption());

		setClosable(true);

		if (bgColor == null) {
			bgColor = getStateRw().read(IPerWinStateRw.STATE.bgcolor, null);
		}
		if (bgColor != null) {
			ZKS.BGCOLOR(this, bgColor);
		}

		applyState(this, false);

		doOverlapped();
	}


	Caption headerCaption;

	public Caption getHeaderCaption() {
		if (headerCaption != null) {
			return headerCaption;
		}
		headerCaption = new Caption();
		headerCaption.appendChild(getHeaderCaptionContainer());
		return headerCaption;
	}

	CaptionMini capMin;

	public Component getHeaderCaptionContainer() {
		return getHeaderCaptionLogoMini();
	}

	public CaptionMini getHeaderCaptionLogoMini() {
		return capMin == null ? (capMin = new CaptionMini()) : capMin;
	}

//	@Override
//	public void onClose() {
//		ZKJS.onCloseJs(this);
//		super.onClose();
//	}

	public class CaptionMini extends Lb {
		public CaptionMini() {
			super(SYMJ.EYE);
//			super(SYMJ.EYE + "" + pathState().getFileName().toString());
//			Label label = new Label(SYMJ.EYE + "" + pathState().getFileName().toString());
			addEventListener(Events.ON_CLICK, getEventSwithcer());
		}
	}

	//	private Component mini;

	//	public Component getHeaderCaptionLogoMini() {
	//		Label label = new Label(SYMJ.EYE);
	//		label.addEventListener(Events.ON_CLICK, getEventSwithcer());
	//		label.setClass(getClass().getSimpleName().toLowerCase() + "_m");
	//		return label;
	//	}

	protected Component getDefaultReseterAndSwitcher(PageDirModel pageDirModel) {
		if (true) {
			getHeaderCaption().addEventListener(Events.ON_DOUBLE_CLICK, getEventSwithcer());
			return new EnumSwitcher(EyesOptions.class) {

				@Override
				protected void applyPosition(Enum typeValue) {
					switch ((EyesOptions) typeValue) {
						case RESET:
							reset(PerWin.this.getClass(), pathState());
							if (pageDirModel != null) {
								ZKR.rebuildPage(pageDirModel);
							}
							break;
						case PROPS:
							ZKME.openEditorImgOrText(pathState());
							break;
						case NOTHING:
							break;
						default:
							throw new WhatIsTypeException(typeValue);
					}
				}
			};

		}
		Lb switcher = new Lb(SYMJ.EYE);
		switcher.onCLICK(getEventSwithcer());
		switcher.onDblClick((SerializableEventListener<Event>) event -> {
			Sys.say("go");
			reset(getClass(), pathState());
			if (pageDirModel != null) {
				ZKR.rebuildPage(pageDirModel);
			}
		});
		return switcher;
	}

	protected SerializableEventListener<? extends Event> getEventSwithcer() {
		return event -> {
			applyState(this, true);
		};
	}

	public static void applyState(PerWin com, boolean swap) {

		IPerWinStateRw stateRw = com.getStateRw();

		String state = stateRw.read(IPerWinStateRw.STATE.state, "def");
		AtomicBoolean visible = new AtomicBoolean();

		//ZKNotify.ZLOG("ApplySt, swap(%s) / state(%s) / wh(%s) / visible (%s)", swap, state, U.toNiceString(wh), visible.get());

		String[] wh = null;

		if (!swap) {
			switch (state) {
				case "min":
					visible.set(false);
					wh = new String[]{"", ""};
					break;
				case "def":
					visible.set(true);
					wh = new String[]{stateRw.read(IPerWinStateRw.LTWH.WIDTH, "200px"), stateRw.read(IPerWinStateRw.LTWH.HEIGHT, "100px")};
					break;
				default:
					throw new WhatIsTypeException(state);
			}
		} else {
			switch (state) {
				case "min":
					visible.set(true);
					state = "def";
					wh = new String[]{stateRw.read(IPerWinStateRw.LTWH.WIDTH, "200px"), stateRw.read(IPerWinStateRw.LTWH.HEIGHT, "100px")};
					break;
				case "def":
					visible.set(false);
					state = "min";
					wh = new String[]{"", ""};
					break;
				default:
					throw new WhatIsTypeException(state);
			}
			stateRw.write(IPerWinStateRw.STATE.state, state);
		}

		if (visible.get()) {
			com.setContentStyle(STYLE.rmStyle(com.getContentStyle(), "display"));
		} else {
			com.setContentStyle(STYLE.addStyle(com.getContentStyle(), "display:none"));
		}

		com.getChildren().forEach(c -> {
			if (c instanceof Caption) {
				Caption cap = (Caption) c;
				List<Component> children = cap.getChildren();
//				if (children.size() > 1) {//hold only first component in Caption
				for (int i = 0; i < children.size(); i++) {
					children.get(i).setVisible(visible.get());
				}
//				}
			} else {
				c.setVisible(visible.get());
			}
		});

		List<Component> capChilds = com.getCaption().getChildren();
		Component capFirst = capChilds.get(0);

		if (capFirst instanceof CaptionMini) {
			if (visible.get()) {
				capChilds.remove(0);
			} else {
				//OK
			}
		} else {
			if (visible.get()) {
				//OK
			} else {
				capChilds.add(0, com.getHeaderCaptionLogoMini());
			}
		}


		com.setWidth(wh[0]);
		com.setHeight(wh[1]);

	}

	String bgColor;

	public PerWin bgcolor(String bgColor) {
		this.bgColor = bgColor;
		return this;
	}

	public void showMainProps() {
		showContent(pathState());
	}

	protected MainView mainView = null;

	private void rmMainViewComponent() {
		if (mainView != null) {
			ZKC.removeMeReturnParent(mainView);
		}
	}

	public void showContent(Path pathFile) {
		XulElement com;
		if (GEXT.IMG.hasIn(pathFile)) {
			com = new Img(pathFile);
		} else {
			com = new MainTbx(pathFile);
		}
		showComponent(com);
	}

	public void showComponent(XulElement component) {
		if (mainView != null) {
			ZKC.removeMeReturnParent(mainView);
		}
		component.setHeight("100%");
		mainView = new MainView(component);
		mainView.setHeight("100%");
		appendChild(mainView);
	}

	public void showComponent(Component component) {
		if (mainView != null) {
			ZKC.removeMeReturnParent(mainView);
		}
		mainView = new MainView(component);
		mainView.setHeight("100%");
		appendChild(mainView);
	}

	public MainView getMainViewComponent() {
		return mainView;
	}

	public MainView getMainViewOrCreate() {
		return getMainViewComponent() == null ? this.mainView = new MainView() : getMainViewComponent();
	}

	public class MainTbx extends Tbxm {

		public MainTbx(Path path) {
			super(path, Tbx.DIMS.WH100);
			saveble();
		}

		@Override
		protected void onSubmitTextValue(Event e) {
			super.onSubmitTextValue(e);
			ZKR.rebuildPage();
		}
	}

	public static class MainView extends Div0 {
		public MainView(Component... coms) {
			super(coms);
//			setCLASS(MainView.class.getSimpleName());
		}
	}
}
