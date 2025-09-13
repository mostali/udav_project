package zk_page;

import mpc.map.MapTableContract;
import mpf.contract.IContract;
import mpu.str.SPLIT;
import mpu.str.UST;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Html;
import zk_form.events.IBoolEvent;
import zk_notes.node_state.FormState;
import zk_os.AppZosProps;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.core.TransformerPageCollections;

import java.util.ArrayList;
import java.util.Map;

public class UPageSP {

	public static void doAlignPageGrid() {
		Map<int[], FormState> comsCoors = SpVM.get().getAllFormComStatesAsGrid();
		ArrayList<int[]> comsGrid = new ArrayList<>(comsCoors.keySet());
//			if (false) {
//				List<int[]> closestComponents = ComponentFinder.findClosestComponents(comsGrid, new int[]{coor.getX(), coor.getY()}, 50);
//				for (int[] closestComponent : closestComponents) {
//					int px = (int) (coor.getDuration() / 20);
//					FormState formState = comsCoors.get(closestComponent);
//					formState.updatePropSingle(CN.TOP, closestComponent[0] + px + "px");
//					formState.updatePropSingle(CN.LEFT, closestComponent[1] + px + "px");
//
//				}
//			}
		int[] gridParams = BoolEvent.getGrid(AppZosProps.AUTO_GRID_PX.getValueOrDefault());

		TransformerPageCollections.GridAligner.alignToGrid(comsGrid, gridParams[0]);

		TransformerPageCollections.OffsetAdder.addOffset(comsGrid, new int[]{gridParams[1], gridParams[2]});

		comsGrid.forEach(comCoor -> comsCoors.get(comCoor).stateCom().fields().set_TOP_LEFT(comCoor));
	}


	//		ZKPage.addJsTag(window.getPage(), "function onBool(data){\n" + "    zAu.send(new zk.Event(zk.Widget.$('.boolCom'), 'onBool', {'data':data?data:null}, {toServer:true}));\n" + "}");
	public static class BoolEvent extends Html implements IBoolEvent<BoolEvent> {

		public BoolEvent() {
		}

		public BoolEvent(String content) {
			super(content);
		}

		@Override
		public void onEvent(Event event) throws Exception {
			Map<String, Object> data = (Map) event.getData();
			Coor coor = Coor.of(data);
			doEvent(event, data, coor);
		}

		protected void doEvent(Event event, Map<String, Object> data, Coor coor) {
			try {
				PageSP.L.info("Bool coor : " + coor.mapc());
			} catch (Exception ex) {
				PageSP.L.error("doEvent:" + ex);
			}
		}

		;

		public static int[] getGrid(String pat) {
			String[] grid = SPLIT.argsByComma(pat);
			return new int[]{UST.INT(grid[0]), UST.INT(grid[1]), UST.INT(grid[2])};
		}

		@Override
		public void onPageAttached(Page newpage, Page oldpage) {
			super.onPageAttached(newpage, oldpage);
			addEventListener("onBool", this);
		}

		public interface Coor extends IContract {
			int getX();

			int getY();

			int getX2();

			int getY2();

			long getDuration();

			String getDirection();

			public static Coor of(Map data) {
				return MapTableContract.buildContract_MarkNotRq(data, Coor.class);
			}

			default String toStringSimple() {
//				return X.f("%s_%s_%s <<< %s_%s <<<%s", getNid(), getName(), getCreated());
				return mapc().toString();
			}
		}
	}

	//	public static class PulseEvent extends Html implements IBoolEvent<BoolEvent> {
//
//		public PulseEvent() {
//		}
//
//		public PulseEvent(String content) {
//			super(content);
//		}
//
//		@Override
//		public void onEvent(Event event) throws Exception {
//			Map<String, Object> data = (Map) event.getData();
//			Coor coor = Coor.of(data);
//			doEvent(event, data, coor);
//		}
//
//		protected void doEvent(Event event, Map<String, Object> data, Coor coor) {
//			L.info("Bool coor : " + coor.mapc());
//		}
//
//		@Override
//		public void onPageAttached(Page newpage, Page oldpage) {
//			super.onPageAttached(newpage, oldpage);
//			addEventListener("onBool", this);
//		}
//
//		public interface Coor extends IContract {
//			int getX();
//
//			int getY();
//
//			int getX2();
//
//			int getY2();
//
//			long getDuration();
//
//			String getDirection();
//
//			public static Coor of(Map data) {
//				return MapTableContract.buildContract_MarkNotRq(data, Coor.class);
//			}
//
//			default String toStringSimple() {
////				return X.f("%s_%s_%s <<< %s_%s <<<%s", getNid(), getName(), getCreated());
//				return mapc().toString();
//			}
//		}
//	}

}
