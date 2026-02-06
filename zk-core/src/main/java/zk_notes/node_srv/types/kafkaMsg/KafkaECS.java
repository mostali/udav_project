package zk_notes.node_srv.types.kafkaMsg;

import api_kafka37.ApiKafka;
import api_kafka37.KafkaWalkerFunc;
import mpc.arr.STREAM;
import mpc.exception.ExistException;
import mpc.exception.WhatIsTypeException;
import mpc.json.GsonMap;
import mpe.core.ERR;
import mpe.rt.Thread0;
import mpe.call_msg.KafkaCallMsg;
import mpu.SysThreads;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Sec;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;
import zk_page.ZKME;
import zk_page.ZKRPush;

import java.util.LinkedList;
import java.util.List;

public class KafkaECS {
	public static final Logger L = LoggerFactory.getLogger(KafkaECS.class);

	public static Pare3<KafkaCallMsg, Object, Throwable> doKafkaCall(NodeDir node) {
		return doKafkaCall(node, null, true);
	}

	public static String getKafkaConsumerThreadName(NodeDir node) {
		return "KafkaConsumer-" + node.nodeId();
	}

	public static void doKafkaCall(NodeDir node, Component activeExecutionCom) {
		doKafkaCall(node, activeExecutionCom, false);
	}

	private static Pare3<KafkaCallMsg, Object, Throwable> doKafkaCall(NodeDir node, Component activeExecutionCom, boolean... RETURN) {

		boolean isExecutionEnable = activeExecutionCom != null;
		boolean isReturn = ARG.isDefEqTrue(RETURN);

		if(isExecutionEnable){
			String threadName = KafkaECS.getKafkaConsumerThreadName(node);
			if (KafkaCallMsg.of(node).kafka_method == KafkaCallMsg.KafkaMethodType.KGET && !SysThreads.isThreadActive(threadName)) {
				ZKI_Sec.log("Active push for consumer");
				ZKRPush.activePush();
			}
		}

		ObjState state = node.state();

		KafkaCallMsg kafkaCallMsg = KafkaCallMsg.ofQk(state.nodeData());

		try {

			state.deletePathFc_OkErr();

			kafkaCallMsg.throwIsErr();

			switch (kafkaCallMsg.kafka_method) {
				case KPUT: {

					Thread0<String> objThread = new Thread0("KafkaProducer-" + node.nodeId(), true) {
						@Override
						public void run() {
							Callback sendCallback = (metadata, err) -> {
								if (err != null) {
									set_result_error(err);
								} else {
									String rsp = X.f("%s:%s*%s", metadata.topic(), metadata.partition(), metadata.offset());
									set_result_object(rsp);
								}
							};
							ApiKafka.produceMsg(kafkaCallMsg, sendCallback);
						}
					};

//					if (!isReturn) {
//						return Pare3.of(kafkaCallMsg, null, null);
//					}

					String resultObject = objThread.getAndWaitResult(30_000, null);

					Pare3<KafkaCallMsg, Object, Throwable> rslt = Pare3.of(kafkaCallMsg, resultObject, objThread.getErrorsAsMultiException(null));
					if (rslt.val() != null) {
						state.writeFcDataOk(resultObject);
						ZKI.showMsgBottomRightFast_INFO(resultObject);
					}
					if (rslt.ext() != null) {
						state.writeFcDataErr(rslt.ext());
						ZKI.alert(rslt.ext());
					}
					return rslt;
				}
				case KGET: {

					Thread.State threadState = null;
					String threadName = getKafkaConsumerThreadName(node);
					if (isExecutionEnable) {
						try {
//							ZKRPush.activePushCom(KafkaPlayLn.this.getDesktop());
							threadState = SysThreads.getThreadState(threadName, null);
							if (threadState == null) {
								ZKI.showMsgBottomRightFast_INFO("Kafka Consumer Thread starting...");
							} else if (threadState == Thread.State.TERMINATED) {
								ZKI.showMsgBottomRightFast_INFO("Kafka Consumer Thread re-starting...");
								threadState = null;
							} else {
								ZKI.showMsgBottomRightFast_INFO("Kafka Consumer Thread already job with state '%s'...", threadState);
							}
						} finally {
//							ZKRPush.deactivePushCom(KafkaPlayLn.this.getDesktop());
						}
					}

					if (threadState != null) {
						String msg = X.f("KafkaCustomer '%s' is worked", threadName);
						if (isExecutionEnable) {
							ZKI.alert(msg);
						}
						Pare3<KafkaCallMsg, Object, Throwable> rslt = Pare3.of(kafkaCallMsg, msg, new ExistException(msg));
						return rslt;
					}
					Thread0<List<Pare<String, String>>> objThread = new Thread0<List<Pare<String, String>>>(threadName, true) {
						//
						@Override
						public void run() {
							try {
								ApiKafka.consumeMsg(kafkaCallMsg, new KafkaWalkerFunc() {
									@Override
									public Boolean reciveMessage_NextMsg_NoMsg_Exit(ConsumerRecord<String, String> record) {
//											String msg = record.key() + "\n=\n" + record.value() + "\n|\n";
										List<Pare<String, String>> resultObject = getResultObject(null);
										if (resultObject == null) {
											set_result_object(resultObject = new LinkedList<>());
										}
										Pare<String, String> recivedMsg = Pare.of(record.key(), record.value());
										resultObject.add(recivedMsg);
//											node.state().appendFcData(msg, 1);
										if (isExecutionEnable) {
											try {
												ZKRPush.activePushCom(activeExecutionCom, true);
												ZKI.showMsgBottomRightFast_INFO("Get kafka message -> %s = %s", recivedMsg.key(), recivedMsg.val());
											} catch (Exception ex) {
												ZKI.alert(ex, ERR.UNHANDLED_ERROR);
											} finally {
												ZKRPush.deactivePushCom(activeExecutionCom, true);
											}
										}
										return true;
									}
								});
							} catch (Exception ex) {
								L.error("Error threadHolder", ex);
								node.state().writeFcDataErr(ex);
							}
						}
					};
					if (!isReturn) {
						return null;
					}
					List<Pare<String, String>> recivedMsgs = objThread.getAndWaitResult(30_000, null);
					Pare3<KafkaCallMsg, Object, Throwable> rslt = Pare3.of(kafkaCallMsg, recivedMsgs, objThread.getErrorsAsMultiException(null));
					if (rslt.val() != null) {
						List<GsonMap> jsonMsgs = STREAM.mapToList(recivedMsgs, ksfakMsg -> GsonMap.ofKV("key", ksfakMsg.key(), "value", ksfakMsg.val()));
						String content = JOIN.allByNL(STREAM.mapToList(jsonMsgs, GsonMap::toStringPrettyJson));
						state.appendFcData(content, 1);
						if (isExecutionEnable) {
							ZKME.textReadonly(kafkaCallMsg.kafka_method + ":" + node.nodeId(), content, true);
						}
					}
					if (rslt.ext() != null) {
						state.writeFcDataErr(rslt.ext());
						if (isExecutionEnable) {
							ZKI.alert(rslt.ext());
						}
					}
					return rslt;

				}
				default:
					throw new WhatIsTypeException(kafkaCallMsg.kafka_method);

			}
		} catch (Throwable ex) {
			String msg = X.f("Kafka Call error with node '%s'", node.nodeId());
			L.error(msg, ex);
			state.writeFcDataErr(ex);
			if (isExecutionEnable) {
				ZKI.alert(ex);
			}
			return Pare3.of(kafkaCallMsg, null, ex);
		}
	}

	public static Object doEventAction(NodeDir node, Component pushHolderCom) {

		KafkaCallMsg kafkaCallMsg = KafkaCallMsg.of(node);
		switch (kafkaCallMsg.kafka_method) {
			case KPUT:
				doKafkaCall(node);
				break;
			case KGET:
				doKafkaCall(node, pushHolderCom);
				break;
			default:
				throw new WhatIsTypeException(kafkaCallMsg.kafka_method);
		}
		return 0;
	}
}
