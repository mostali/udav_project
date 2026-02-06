package zk_notes.node_srv.types.jarMsg;

import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpe.call_msg.JarCallMsg;
import mpf.zcall.ZJar;
import mpf.zcall.ZType;
import mpu.core.ARR;
import mpu.func.FunctionV;
import mpu.str.JOIN;
import mpu.str.TKN;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Dd;
import zk_com.base_ctr.Span0;
import zk_notes.node.NodeDir;
import zk_page.ZKR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ThreeDd extends Span0 {

	public final NodeDir node;

	@Override
	protected void init() {
		super.init();

		JarCallMsg jarCallMsg = JarCallMsg.ofIPath(node);

		Path dir = jarCallMsg.getDir(null);

		if (dir == null) {
			dir = node.getSelfDir();
		}

		AtomicReference<String> fixName = new AtomicReference<>();
		if (UFS.existDir(dir)) {

			Function<ZType, ZCallsMethodsDd> showNext_MethodDd = (zType) -> {
				List<String> values = zType.getAllZMethods().stream().map(ZType.ZMethod::name).collect(Collectors.toList());
				ZCallsMethodsDd zCallsTypes = new ZCallsMethodsDd(values) {
					@Override
					public void onHappensChange(Event e, Object value) {

						FunctionV changeNodeData = () -> {
							List<String> list = node.state().readFcDataAsLines();
							String first = TKN.firstGreedy(list.get(0), "#");
							list.set(0, first + "#" + value);
							L.info("Choiced & update jar-type-method:{}", first);
							node.state().writeFcData(JOIN.allByNL(list));
						};
						FunctionV checkNodeOnCall = () -> {

						};

						changeNodeData.apply();

						ZKR.restartPage();
					}
				};
				return zCallsTypes;
			};

			Function<String, ZCallsTypesDd> showNext_TypeDd = (fileJar) -> {
				JarCallMsg jcm = JarCallMsg.of(node.state().readFcData());
				String[] packages = jcm.getPackages(null);
//				List<ZType> allZTypes = (packages != null ? ZJar.of(ARR.merge(fileJar, packages)) : ZJar.of(fileJar)).getAllZTypes();
				List<ZType> allZTypes = (packages != null ? ZJar.of(ARR.merge(fileJar, packages)) : ZJar.of(fileJar)).getAllZTypes();
				Map<String, ZType> zTypeMap = allZTypes.stream().collect(Collectors.toMap(z -> z.name(), z -> z));
				List<String> values = STREAM.mapToList(allZTypes, ZType::name);
				ZCallsTypesDd zCallsTypes = new ZCallsTypesDd(Paths.get(fileJar), values) {
					@Override
					public void onHappensChange(Event e, Object value) {

						List<String> list = node.state().readFcDataAsLines();
						String last = TKN.last(list.get(0), "#");
						ZType choicedType = zTypeMap.get(value + "");
						list.set(0, JarCallMsg.KEY + ":" + choicedType.zType.getName() + "#" + last);

						L.info("Choiced & update jar-type:{}", fileJar);

						node.state().writeFcData(JOIN.allByNL(list));

						replaceWith(showNext_MethodDd.apply(choicedType));


					}
				};
				return zCallsTypes;
			};

			//

			Path finalDir = dir;
			ZCallsChoiceJarDd showAllJar = new ZCallsChoiceJarDd(dir, ZCallsChoiceJarDd.fJarNames.apply(dir).keySet()) {
				@Override
				public void onHappensChange(Event e, Object value) {

					node.state().set("jar.filename", value);

					List<String> list = node.state().readFcDataAsLines();
					List<String> nlines = new LinkedList<>();
					boolean found = false;
					for (String l : list) {
						if (!found && l.startsWith("--jar.filename:")) {
							nlines.add("--jar.filename:" + value);
							found = true;
						} else {
							nlines.add(l);
						}
					}

					node.state().writeFcData(JOIN.allByNL(nlines));

					String pathJar = ZCallsChoiceJarDd.fJarNames.apply(finalDir).get(getValue());

					L.info("Choiced & update jar:{}", pathJar);

					replaceWith(showNext_TypeDd.apply(pathJar));
				}
			};


			appendChild(showAllJar);
		}


	}

	public static class ZCallsTypesDd extends Dd {

		public final String jarPath;

		public static ZCallsTypesDd ofJar(Path jarPath) {
			List<String> values = STREAM.mapToList(ZJar.of(jarPath).getAllZTypes(), ZType::name);
			return new ZCallsTypesDd(jarPath, values);
		}

		public ZCallsTypesDd(Path jarPath, Collection choices, boolean... skipDefaultOnHappensEventHandler) {
			super(ARR.mergeToList(ARR.as("-"), choices), skipDefaultOnHappensEventHandler);
			title("Show z-types's");
			this.jarPath = jarPath.toString();
		}

	}

	public static class ZCallsMethodsDd extends Dd {

		private ZCallsMethodsDd(Collection choices, boolean... skipDefaultOnHappensEventHandler) {
			super(ARR.mergeToList(ARR.as("-"), choices), skipDefaultOnHappensEventHandler);
			title("Show z-method's");
		}

	}

	public static class ZCallsChoiceJarDd extends Dd {

		public String dirPath;

//		@Setter
//		public @Getter String choicedPathJar;

		static Function<Path, Map<String, String>> fJarNames = (dir) -> UFS.ls(dir).stream().filter(p -> EXT.of(p) == EXT.JAR).collect(Collectors.toMap(p -> p.getFileName().toString(), p -> p.toString()));

		public ZCallsChoiceJarDd(Path dirPath, Collection choices, boolean... skipDefaultOnHappensEventHandler) {
			super(ARR.mergeToList(ARR.as("-"), choices), skipDefaultOnHappensEventHandler);
			title("Show z-jars's");
			this.dirPath = dirPath.toString();
		}

	}
}
