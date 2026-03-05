package mpe.cmsg.biwork;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.exception.IErrorsCollector;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpc.str.sym.SYMJ;
import mpe.cmsg.core.CallMsg;
import mpe.cmsg.core.ICallMsg;
import mpe.cmsg.core.INode;
import mpe.cmsg.core.INodeType;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class BiWorker<I extends CallMsg, R> implements IErrorsCollector {

	static final ConcurrentMap<String, BiWorker> _WORKERS = new ConcurrentHashMap<>();

	final I callMsg;

	private @Getter CompletableFuture call;

	private List<Throwable> _errors;

	private R result;

	public BiWorker onJob() {

		INode fromSrc = callMsg.getFromSrcProxy().asINode();

		INodeType evalType = ICallMsg.defineNodeType(fromSrc, false, null);
//		INodeType iNodeEvalType = StdType.valueOf(TKN.first(callMsg.line0, ":"));

//		NodeSrv service = NodeSrvReg.get(iNodeEvalType);

//		NodeMsg nodeMsg = NodeMsg.of(callMsg);
//		this.result = (R) service.doSendMsg_AsyncLog(nodeMsg, null);
		return this;
	}

	private BiWorker oldBi;

	public void startNewJob() {

		this.call = CompletableFuture.runAsync(() -> {

			onJob();

		}).exceptionally(t -> {

			L.error("Worker error, put in collector", t);

			addError(t);

			return null;

		});

		String objid = objMsgId();

		this.oldBi = _WORKERS.put(objid, this);

		L.info("startNewBiJob " + objid);

	}

	private String objMsgId() {
		return callMsg.toObjMsgId();
	}

	@Override
	public List<Throwable> getErrors() {
		return _errors != null ? _errors : (_errors = new LinkedList<>());
	}


	public Status getStatus() {
		BiWorker vWorker = _WORKERS.get(objMsgId());
		if (vWorker == null) {
			return Status.NEW;
		} else if (!vWorker.getCall().isDone()) {
			return Status.WORK;
		}
		return Status.DONE;
	}

	public R getAndThrow() {
		try {
			CompletableFuture call_ = getCall();
			return (R) call_.get();
		} catch (ExecutionException e) {
			L.error("Reg new ExecutionException", e);
			getErrors().add(e);
		} catch (InterruptedException e) {
			L.error("Reg new InterruptedException", e);
			getErrors().add(e);
		}
		throwIsErr();
		return null;
	}

	public enum Status {
		WORK, DONE, NEW;

		public String icon() {
			switch (this) {
				case NEW:
					return SYMJ.TRACK_PLAY;
				case WORK:
					return SYMJ.FAIL_STOP;
				case DONE:
					return SYMJ.OK_GREEN;
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}
}
