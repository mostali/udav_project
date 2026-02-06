package zklogapp.filter;

import lombok.RequiredArgsConstructor;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.exception.RequiredRuntimeException;
import mpu.str.SPLIT;
import mpc.str.sym.SYM;
import mpu.pare.Pare3;
import mpc.ui.ColorTheme;
import mpe.logs.filter.ILogFilter;
import mpe.logs.filter.filters.KeyLineCondition;
import org.zkoss.zk.ui.event.Events;
import zk_com.base.Cb;
import zk_com.base_ctr.Span0;
import zk_com.base_ext.Bandbox0;
import zk_page.ZKS;
import zklogapp.AppLogCore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KeyDdFilter extends Span0 implements ILogFilter {

	final boolean include_exclude;

	final boolean ignoreCase;

	private Bandbox0 bdxPhrasesKeys;

	@Override
	protected void init() {
		super.init();

		appendLb(include_exclude ? "+" : SYM.DASH_MIDDLE);
		appendChild(bdxPhrasesKeys = Bandbox0.buildComponent(getPhrasesKeys().stream().map(vl -> new Cb(vl)).collect(Collectors.toList())));
		bdxPhrasesKeys.addEventListener(Events.ON_OK, e -> onDefaultActionEvent(e));
		bdxPhrasesKeys.setPlaceholder(include_exclude ? "include" : "exclude");

		ZKS.FLOAT(this, false);
		ZKS.BGCOLOR(this, ColorTheme.GREEN[0]);
		ZKS.WIDTH(bdxPhrasesKeys, 150);
	}

	private List<Pare3<String, String, String>> getPhrasesPares() {
		List<Pare3<String, String, String>> phrases = AppLogCore.TREE_EXCLUDE_PHRASES.getModelsAsPare3();
		return phrases;
	}

	private List<String> getPhrasesKeys() {
		return getPhrasesPares().stream().map(p -> p.key()).collect(Collectors.toList());
	}

	private List<String> getPhrases(String setKey, List<String>... defRq) {
		Pare3<String, String, String> pare3 = getPhrasesPares().stream().filter(p -> p.key().equals(setKey)).findAny().orElseGet(() -> null);
		if (pare3 != null) {
			return SPLIT.allByNL(pare3.val());
		}
//		orElseGet()orElseThrow(() -> new FIllegalArgumentException("PhrasesSet by key '%s' not found", setKey))
		return ARG.toDefThrow(() -> new RequiredRuntimeException("PhrasesSet by key '%s' not found", setKey), defRq);
	}

	@Override
	public KeyLineCondition toFilter() {
		List allPhrases = new ArrayList();
		if (X.isNotEqObjAny(bdxPhrasesKeys.getValue(), "", " ", "-")) {
			allPhrases.add(bdxPhrasesKeys.getValue());
		}
		List<Cb> checked = bdxPhrasesKeys.getChildren().get(0).getChildren().stream().map(i -> Cb.class.cast(i)).filter(i -> i.isChecked()).collect(Collectors.toList());
		if (X.emptyAll(checked, allPhrases)) {
			return null;
		}
		List<String> checkedStr = checked.stream().flatMap(i -> getPhrases(i.getLabel(), (List<String>) ARR.EMPTY_LIST).stream()).collect(Collectors.toList());
		if (X.emptyAll(checkedStr, allPhrases)) {
			return null;
		}
		allPhrases.addAll(checkedStr);
		return KeyLineCondition.of(include_exclude, ignoreCase, new HashSet<>(allPhrases), null);
	}

}
