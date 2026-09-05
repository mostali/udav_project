package mpt;

import lombok.RequiredArgsConstructor;
import mpu.X;
import mpc.types.opts.SeqOptions;
import mpc.types.tks.cmt.Cmd7;

import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class TrmRq {

	@Override
	public String toString() {
		return "TrmRq{c=" + cmd + ", ua=" + userAgent + ", context=" + context + "}";
	}

	private final String cmd;
	private AtomicReference context = new AtomicReference();

	private UA userAgent;

	private transient Cmd7 cmd7;
	private transient SeqOptions seqOpts;

	public TrmRq(CharSequence cmd) {
		this.cmd = cmd.toString();
	}

	public TrmRq setUserAgent(UA userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public UA getUserAgent() {
		return this.userAgent;
	}


	public TrmRq clone(String cmd) {
		TrmRq rq = new TrmRq(cmd);
		rq.context = context;
		rq.userAgent = userAgent;
		rq.cmd7 = cmd7;
		return rq;
	}

	public String cmd() {
		return cmd;
	}

	public Cmd7<String, String, String, String, String, String, String> cmd7() {
		return cmd7 != null ? cmd7 : Cmd7.of7(cmd);
	}

	public SeqOptions seqOpts() {
		return seqOpts != null ? seqOpts : SeqOptions.of(cmd);
	}

	public static TrmRq fromTg(String cmd) {
		return new TrmRq(cmd).setUserAgent(UA.TG);
	}

	public static TrmRq fromWeb(String cmd, Object... args) {
		return new TrmRq(X.f(cmd,args)).setUserAgent(UA.WEB);
	}

	public static TrmRq fromTrm(String cmd) {
		return new TrmRq(cmd).setUserAgent(UA.TRM);
	}

//	public static TrmRq fromTrm(String cmd, Object request) {
//		return new TrmRq(cmd).setUserAgent(UA.TRM).setContext(request);
//	}

	public static TrmRq fromRest(String cmd, Object request) {
		return new TrmRq(cmd).setUserAgent(UA.REST).setContext(request);
	}

	private TrmRq setContext(Object request) {
		context.set(request);
		return this;
	}

	public String key(String... defRq) {
		return cmd7().keyObj.str(defRq);
	}

	public String val(String... defRq) {
		return cmd7().valObj.str(defRq);
	}

	public SeqOptions getSeqOpts() {
		return SeqOptions.of(cmd);
	}

	public enum UA {
		TRM, TG, REST, WEB
	}

}
