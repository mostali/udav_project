package zk_pages;

import lombok.SneakyThrows;
import mpu.Sys;
import mpu.func.Function2;
import mpe.str.URx;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Lb;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.ck_editor.CkEditorComposer;
import zk_com.ext.Ddl;
import zk_com.ext.video.AdvVideo;
import zk_com.sun_editor.IPerPage;
import zk_com.sun_editor.SeTbxm;
import zk_com.uploader.ClipboardLoaderComposer;
import zk_form.events.Tbx2_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_notes.node.core.NVT;
import zk_os.sec.ROLE;
import zk_page.ZKC;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;

import java.nio.file.Paths;
import java.util.List;

//@VariableResolver(DelegatingVariableResolver.class)
@PageRoute(pagename = "_draft", role = ROLE.ADMIN)
public class DraftPageSP extends PageSP implements IPerPage {

	public DraftPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

//	static class PdfTag extends Embed {
//		public PdfTag() {
//		}
//
//		APdf audio;
//
//		public void setContent(APdf audio) {
//			this.audio = audio;
//			this.smartUpdate("src", new PdfTag.EncodedSrc0());
//
//		}
//
//		public APdf getContent() {
//			return this.audio;
//		}
//
//		class EncodedSrc0 implements DeferredValue {
//			private EncodedSrc0() {
//			}
//
//			public Object getValue() {
//				return this.getEncodedSrc();
//			}
//
//			private List<String> getEncodedSrc() {
//				Desktop dt = getDesktop();
//				List<String> list = new ArrayList();
////						 if (this._audio != null) {
////							 list.add(this.getAudioSrc());
////						 } else if (dt != null) {
////						Iterator var3 = this._src.iterator();
////						while (var3.hasNext()) {
////							String src = (String) var3.next();
//				list.add(dt.getExecution().encodeURL("/home/dav/.data/bea/.planes/.index/ii/.forms/PresentationPdf/ИИ-стартовый набор .pdf"));

	/// /						}
	/// /						 }
//				return list;
//			}
//		}
//
//		@Override
//		public void smartUpdate(String attr, int value) {
//			super.smartUpdate(attr, value);
//		}
//	}

	public static class Embed extends AbstractComponent {
//		public Embed() {
//			super("embed");
//		}
	}
//	public static class Embed extends AbstractTag {
//		public Embed() {
//			super("embed");
//		}
//
//		public String getHeight() {
//			return (String)this.getDynamicProperty("height");
//		}
//
//		public void setHeight(String height) throws WrongValueException {
//			this.setDynamicProperty("height", height);
//		}
//
//		public String getSrc() {
//			return (String)this.getDynamicProperty("src");
//		}
//
//		public void setSrc(String src) throws WrongValueException {
//			this.setDynamicProperty("src", src);
//		}
//
//		public String getType() {
//			return (String)this.getDynamicProperty("type");
//		}
//
//		public void setType(String type) throws WrongValueException {
//			this.setDynamicProperty("type", type);
//		}
//
//		public String getWidth() {
//			return (String)this.getDynamicProperty("width");
//		}
//
//		public void setWidth(String width) throws WrongValueException {
//			this.setDynamicProperty("width", width);
//		}
//	}


	@SneakyThrows
	public void buildPageImpl() {

		Ddl<NVT> ddl = new Ddl<NVT>(NVT.MD) {
			@Override
			public boolean onHappensClickItem(MouseEvent e, NVT item) {
				Sys.say("ok:" + item);
				return true;
			}
		};
		appendChild(ddl);

//		Ddl<String> ddl = new Ddl<String>("333", ARR.as("1", "22", "333")) {
//			@Override
//			public void onClickItem(String value) {
//				Sys.say("ok:" + value);
//			}
//		};
//		appendChild(ddl);

		appendChild(new Bt("cl").onCLICK(e -> {
			ddl.getChildren().forEach(c -> c.detach());
		}));

		if (true) {
			return;
		}
		if (true) {
//			window.appendChild(new DirViewTree());
			return;
		}
		if (true) {
			window.appendChild(CkEditorComposer.loadComponent(Paths.get("tmp/test.wsy")));
			return;
		}
		if (true) {
			window.appendChild(new Embed());
		}
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


		AdvVideo video = ZKC.newVideo(Paths.get("/home/dav/Рабочий стол/boysya.MP4"));
		window.appendChild(video);

		window.appendChild(Xml.HR());

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
//
//		testDnd();

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
