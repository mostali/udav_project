package zk_pages;

import lombok.SneakyThrows;
import mp.utl_odb.tree.UTree;
import mpc.env.Env;
import mpc.fs.UFS;
import mpc.json.GsonMap;
import mpc.map.MAP;
import mpe.str.URx;
import mpf.zcall.ZJar;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.func.Function2;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;
import zk_com.base.*;
import zk_com.base_ctr.Div0;
import zk_com.base_ext.Bandbox0;
import zk_com.charts.LineChart;
import zk_com.listbox.Listbox0;
import zk_com.ck_editor.CkEditorComposer;
import zk_com.ext.Ddl;
import zk_com.ext.video.AdvVideo;
import zk_com.sun_editor.IPerPage;
import zk_com.sun_editor.SeTbxm;
import zk_com.tabs.Tabbox0;
import zk_com.uploader.ClipboardLoaderComposer;
import zk_com.uploader.DDFileUploader;
import zk_form.dirview.DirView0;
import zk_form.events.IBoolEvent;
import zk_form.events.Tbx2_CfrmSerializableEventListener;
import zk_form.ext.DNode;
import zk_form.head.StdHeadLib;
import zk_form.notify.NotifyRef;
import zk_form.notify.ZKI;
import zk_form.tree.DirViewTree;
import zk_form.tree.ZJarViewTree;
import zk_notes.coms.PrettyCodeXml;
import zk_notes.control.maintbx.mvelconsole.MvelConsolePanel;
import zk_notes.control.maintbx.mvelconsole.MvelConsole;
import zk_notes.control.maintbx.shconsole.ShConsolePanel;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;
import zk_notes.search.NoteBandbox;
import zk_os.core.Sdn;
import zk_os.sec.ROLE;
import zk_page.UPageSP;
import zk_page.ZKC;
import zk_page.ZKS;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

//@VariableResolver(DelegatingVariableResolver.class)
@PageRoute(pagename = "demo-com", role = ROLE.ADMIN)
public class DemoComPageSP extends PageSP implements IPerPage {

	public static final Path FS_DEMO_COM = Env.TMP.resolve("fsDemoCom");

	public DemoComPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {

		StdHeadLib.CHARTS_JS.addToPage();

		appendChild(new Bt("run jira").onCLICK(e -> {
			String[] packages = {};
			ZJar zJar = ZJar.of(Paths.get("/opt/appVol/.bin/jira-mod.jar"), packages);
			Object taskCreate = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "create", "-task.type", "3", "-task.project", "SUP", "-task.summary", "summarryss", "-task.desc", "descc", "-task.assignee", "ditts.aleksandr"});
			GsonMap gm = GsonMap.ofObj(taskCreate);
			Sys.open_Chrome("https://ias-tst-job-jira.otr.ru/browse/" + gm.get("key"));
//				X.exit();
		}));

		sectionShConsole();

//		sectionMvelConsole();

		sectionDragDropFile();

		sectionFs();

		sectionTree();

		sectionTabbox();

		sectionCombobox();

		sectionBoolCom();

		sectionChart();

		sectionDDnode();

		sectionBandbox();

		sectionDb();

		addHeaderCom("prettyfy code component");

//		appendChild(Xml.ofXml("<pre class=\"prettyprint\">\n" +
//				"int x = foo();  /* This is a comment  <span class=\"nocode\">This is not code</span>\n" +
//				"  Continuation of comment */\n" +
//				"int y = bar();\n" +
//				"</pre>"));

		appendChild(PrettyCodeXml.of("int x = foo();  /* This is a comment  <span class=\"nocode\">This is not code</span>\n" + "  Continuation of comment */\n" + "int y = bar();"));

		addHeaderCom("WYSIWIG");

		SeTbxm modalCom = new SeTbxm("Hello WYSIWIG");
		//modalCom.renderHead();
		appendChild(modalCom);

//		appendChild(new Ln("show").onCLICK(e -> {
//			ZKM_Editor.openEditorHTML("asd", "asdasd");
//		}));


		section_POPUP();

		section_MSG();


		section_DDL();

		section_VIDEO();


		if (true) {
			return;
		}


		if (true) {
			return;
		}

		if (true) {
			window.appendChild(CkEditorComposer.loadComponent(Paths.get("tmp/test.wsy")));
			return;
		}
//		if (true) {
//			window.appendChild(new Embed());
//		}
//		if (true) {
//			AdvVideo video = ZKC.newVideo(Paths.get("/home/dav/Рабочий стол/boysya.MP4"));
//			window.appendChild(video);
//			return;
//		}


//		if (false) {
////			PdfTag embed = new PdfTag();
////			embed.setContent(new APdf("/home/dav/.data/bea/.planes/.index/ii/.forms/PresentationPdf/ИИ-стартовый набор .pdf"));
////			embed.setSrc(ZKC.getFirstPage().getDesktop().getExecution().encodeURL("/home/dav/.data/bea/.planes/.index/ii/.forms/PresentationPdf/ИИ-стартовый набор .pdf"));
//
//			Iframe embed = new Iframe() {
//				@Override
//				public Object getExtraCtrl() {
//					return new DynamicMedia() {
//						@Override
//						public Media getMedia(String s) {
//							return new APdf("/home/dav/.data/bea/.planes/.index/ii/.forms/PresentationPdf/ii2.docx");
//						}
//					};
//				}
//			};
//			window.appendChild(embed);
//			String src = Utils.getDynamicMediaURI(embed, 1, "ii2.docx", "DOCX");
//			String iSrc = "https://docs.google.com/gview?url=%s&embedded=true";
//			embed.setSrc(X.f(iSrc, src));
//			return;
//		}
//
//		if (true) {
////			PdfTag embed = new PdfTag();
////			embed.setContent(new APdf("/home/dav/.data/bea/.planes/.index/ii/.forms/PresentationPdf/ИИ-стартовый набор .pdf"));
////			embed.setSrc(ZKC.getFirstPage().getDesktop().getExecution().encodeURL("/home/dav/.data/bea/.planes/.index/ii/.forms/PresentationPdf/ИИ-стартовый набор .pdf"));
//
//			Embed embed = new Embed() {
//				@Override
//				public Object getExtraCtrl() {
//					return new DynamicMedia() {
//						@Override
//						public Media getMedia(String s) {
//							return new APdf("/home/dav/.data/bea/.planes/.index/ii/.forms/PresentationPdf/ii.pdf");
//						}
//					};
//				}
//			};
//			window.appendChild(embed);
//			String src = Utils.getDynamicMediaURI(embed, 1, "ii.pdf", "PDF");
//			embed.setDynamicProperty("src", src);
////			embed.setSrc("https://css4.pub/2015/icelandic/dictionary.pdf");
//
//			return;
//		}
		if (false) {
			window.appendChild(Ln.uploadTo("+", Paths.get("./tmp"), 2));

			window.appendChild(Ln.uploadExtTo("+", Paths.get("./tmp"), 2));

//			FileUploaderComposer.loadComponent("asd", Paths.get("./tmp"), window);
			return;
		}

		Lb hello = new Lb("hello");
		appendChild(hello);

		Function2<String, String, Object> func = new Function2<String, String, Object>() {
			@Override
			public Object apply(String vl1, String vl2) {
				List<String> allGroup = URx.findAllGroup(vl2, vl1);
				ZKI.infoEditorDark(allGroup);
				return null;
			}
		};

		hello.getOrCreateMenupopup(window).addMI_Href_in_Self(Tbx2_CfrmSerializableEventListener.toMI("22", "", new String[2], new String[2], func));

		Div0 divWith = Div0.of();
		divWith.setWidth("100%");
		divWith.setHeight("100px");


		ClipboardLoaderComposer.loadComponent("aaaaaaa", Paths.get("./tmp"));


//		window.appendChild(new SingleTopPost());

//		ZkPage.renderHeadRsrcs(window, StdHeadLib.JQUERY_3_1_1);
//		ZkPage.renderHeadRsrcs(window, StdHeadLib.JQUERY_1_9_1);

//		ZkPage.renderHeadRsrcs(window, StdHeadLib.PAGE_JQ_TOAST);
//		ZkPage.renderHeadRsrcs(window, StdHeadLib.PAGE_JQ_WIDGET);
//		ZkPage.renderHeadRsrcs(window, StdHeadLib.PAGE_JQ_TRANSP);
//		ZkPage.renderHeadRsrcs(window, StdHeadLib.PAGE_JQ_FILEUPLOAD);

//		DdFileUploaderComposer.loadComponent("aaaaaaa", Paths.get("./tmp"));


//		SerializableEventListener<UploadEvent> eventUpload = FileUploaderComposer.getEventUpload("./tmp");
//		ZKE.addEventListenerAll_LOGZK(hello);

//		Events.KEY
//		appendChild(new Html("<audio controls=true src=\"/@@@uploads/0.mp3\"> </audio>"));
//		appendChild(new Mp3(Paths.get("/home/dav/.data/bea/@@@uploads/0.mp3")));
//		appendChild(new Lb("wtf"));

//		testSunEditor();
//		testCkEditor();
//		testDnd();

	}

	private void sectionShConsole() {
		addHeaderCom("Sh Console");
//		MvelConsolePanel consolePanel = MvelConsole.getConsolePanel(ARR.EMPTY_MAP);
		window.appendChild(new ShConsolePanel());

	}

	private void sectionMvelConsole() {
		addHeaderCom("Mvel Console");
		MvelConsolePanel consolePanel = MvelConsole.getConsolePanel(ARR.EMPTY_MAP);
		consolePanel.setCollapsible(true);
		window.appendChild(consolePanel);
	}

	private void sectionDragDropFile() {

		addHeaderCom("Drag&Drop File");
//		DdFileUploaderComposer.loadComponent("load", Paths.get("/tmp"), window);


		DDFileUploader.open();
//		FileUploadViewModel_V3.open();
//		window.appendChild();
	}

	private void sectionFs() {

		addHeaderCom("FS Dir Tree");

		Path path = NodeDir.ofNodeName("dev", "page", "item").createIfNotExist().toPath();
//		Path path = NodeDir.ofNodeName("dev", "page", "item2").toPath();

//		window.appendChild(new DirView0(path, true));
		window.appendChild(DirView0.openWithSimpleMenuAsForm_asModal(path));
//		window.appendChild(DirView0.openWithSimpleMenu(path));
	}

	private void sectionTree() {
		addHeaderCom("Tree");
		window.appendChild(new DirViewTree("/home/dav/pjbf_tasks/33"));
		addHeaderCom("Tree ZJar");
		ZJarViewTree zJarViewTree = new ZJarViewTree(new String[]{"/opt/appVol/.bin/jira-mod.jar", "mp.jira"});
		window.appendChild(zJarViewTree);
	}

	private void sectionTabbox() {
		addHeaderCom("Tabbox");

		{


//			Tabbox0 tabboxMap = Tabbox0.newTabboxAs(zJar.getMapZTypes());
//			window.appendChild(tabboxMap);

			{
//				window.appendChild(Xml.H(3,"Tabs"));
				Tabbox0 tabbox0 = Tabbox0.newTabboxAs("aaa", Lb.of("a"), "bbb", Lb.of("b"), "ccc", Lb.of("C"));
				window.appendChild(tabbox0);
			}

			window.appendChild(Xml.BR());
			window.appendChild(Xml.BR());

			{
				window.appendChild(Xml.H(3, "Accordion"));
				Tabbox0 tabbox0 = Tabbox0.newTabboxAs("aaa", Lb.of("a"), "bbb", Lb.of("b"), "ccc", Lb.of("C"));
				tabbox0.accordeon();
				window.appendChild(tabbox0);
			}

		}

	}

	private void sectionCombobox() {
		addHeaderCom("Combobox");

		{
			Combobox combobox = new Combobox();
			ListModel<?> cm = new ListModelList<>(ARR.as("1\n3", "2"));
//			ListModel<?> cm = new ListModelMap<>(MAP.as("1", "11", "2", "22"));
//			ListModel<?> cm = new ;
			combobox.setModel(cm);
			combobox.addEventListener(Events.ON_CHANGE, (InputEvent e) -> {
				X.p("SEL:" + e.getValue());
			});
//			combobox.setMultiline(true);

			window.appendChild(combobox);

		}
		{
			Combobox combobox = new Combobox("3");
			ListModel<?> cm = new ListModelList<>(ARR.as("1", "2", "3"));
			combobox.setModel(cm);
			combobox.addEventListener(Events.ON_CHANGE, (InputEvent e) -> {
				X.p("SEL:" + e.getValue());
			});
			window.appendChild(combobox);
			combobox.setMold("rounded");
		}
	}

	private void section_POPUP() {
		addHeaderCom("Popup");

		//		Bt bt1 = new Bt();
		Ln bt1 = new Ln("go");

		Popup popup = new Popup();
		popup.appendChild(new Ln("go"));
		popup.appendChild(new Lb("go2"));
		window.appendChild(popup);

		bt1.addEventListener(Events.ON_MOUSE_OVER, e -> popup.open(window, NotifyRef.Pos.after_pointer.name()));
		appendChild(bt1);
	}

	private void section_MSG() {
		addHeaderCom("Info msg");

		for (ZKI.ViewType value : ZKI.ViewType.values()) {
			CharSequence msg;
			switch (value) {
				case MODAL_JSON:
				case MODAL_JSON_BW:
					msg = GsonMap.ofKV("1", "2", "3", "4", "5", GsonMap.ofKV("1", "2", "3", "4", "5", "6")).toStringJson();
					break;
				default:
					msg = X.f("Hello info message of type '%s'", value);
					break;
			}
			Bt bt = new Bt(value.name()).onCLICK(e -> value.showView(msg));
			appendChild(bt);
		}
		addHeaderCom("Info msg - ERROR");

		for (ZKI.ViewType value : ZKI.ViewType.values()) {
			try {
				RW.readLines("file not found");
			} catch (Exception ex) {
				Bt bt = new Bt(value.name()).onCLICK(e -> value.showView(ex, X.f("Error message of type '%s'", value)));
				appendChild(bt);

			}
		}
	}

	private void section_VIDEO() {
		addHeaderCom("Video");
		AdvVideo video = ZKC.newVideo(Paths.get("/home/dav/Рабочий стол/boysya.MP4"));
		window.appendChild(video);
	}

	private void section_DDL() {
		addHeaderCom("Ddl");

		Ddl<NVT> ddl = new Ddl<NVT>(NVT.MD) {
			@Override
			public boolean onHappensClickItem(MouseEvent e, NVT item) {
				Sys.say("ok:" + item);
				return true;
			}
		};
		appendChild(ddl);

		appendChild(new Bt("cl").onCLICK(e -> {
			ddl.getChildren().forEach(c -> c.detach());
		}));


		//		Ddl<String> ddl = new Ddl<String>("333", ARR.as("1", "22", "333")) {
		//			@Override
		//			public void onClickItem(String value) {
		//				Sys.say("ok:" + value);
		//			}
		//		};
		//		appendChild(ddl);
	}


	private void sectionBoolCom() {
		addHeaderCom("BoolCom - Click and move");
		//TODO
		UPageSP.BoolEvent html = new UPageSP.BoolEvent() {
			@Override
			protected void doEvent(Event event, Map<String, Object> data, Coor coor) {
				super.doEvent(event, data, coor);
				X.nothing();
				ZKI.log("Data:" + data);
			}
		};

		html.setClass("boolCom");
		window.appendChild(html);

		IBoolEvent.initNewAndAppend(window);

	}

	private void sectionChart() {
		addHeaderCom("Charts");
		appendChild(LineChart.ofN(LineChart.ChartType.line, null, //
				ARR.as("1", "2", "3", "4", "5"), //
				MAP.of("label1", ARR.as(5, 2, 22, 3, 7), "label2", ARR.as(22, 5, 2, 3, 7), "label3", ARR.as(5, 2, 3, 7, 22)) //
		));
//		appendChild(Chart0.newChart());
	}

	private void sectionDDnode() {
		addHeaderCom("Double DNode");
		Path n1 = Paths.get("/home/dav/.data/bea/.planes/.index/_demo-notes/.forms/SQL");
		Path n2 = Paths.get("/home/dav/.data/bea/.planes/.index/_demo-notes/.forms/-SQL-");
		window.appendChild(DNode.of(NodeDir.ofDir(Sdn.get(), n1), NodeDir.ofDir(Sdn.get(), n2)));
	}

	private void addHeaderCom(String header) {
		appendChild(Xml.BR());
		appendChild(Xml.BR());
		appendChild(Xml.BR());
		appendChild(Xml.BR());
		appendChild(ZKS.CENTER(Xml.H(2, header)));
		appendChild(Xml.HR());
	}

	private void sectionBandbox() {

		addHeaderCom("Work with Bandbox");

		Path target = FS_DEMO_COM;
		Path dirExample1 = target.resolve("dir1");
		Path dirExample2 = target.resolve("dir2");
		Path fileExample1 = target.resolve("dir1/file1");

		UFS.MKDIR.createDirs(dirExample1);
		UFS.MKDIR.createDirs(dirExample2);
		UFS.MKFILE.createEmptyFileIfNotExist(fileExample1);

//		X.p(UF.ln(target));

//		Bandbox0 bandbox1 = new FsBandbox(target);
		Bandbox0 bandbox1 = new NoteBandbox();
		window.appendChild(bandbox1);
	}

	private void sectionDb() {

		addHeaderCom("Work with Db");

//		Bt bt = new Bt("show") {
//			@Override
//			public void init() {
//				super.init();
//		UTree.tree("def").clear();

		UTree tree = UTree.tree("def");
		if (tree.isEmptyDb()) {
			tree.put("key", "val");
		}

		Listbox0 def = Listbox0.fromCtxDb(UTree.tree("def").getDbFilePath());
//				onCLICK(e -> ZKM.showModal("asd", def));
//			}
//		};
//		appendChild(bt);
		appendChild(def);

	}

	void testSunEditor() {
//		appendChild(new SeWin());
//		SeTbxm child = (SeTbxm) new SeTbxm().writable();
		SeTbxm child = (SeTbxm) new SeTbxm(Paths.get("/tmp/tt")).saveble();
		appendChild(child);
//
		child.setHeight("500px");
//		window.appendChild(child);
//		window.appendChild(new SeTextboxFile(Paths.get("/tmp/t")));
//		appendChild(new Label("uuid:" + child.getUuid()));
//		Div toolbar = new Div();
//		DraftPageSP.this.appendChild(toolbar);
//		toolbar.setSclass("toolb");
//		ZKS.STYLE(toolbar, "height:100px;border:1px solid red");
	}

	private void testCkEditor() {
		window.appendChild(new Lb("aaaaaa"));

		CkEditorComposer.loadComponent(Paths.get("/tmp/test.html"));
//		child.setFilebrowserImageUploadUrl();
//		CKeditor child =
//		CKeditor child = new CKeditor();
//		child.s
//		child.addEventListener("onSave", new SerializableEventListener<Event>() {
//			@Override
//			public void onEvent(Event event) throws Exception {
//				U.p("save:" + child.getValue());
//			}
//		});
//		child.addEventListener(Events.ON_CHANGE, new SerializableEventListener<Event>() {
//			@Override
//			public void onEvent(Event event) throws Exception {
//				U.p("change:"+child.getValue());
//			}
//		});
//		child.addEventListener(Events.ON_CHANGING, new SerializableEventListener<Event>() {
//			@Override
//			public void onEvent(Event event) throws Exception {
//				U.p("changING:"+child.getValue());
//			}
//		});
//		appendChild(child);
	}

	public void appendChild(Component child) {
		window.appendChild(child);
	}


	static void testDnd() {

//			ZkPage.renderHeadPage_Rsrc("/_com/mouse-event/dnd-simple.js");

//			Div movable = DivWith.of(new Label("wtf"));
//			ZKS.of(movable).abs().right(20).bottom(20).zindex(1000);
//			movable.addEventListener("onMouseMove", new SerializableEventListener<Event>() {
//				@Override
//				public void onEvent(Event event) throws Exception {
//					ZK.log("Up:" + event);
//				}
//			});
//			movable.addEventListener("onMouseDown", new SerializableEventListener<Event>() {
//				@Override
//				public void onEvent(Event event) throws Exception {
//					ZK.log("Up:" + event);
//				}
//			});

//			ZKE.addEventListener(movable, new SerializableEventListener() {
//				@Override
//				public void onEvent(Event event) throws Exception {
//					U.say("drop1");
//					ZK.ZLOG("drop1:" + event + ":");
//				}
//			}, Events.ON_DROP);
//			movable.setWidgetListener("onDrop", "alert(event);"); //initialize client side paste listener
//			movable.setWidgetListener("onBind", "dragElementById(this.uuid);"); //initialize client side paste listener

//			movable.
//			window.appendChild(movable);

//			Div parent = DivWith.of(new Label("drop to me"));
//
//			ZKE.addEventListener(parent, new SerializableEventListener() {
//				@Override
//				public void onEvent(Event event) throws Exception {
//					U.say("drop2");
//					ZK.ZLOG("drop:2" + event + ":");
//				}
//			}, Events.ON_DROP);
//			parent.setDroppable("true");
//			window.appendChild(parent);

	}

}
