package mpe.str;

import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.IT;
import mpu.X;
import mpc.str.sym.SYMJ;
import mpu.str.Sb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class StringWalkBuilder<M> {

	private String sepNL = "\n";
	private String pfxLine = null;
	private String pfxMsg = null;
	public String messageIfEmpty = "empty";

	private final Function<M, String> line_builder;

	public StringWalkBuilder<M> pfxLine(String head) {
		this.pfxLine = head;
		return this;
	}

	public StringWalkBuilder<M> pfxMsg(String headGlobal) {
		this.pfxMsg = headGlobal;
		return this;
	}

	public static <M> StringWalkBuilder<M> of() {
		return new StringWalkBuilder(DEF_INFO_LINE_BUILDE).ol();
	}

	public static <M> StringWalkBuilder<M> of(Function<M, String> builderLine) {
		return new StringWalkBuilder(builderLine);
	}

	public StringWalkBuilder() {
		this(null);
	}

	public StringWalkBuilder(Function<M, String> line_builder) {
		this.line_builder = line_builder;
	}

	public StringBuilder buildSB(M... messages) {
		return buildSB(Arrays.asList(messages));
	}

	public StringBuilder buildSB(Collection<M> list) {
		return buildSB(new ArrayList(list));
	}

	public StringBuilder buildSB(List<M> list) {
		if (X.empty(list)) {
			return newSbWithHead(IT.notNull(messageIfEmpty));
		}
		StringBuilder sb = newSbWithHead();
		for (int i = 0; i < list.size(); i++) {
			M m = list.get(i);
			if (ol) {
				sb.append(i + ") ");
			}
			String str = buildSingleLineMsg(m);
			sb.append(str);
			if (sepNL != null && i != list.size() - 1) {
				sb.append(sepNL);
			}
		}
		return sb;
	}

	public String buildSingleLineMsg(M mdl) {
		Object line = mdl;
		if (line_builder != null) {
			line = line_builder.apply(mdl);
		}
		return newSbWithHead(line).toString();
	}

	private StringBuilder newSbWithHead(Object... line) {
		Sb sb = new Sb();
		if (pfxMsg != null) {
			sb.append(pfxMsg);
			pfxMsg = null;
		}
		if (pfxLine != null) {
			sb.append(pfxLine);
		}
		if (ARG.isDef(line)) {
			sb.append(ARG.toDef(line));
		}
		return sb.to();
	}


	public StringWalkBuilder<M> messageIfEmpty(String messageIfEmpty) {
		this.messageIfEmpty = messageIfEmpty;
		return this;
	}

	boolean ol = false;

	public StringWalkBuilder<M> ol(boolean... olTrue) {
		ol = ARGn.toDefOr(true, olTrue);
		return this;
	}

	public static final Function<String[], String> DEF_INFO_LINE_BUILDE = l -> l[0] + " " + SYMJ.ARROW_RIGHT_SPEC + " " + l[1];

	public static StringBuilder getInfoString(String[][] info, Function<String[], String>... line_builder) {
		return new StringWalkBuilder<String[]>(ARG.toDefOr(DEF_INFO_LINE_BUILDE, line_builder)).buildSB(info);
	}

}
