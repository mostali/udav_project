package zk_notes.node_srv;

import groovy.lang.GroovyShell;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpc.types.abstype.AbsType;
import mpc.types.tks.LID;
import mpe.NT;
import mpe.core.ERR;
import mpe.str.StringWalkBuilder;
import mpe.wthttp.*;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.func.FunctionV1;
import mpu.str.UST;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.JobKey;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;
import zk_com.base.Ln;
import zk_com.base.Tbx;
import zk_com.base_ctr.Span0;
import zk_com.base_ext.Listbox0;
import zk_com.editable.EditableValue;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Messagebox;
import zk_notes.events.ANM;
import zk_notes.node_srv.jarcall.JarCallService;
import zk_notes.node_srv.quartz.QzEvalService;
import zk_os.quartz.QzApiEE;
import zk_notes.node_srv.quartz.QzTaskService;
import zk_os.sec.Sec;
import zk_page.*;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class NodeCapsCom {

	public static Class<? extends Component> getNodeCallCapLinkClass(NodeEvalType nodeCallType) {
		switch (nodeCallType) {
			case HTTP:
				return HttpPlayLn.class;
			case KAFKA:
				return KafkaCtrlLn.class;
			case SQL:
				return SqlPlayLn.class;
			case QZTASK:
				return QzCtrlTaskLn.class;
			case QZEVAL:
				return QzCtrlEvalLn.class;
			case JARTASK:
				return JartaskCtrlLn.class;
			case GROOVY:
				return GroovyPlayLn.class;
			default:
				throw new WhatIsTypeException(nodeCallType);
		}
	}

	//
	//
	//

	public static class FormSizeTbx extends Tbx {
		public FormSizeTbx(NodeDir node) {
			super();
			setValue(node.upd().get_SIZE(1) + "");
			placeholder("1");
			ZKS.OPACITY(this, 0.4);
			ZKS.FLOAT(this, false);
			setWidgetAttribute("type", "number");
			width(30).float0(false);
			onOK(e -> {
				int vl = UST.INT(getValue());
				node.upd().set_SIZE(vl < 0 ? null : vl);
				ZKR.restartPage();
			});
		}

	}

	public static class FormEditableName extends EditableValue {
		final NodeDir node;

		public FormEditableName(NodeDir node) {
			super(node.nodeName());
			this.node = node;
			if (Sec.isEditorAdminOwner()) {
				ANM.applyFormLink(getOrCreateMenupopup(ZKC.getFirstWindow()), node);
			}
		}

		@Override
		protected void onUpdatePrimaryText(String value) {
			super.onUpdatePrimaryText(value);
			NodeFileTransferMan.rename(node, value, false);
			ZKR.restartPage();
		}


		public void enableDisappearComs(Supplier<List<Component>> slaveOpacityComs, Integer... ms) {
			addEventListener(Events.ON_MOUSE_OVER, e -> slaveOpacityComs.get().forEach(sc -> ZKJS.eval(X.f_("toggleOpacity('#%s',%s)", sc.getUuid(), ARG.toDefOr(7_000, ms)))));

			slaveOpacityComs.get().forEach(c -> {
				if (c instanceof FormEditableName) {
					//ok
				} else {
					ZKS.OPACITY((HtmlBasedComponent) c, 0.05);
				}
			});
		}
	}


	//
	//-------------------- HTTP CALL -------------------------------
	//

	public static class HttpPlayLn extends Ln {

		public HttpPlayLn(NodeDir node) {
			super();
			NodeEvalType.HTTP.applyLn(this, node, this);
		}

		public static Object doEventAction(NodeDir nodeDir) {
			return HttpCallService.doHttpCall(TrackMap.getTrackId(), nodeDir, false);
		}
	}

	//
	//-------------------- JAR CALL -------------------------------
	//

	public static class JartaskCtrlLn extends Span0 {
		public JartaskCtrlLn(NodeDir node) {
			super();
			appendChilds(new JarPlayLn(node));
		}

		public static Object doEventAction(NodeDir node, Component activePushComHolder) {
			JarCallMsg jarCallMsg = JarCallMsg.ofQk(node.state().nodeData(), true);
			if (jarCallMsg.isSync()) {
				JarCallService.doJarCallSyncWeb(node);
			} else {
				JarCallService.doJarCallAsyncWeb(node, activePushComHolder);
			}
			return 0;
		}

		public static class JarPlayLn extends Ln {
			public JarPlayLn(NodeDir node) {
				super();
				NodeEvalType.JARTASK.applyLn(this, node, JarPlayLn.this);
			}
		}

//		public static class QzRmLn extends Ln {
//			public QzRmLn(NodeDir node) {
//				super(" " + SYMJ.FAIL_RED_THINK);
//				title("Remove all Quartz tasks");
//				addEventListener(e -> {
//					int[] deleteRslt = QzNoteService.deleteAll(node, true);
//					ZKI.infoBottomRightFast("All task %s of node '%s' was CLEAN from Scheduler", ARR.ofInt(deleteRslt), node.id());
//				});
//			}
//		}

	}


	//
	//-------------------- SQL CALL -------------------------------
	//


	public static class SqlPlayLn extends Ln {

		public SqlPlayLn(NodeDir node) {
			super();
			NodeEvalType.SQL.applyLn(this, node, null);
//			addEventListener(e -> doEventAction(node));
		}

		public static Object doEventAction(NodeDir nodeDir) {
			FormState state = nodeDir.state();
			state.deletePathFc_OkErr();
			try {
				List<List<AbsType>> maps = SqlCallService.doSqlCall(nodeDir);
				state.writeFcDataOk(String.valueOf(maps));
				HtmlBasedComponent modalCom = Listbox0.fromListList(maps, null);
				ZKM.showModal("Found " + X.sizeOf(maps) + " rows", modalCom, ZKC.getFirstWindow(), new String[]{"90%", null});
			} catch (Exception ex) {
				state.writeFcDataErr(ERR.getStackTrace(ex));
				ZKI.alert(ex);
			}
			return 0;
		}

	}

	//
	//-------------------- QUARTZ CALL -------------------------------
	//

	public static class QzCtrlTaskLn extends Span0 {
		public QzCtrlTaskLn(NodeDir node) {
			super();
			appendChilds(new QzPlayLn(node), new QzRmLn(node), new QzShowAll(node));
		}

		public static Object doEventAction(NodeDir node) {
			QzTaskService.runAll(node);
			ZKI.infoAfterPointer(X.f("Run All Quartz task of node '%s' successfully", node.id()), ZKI.Level.INFO);
			return 0;
		}

		public static class QzPlayLn extends Ln {
			public QzPlayLn(NodeDir node) {
				super();
				NodeEvalType.QZTASK.applyLn(this, node, null);
//				addEventListener(e -> doEventAction(node));
			}
		}

		public static class QzRmLn extends Ln {
			public QzRmLn(NodeDir node) {
				super(" " + SYMJ.FAIL_RED_THINK);
				title("Remove all Quartz tasks");
				addEventListener(e -> {
					FunctionV1<Boolean> f = r -> {
						String msg;
						if (r == null) {
							return;
						} else if (r) {
							int[] deleteRslt = QzTaskService.deleteAll(node, true);
							msg = X.f("All(%s) taskof node '%s' was CLEAN from Scheduler", ARR.ofInt(deleteRslt), node.id());
						} else {
							int[] deleteRslt = QzApiEE.deleteAllTotal(true);
							msg = X.f("All(%s) task was CLEAN from Scheduler", ARR.ofInt(deleteRslt));
						}
						ZKI.infoAfterPointer(msg, ZKI.Level.INFO);
					};
					String msg = X.f("Remove mode -> ALL (YES) | Single (NO) <- (%s/%s) ?", X.sizeOf(QzApiEE.getAllJobKeys()), X.sizeOf(QzTaskService.findAllJobKeys(node)));
					ZKI_Messagebox.showMessageBoxYNC_ofLevel("Remove quartz task's", msg, f, Messagebox.QUESTION);

				});
			}
		}

		public static class QzShowAll extends Ln {
			public QzShowAll(NodeDir node) {
				super(" " + SYMJ.QUEST_RED);
				title("Show all Quartz tasks");
				FunctionV1<Set<JobKey>> printer = jobs -> {
					String rp = StringWalkBuilder.<JobKey>of(jk -> jk.toString()).ol().buildSB(jobs).toString();
					ZKM_Editor.openEditorText("All founded Job's", rp);
				};
				addEventListener(e -> {
					FunctionV1<Boolean> f = r -> {
						Set<JobKey> allJobKeys;
						if (r == null) {
							return;
						} else if (r) {
							allJobKeys = QzApiEE.getAllJobKeys();
						} else {
							allJobKeys = QzTaskService.findAllJobKeys(node);
						}
						printer.apply(allJobKeys);
					};
					String msg = X.f("Show mode -> ALL (YES) | Single (NO) <- (%s/%s) ?", X.sizeOf(QzApiEE.getAllJobKeys()), X.sizeOf(QzTaskService.findAllJobKeys(node)));
					ZKI_Messagebox.showMessageBoxYNC_ofLevel("Show quartz task's", msg, f, Messagebox.QUESTION);

					//
//					Set<JobKey> allJobKeys = QzApiEE.getAllJobKeys();

				});
			}
		}
	}

	//
	//-------------------- QUARTZ EVAL CALL -------------------------------
	//

	public static class QzCtrlEvalLn extends Span0 {
		public QzCtrlEvalLn(NodeDir node) {
			super();
			appendChilds(new QzPlayLn(node), new QzRmLn(node), new QzShowAll(node));
		}

		public static Object doEventAction(NodeDir node) {
			QzEvalService.runAll(node);
			ZKI.infoAfterPointer(X.f("Run Quartz task of node '%s' successfully", node.id()), ZKI.Level.INFO);
			return 0;
		}

		public static class QzPlayLn extends Ln {
			public QzPlayLn(NodeDir node) {
				super();
				NodeEvalType.QZEVAL.applyLn(this, node, null);
			}
		}

		public static class QzRmLn extends Ln {
			public QzRmLn(NodeDir node) {
				super(" " + SYMJ.FAIL_RED_THINK);
				title("Remove all Quartz tasks");
				addEventListener(e -> {
					FunctionV1<Boolean> f = r -> {
						String msg;
						if (r == null) {
							return;
						} else if (r) {
							int[] deleteRslt = QzEvalService.deleteAll(node, true);
							msg = X.f("All(%s) taskof node '%s' was CLEAN from Scheduler", ARR.ofInt(deleteRslt), node.id());
						} else {
							int[] deleteRslt = QzApiEE.deleteAllTotal(true);
							msg = X.f("All(%s) task was CLEAN from Scheduler", ARR.ofInt(deleteRslt));
						}
						ZKI.infoAfterPointer(msg, ZKI.Level.INFO);
					};
					String msg = X.f("Remove mode -> ALL (YES) | Single (NO) <- (%s/%s) ?", X.sizeOf(QzApiEE.getAllJobKeys()), X.sizeOf(QzEvalService.findAllJobKeys(node)));
					ZKI_Messagebox.showMessageBoxYNC_ofLevel("Remove quartz task's", msg, f, Messagebox.QUESTION);

				});
			}
		}

		public static class QzShowAll extends Ln {
			public QzShowAll(NodeDir node) {
				super(" " + SYMJ.QUEST_RED);
				title("Show all Quartz tasks");
				FunctionV1<Set<JobKey>> printer = jobs -> {
					String rp = StringWalkBuilder.<JobKey>of(jk -> jk.toString()).ol().buildSB(jobs).toString();
					ZKM_Editor.openEditorText("All founded Job's", rp);
				};
				addEventListener(e -> {
					FunctionV1<Boolean> f = r -> {
						Set<JobKey> allJobKeys;
						if (r == null) {
							return;
						} else if (r) {
							allJobKeys = QzApiEE.getAllJobKeys();
						} else {
							allJobKeys = QzEvalService.findAllJobKeys(node);
						}
						printer.apply(allJobKeys);
					};
					String msg = X.f("Show mode -> ALL (YES) | Single (NO) <- (%s/%s) ?", X.sizeOf(QzApiEE.getAllJobKeys()), X.sizeOf(QzEvalService.findAllJobKeys(node)));
					ZKI_Messagebox.showMessageBoxYNC_ofLevel("Show quartz task's", msg, f, Messagebox.QUESTION);

					//
//					Set<JobKey> allJobKeys = QzApiEE.getAllJobKeys();

				});
			}
		}
	}

	//
	//-------------------- KAFKA CALL -------------------------------
	//

	public static class KafkaCtrlLn extends Span0 {
		public KafkaCtrlLn(NodeDir node) {
			super();
			KafkaCallMsg kafkaCallMsg = KafkaCallMsg.ofQk(node);
			appendChild(new KafkaPlayLn(node));
			if (kafkaCallMsg.type() == KafkaCallMsg.KafkaMethodType.KGET) {
				appendChild(new KafkaRmLn(node));
			}
		}

		public static Object doEventAction(NodeDir node, Component pushHolderCom) {

			KafkaCallMsg kafkaCallMsg = KafkaCallMsg.of(node);
			switch (kafkaCallMsg.kafka_method) {
				case KPUT:
					KafkaCallService.doKafkaCall(node);
					break;
				case KGET:
					KafkaCallService.doKafkaCall(node, pushHolderCom);
					break;
				default:
					throw new WhatIsTypeException(kafkaCallMsg.kafka_method);
			}
			return 0;
		}

		public static class KafkaPlayLn extends Ln {
			public KafkaPlayLn(NodeDir node) {
				super();

				NodeEvalType.KAFKA.applyLn(this, node, null);



//				addEventListener(e -> doEventAction(node, this));
			}
		}

		public static class KafkaRmLn extends Ln {
			public KafkaRmLn(NodeDir node) {
				super(" " + SYMJ.FAIL_RED_THINK);
				title("Remove all Kafka consumer's");
				addEventListener(e -> {
					List<Thread> threads = Sys.getThreads(KafkaCallService.getKafkaConsumerThreadName(node), null);
					if (threads == null) {
						ZKI.showMsgBottomRightFast_INFO("Not found thread's '%s'", node.id());
					} else {
						threads.stream().filter(Thread::isAlive).forEach(Thread::interrupt);
						ZKI.showMsgBottomRightFast_INFO("All '%s' thread's of node '%s' was INTERRUPT", X.sizeOf(threads), node.id());
					}
				});
			}
		}

	}

	//
	//-------------------- GROOVY CALL -------------------------------
	//

	public static class GroovyPlayLn extends Ln {

		public GroovyPlayLn(NodeDir node) {
			super();
			NodeEvalType.GROOVY.applyLn(this, node, null);
//			addEventListener(e -> doEventAction(node));
		}

		public static Object doEventAction(NodeDir node) {
			{
				FormState state = node.state();
				state.deletePathFc_OkErr();
				try {
					GroovyShell shell = new GroovyShell();
					Object evaluate = shell.evaluate(state.readFcData());
					state.writeFcDataOk(X.toString(evaluate, ""));
					ZKI.infoEditorBw(evaluate + "");
				} catch (Throwable ex) {
					String stackTrace = ERR.getStackTrace(ex);
					L.error("Groovy error:" + node, ex);
					state.writeFcDataErr(stackTrace);
					ZKI.alert(stackTrace);
				}

			}
			return 0;
		}
	}


	//
	//-------------------- SWAP TG CALL -------------------------------
	//

	public static class SwapTgPostLn extends Ln {

		public static final String PT_TG_WIDGET = "<script src=\"https://telegram.org/js/telegram-widget.js?22\" data-telegram-post=\"{0}\" data-width=\"100%\"></script>";
		public static final String SCRIPT_PFX = "<script src=";

		public static String getDataTelegramPost(String html, String... defRq) {
			Document document = Jsoup.parse(html);
			Element scriptElement = document.selectFirst("script[data-telegram-post]");
			if (scriptElement != null) {
				return scriptElement.attr("data-telegram-post");
			}
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Except valid script[data-telegram-post], from %s", html), defRq);
		}

		public SwapTgPostLn(NodeDir node) {
			super(SYMJ.ARROW_REPEAT_LEFT_TH);
			title("Swap tg post link to script");
			addEventListener(e -> {
				String swapedValue;
				String line0 = node.state().nodeLine(0);

				boolean skip = false;
				if (isTmeHostPattern(line0)) {
					line0 = line0.substring(NT.TG.HOST_URL().length());
					LID lid = LID.of(line0);
					String chatName = lid.first();
					Long msgId = lid.secondLong();
//					String[] pageItem = UUrl.getPathFirstAndSencondItemFromUrl(line0);

					swapedValue = X.fm(PT_TG_WIDGET, chatName + "/" + msgId);

					node.state().fields().set_VIEW(NodeDir.NVT.HTML);

				} else if (isScriptPattern(line0)) {

					swapedValue = NT.TG.HOST_URL() + getDataTelegramPost(line0);

					node.state().fields().set_VIEW(NodeDir.NVT.TEXT);

				} else {
					swapedValue = null;
					skip = true;
				}

				if (!skip && swapedValue != null) {
					node.state().writeFcData(swapedValue);
				}

//				NotesSpace.rerenderFirst();
				ZKR.restartPage();

			});
		}

		private boolean isTmeOrScriptPattern(String line0) {
			return isTmeHostPattern(line0) || isScriptPattern(line0);
		}

		public static boolean isScriptPattern(String line0) {
			return line0 != null && line0.startsWith(SCRIPT_PFX);
		}

		private static boolean isTmeHostPattern(String line0) {
			return line0 != null && line0.startsWith(NT.TG.HOST_URL());
		}
	}
}
