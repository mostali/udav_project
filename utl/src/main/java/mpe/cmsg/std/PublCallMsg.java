package mpe.cmsg.std;

import lombok.Getter;
import mpc.str.sym.SYMJ;
import mpc.ui.ColorTheme;
import mpe.cmsg.core.CallMsg;
import mpf.CallCmdLine;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.TKN;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

//@CallMsg.CallMsgAno(st = "")
public class PublCallMsg extends CallMsg {

	//LINE0
	//TYPE
	//NAME
 	//NAME_EN--
 	//NAME_RU
 	//TITLE

	//	public static final String NAME = "PUBL";
//	public static final String TITLE = "Send PUBL";
	public static final String ICON = SYMJ.PUZZLE;
	//	public static final String ICON_LIGHT = "";
	public static final String[] COLOR = ColorTheme.ORANGE;
	//	public static final String SHORT_NAME = "publ";
//	public static final String SHORT_NAME_RU = "ПУБЛИКАЦИЯ";

	public static final String KEY = "publ";
	public static final String LINE0 = "publ:";


	public static boolean isValidKey(String msg) {
		return STR.startsWith(msg, LINE0, true);
	}

	public String _getDst(String... defRq) {
		return getHeaderValueByKey("dst", defRq);
	}

	private Integer oid = null;

	public Integer getDstVkOID() {
		if (oid != null) {
			return oid;
		}
		String dst = _getDst();
		oid = -1 * TKN.lastGreedy(dst, "g", Integer.class);//it vk group
		return oid;
	}

	public List<String> _getDstTypes() {
		return SPLIT.allBySpace(getHeaderValueByKey("dst.types", ""));
	}

	public Pare<Integer, String> _getDstUt() {
		return Pare.of(getHeaderValueByKey("dst.ut.id", Integer.class), getHeaderValueByKey("dst.ut.tk"));
	}

	public Boolean getIsRandom(Boolean... defRq) {
		return getHeaderValueByKey("dst.ut.id", Boolean.class, defRq);
	}

	public enum TYPE {
		VK
	}

	@Override
	public TYPE subtype(Object... defRq) {
		Map<String, Object> head = getHeaders_MAP();
		if (head.keySet().stream().anyMatch(k -> k.startsWith("dst:g"))) {
			return TYPE.VK;
		}
		return (TYPE) ARG.throwMsg(() -> X.f("Not found PublCallMsg"), defRq);
	}


	public PublCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(getLinesMsg())) {
			addError("Empty msg");
			return;
		}

		if (!STR.startsWith(line0, true, LINE0)) {
			addError("Except first line with starts %s", LINE0);
		}


	}

	@Override
	public String toString() {
		return "PublCallMsg{" + "msg='" + msg + '\'' + ", line='" + line0 + '\'' + ", state=" + state + ", errs=" + X.sizeOf0(getErrors()) + '}';
	}

	public static PublCallMsg of(Path file) {
		String msg = RW.readString(file);
		PublCallMsg publCallMsg = of(msg);
		publCallMsg.setFromSrc(file);
		return publCallMsg;
	}

	public static PublCallMsg of(String msg) {
		return (PublCallMsg) ofQk(msg).throwIsErr();
	}

	public static PublCallMsg ofQk(String msg) {
		return new PublCallMsg(msg);
	}

	public static boolean isValid(String data) {
		return PublCallMsg.of(data).isValid();
	}

	public static class SingleSrcLine extends CallCmdLine implements Comparable<SingleSrcLine> {

		@Override
		public String toString() {
			return line0 + " " + STR.ARR_DEL_RIGHT + (isValid() ? "ok" : getMultiOrSingleErrorOrNullStr());
		}

		public final String link;

		public Integer getLinkOid(Integer... defRq) {
			Integer last = TKN.last(link, "/club", Integer.class, null);
			return last != null ? -1 * last : ARG.throwMsg(() -> X.f("Except link oid from pattern: %s", last), defRq);
		}

		private final @Getter Integer searchDaysAgo;
		private final @Getter Integer countPost;

		public SingleSrcLine(String line) {
			super(line);

			if (line.startsWith("#")) {
				addError("line is commment: " + line);
				link = null;
				searchDaysAgo = null;
				countPost = null;
				return;
			}
			String[] two = TKN.twoGreedy(line, " ", null);

			if (!checkNotNull(two, "Illegal line format: %s", line)) {
				link = null;
				searchDaysAgo = null;
				countPost = null;
				return;
			}

			this.link = UST.URL(two[1], null);

			if (!checkNotNull(this.link, "Except url from line: %s", line)) {
				searchDaysAgo = null;
				countPost = null;
				return;
			}

			String[] params = TKN.twoGreedy(two[0], " ", null);

			this.searchDaysAgo = UST.INT(params[0], null);
			if (!checkNotNull(this.searchDaysAgo, "Except 'X:searchDaysAgo' as [ X? *:countPost *:url ] : %s", line)) {
				countPost = null;
				return;
			}
			this.countPost = UST.INT(params[1], null);
			if (!checkNotNull(this.countPost, "Except 'X:countPost' as [ *:searchDaysAgo X? *:url ] : %s", line)) {
				return;
			}

		}

		@Override
		public int compareTo(@NotNull PublCallMsg.SingleSrcLine singleDonorLine) {
			return line0.compareTo(singleDonorLine.line0);
		}

	}
}
