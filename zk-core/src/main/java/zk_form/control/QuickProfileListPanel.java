package zk_form.control;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.CKey;
import mpc.str.sym.SYMJ;
import mpc.ui.ColorTheme;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare3;
import mpu.str.RANDOM;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Bt;
import zk_com.base.Ln;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_form.events.DefAction;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_page.*;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class QuickProfileListPanel extends Div0 {

	public static final String MSG_SET_NAME_OF_QUICK_DATA = "set name of quick profile";
	public static final String PFX_QUICK_DATA = "set your quick data";
	public static final String PFX_TMP_QUICK_DATA = "quick-profile-";
	public static final String LN_TITLE = "new quick profile";
	public static final String BT_REMOVE_IT = "Remove it";

	private final UTree uTree;

	public static QuickProfileListPanel findFirst(QuickProfileListPanel... defRq) {
		return ZKCFinderExt.findFirst_inWin0(QuickProfileListPanel.class, true, defRq);
	}

	@Override
	protected void init() {
		super.init();

		float0(false);

		appendRight_StopPhrasesPanel(this);

	}

	private void appendRight_StopPhrasesPanel(Div0 topDiv) {

		List<Pare3<String, String, String>> pares = uTree.getModelsAsPare3();

		for (Pare3<String, String, String> profiles : pares) {

			String profielName = profiles.key();
			String profileData = profiles.val();

			Tbx tbxKey = new Tbx(profielName);
			DefAction act = (e) -> {
				uTree.removeByKeyIfExist(profielName);
				ZKC.removeParentWindowForChild(e);
				rerenderParent();
			};

			Bt btRmList = (Bt) new Bt(BT_REMOVE_IT).onCLICK(rmProfileEventAction(profielName)).margin("0 0 0 100px").bgcolor(ColorTheme.RED[0]);
			DefAction defAction = e -> ZKME.anyWithBtSave(() -> profileData, newSaveCallback(tbxKey), Span0.of(tbxKey, btRmList), false);
			Ln ln = topDiv.appendLn(defAction, SYMJ.FILE_WITH_COLOR + profielName).decoration_none();

			ln.title(profileData);

			ZKS.COLOR(ln, ColorTheme.RED[1]);

			Menupopup0 profileLnMenu = ln.getOrCreateMenupopup(topDiv);
			profileLnMenu.addMI("Copy Profile", newProfileEventAction(profileData));
			profileLnMenu.addMI("Remove Profile", rmProfileEventAction(profielName));

		}

		//
		//

		topDiv.appendChild(newBt_AddNewProfile());

	}

	private Ln newBt_AddNewProfile(String... withContent) {
		//bug with 40px - ln not visible on page
		Ln ln = (Ln) new Ln(SYMJ.PLUS).decoration_none().padding(50).title(LN_TITLE);
		ln.onCLICK(newProfileEventAction(withContent));
		return ln;
	}

	@NotNull
	private SerializableEventListener newProfileEventAction(String... withContent) {
		boolean needCheckExist = ARG.isDef(withContent);
		Tbx tbxKey = (Tbx) new Tbx(PFX_TMP_QUICK_DATA + RANDOM.alpha(3)).placeholder(MSG_SET_NAME_OF_QUICK_DATA);
		SerializableEventListener defAction = e -> ZKME.anyWithBtSave(() -> ARG.toDefOr(PFX_QUICK_DATA, withContent), newSaveCallback(tbxKey, needCheckExist), tbxKey, false);
		return defAction;
	}

	private SerializableEventListener rmProfileEventAction(String profileName) {
		return e -> ZKI_Quest.showMessageBoxBlueYN(X.f("Removing profile '%s'", profileName), X.f("Remove profile '%s'?", profileName), (yn) -> {
			if (!yn) {
				return;
			}
			getTree().removeByKeyIfExist(profileName);
			rerenderParent();
		});
	}

	@NotNull
	private Function<String, Boolean> newSaveCallback(Tbx tbxKey, boolean... checkNotExist) {
		String prevValue = tbxKey.getValue();
		Function<String, Boolean> saveCallback = dataProfile -> {
			String keyPhrases = tbxKey.getValue();
			if (X.empty(keyPhrases)) {
				ZKI.alert(MSG_SET_NAME_OF_QUICK_DATA);
				return false;
			} else if (ARG.isDefEqTrue(checkNotExist) && getTree().containsBy(CKey.of(keyPhrases))) {
				ZKI.alert("Profile '%s' already exist. Before remove it.", keyPhrases);
				return false;
			}
			addProfile(prevValue, keyPhrases, dataProfile);

			rerenderParent();

			return true;
		};
		return saveCallback;
	}

	public void addProfile(String prevProfileName, String profileName, String profileData) {
		if (!profileName.equals(prevProfileName)) {
			uTree.removeByKeyIfExist(prevProfileName);
		}
		uTree.put(profileName, profileData);
	}

	public void rerenderParent() {
//		if (ARG.isDefEqTrue(fullRestart)) {
		ZKR.restartPage();
//		}
//		replaceWith(new QuickProfileListPanel(getTree()));
	}

	public UTree getTree() {
		return uTree;
	}

}
