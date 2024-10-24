package zk_pages;

import lombok.SneakyThrows;
import mpu.func.Function2;
import mpe.str.URx;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_com.ck_editor.CkEditorComposer;
import zk_com.sun_editor.IPerPage;
import zk_com.sun_editor.SeTbxm;
import zk_com.uploader.ClipboardLoaderComposer;
import zk_com.uploader.FileUploaderComposer;
import zk_form.events.Tbx2_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_os.sec.ROLE;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;

import java.nio.file.Paths;
import java.util.List;

//@VariableResolver(DelegatingVariableResolver.class)
@PageRoute(pagename = "draft", role = ROLE.USER)
public class DraftPageSP extends PageSP implements IPerPage {

	public DraftPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {


		Lb hello = new Lb("hello");
		appendChild(hello);

		Function2<String, String, Object> func = new Function2<String, String, Object>() {
			@Override
			public Object apply(String vl1, String vl2) {
				List<String> allGroup = URx.findAllGroup(vl2, vl1);
				ZKI.infoEditorBw(allGroup);
				return null;
			}
		};

		hello.getOrCreateMenupopup(window).addMenuitem(Tbx2_CfrmSerializableEventListener.toMenuItemComponent("22", "", new String[2], new String[2], func));

		Div0 divWith = Div0.of();
		divWith.setWidth("100%");
		divWith.setHeight("100px");
		
		FileUploaderComposer.loadComponent("asd", Paths.get("./tmp"), window);

		ClipboardLoaderComposer.loadComponent("aaaaaaa", Paths.get("./tmp"));
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
