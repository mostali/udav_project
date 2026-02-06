package zklogapp.filter;

import lombok.Getter;
import mpc.fs.UFS;
import mpu.X;
import mpc.arr.STREAM;
import mpc.fs.fd.EFT;
import mpc.rfl.RFL;
import mpc.str.condition.LogGetterDate;
import mpc.str.condition.StringCondition;
import mpc.ui.ColorTheme;
import mpe.logs.filter.LogProc;
import mpe.logs.filter.ILogFilter;
import mpe.logs.filter.ILogFilterProcessor;
import mpe.logs.filter.merger.LogFile;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import zk_com.base.Bt;
import zk_com.base.Cb;
import zk_com.base_ctr.Div0;
import zk_com.core.IZCom;
import zk_com.editable.EditableValue;
import zk_form.dirview.DirView;
import zk_form.dirview.DirView0;
import zk_form.events.DefAction;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_page.ZKC;
import zk_page.ZKS;
import zk_page.events.ECtrl;
import zklogapp.ALI;
import zklogapp.merge.BtMerge;
import zklogapp.header.LogMergerPageHeader;
import zklogapp.merge.LogDirView;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class HeadLogFilter extends Div0 implements ILogFilterProcessor {

	private final Bt btHeadOk;
	private final EditableValue edvPath;

	final String path;

	public HeadLogFilter(Bt btHeadOk, String path) {
		super();

		this.path = path;

		this.btHeadOk = btHeadOk;
		this.edvPath = new EditableValue(path) {

			@Override
			protected void init() {
				super.init();

				ZKS.MARGIN(edvPath, "0px 10px");
				ZKS.COLOR(edvPath, ColorTheme.YELLOW[0]);
				ZKS.FONT_FAMILY0(edvPath.getViewCom());
				ZKS.WIDTH_HEIGHT(edvPath.getViewCom(), 200, 20);
				ZKS.DISPLAY(edvPath.getViewCom(), 1);
				ZKS.OVERFLOW(edvPath.getViewCom(), 1);

				getViewCom().setTooltiptext(path);
			}

			EFT ft = null;

			EFT getOrgFileType() {
				return ft != null ? ft : (ft = EFT.of(((Label) getViewCom()).getValue()));
			}

			@Override
			protected void onChangeStateToEditableOrLabel(Event e, boolean editOrLabel, boolean... isCancel) {
				if (editOrLabel) {
					super.onChangeStateToEditableOrLabel(e, editOrLabel, isCancel);
					return;
				}
				String newFd = getEditCom().getValue();
				EFT orgFileType = getOrgFileType();
				if (orgFileType == EFT.FILE && UFS.isDir(newFd)) {
					ZKI_Quest.showMessageBoxBlueYN(null, "Open Log Dir View?", yn -> {
						if (yn) {
							LogDirView.openSingly(newFd);
						}
					});
					return;
				} else {
					if (!orgFileType.existSave(newFd)) {
						ZKI.alert("Except existed %s '%s'", orgFileType, newFd);
						return;
					}
				}
				super.onChangeStateToEditableOrLabel(e, editOrLabel, isCancel);

				LogMergerPageHeader.addLogView(newFd);

				onChangeNewPath(newFd);
			}
		};


	}

	protected abstract void onChangeNewPath(String newPath);

	@Getter
	private LogLevelFilter logLevelFitler;
	@Getter
	private BwDatesFilter logBwDatesFilter;
	private BwKeysFilter logBwKeysFilter;
	@Getter
	private KeyDdFilter logIncludePhrasesDdFilter, logExcludePhrasesDdFilter;
	@Getter
	private Cb cbCollapseMultiLine;

	@Getter
	private Cb cbOne;

	@Override
	protected void init() {
		super.init();

		appendLn((DefAction) e -> ((IZCom) getParent()).removeMe(), ALI.CLOSE);

		cbOne = new Cb(true).moldSwitch();

		appendChilds(cbOne, btHeadOk, edvPath);

		cbCollapseMultiLine = (Cb) new Cb().moldToggle().checked(!(btHeadOk instanceof BtMerge)).onDefaultAction(e -> onDefaultActionEvent((Event) e));
		cbCollapseMultiLine.title("Collapse multiline log's to single line").float0(false).margin(5);

		appendChild(cbCollapseMultiLine);

		appendChild(this.logLevelFitler = new LogLevelFilter() {
			@Override
			public void onDefaultActionEvent(Event event) {
				HeadLogFilter.this.onDefaultActionEvent(event);
			}
		});


		appendChild(this.logBwDatesFilter = new BwDatesFilter() {
			@Override
			public void onDefaultActionEvent(Event event) {
				HeadLogFilter.this.onDefaultActionEvent(event);
			}
		});

//		appendChild(this.logBwKeysFilter = new BwKeysFilter() {
//			@Override
//			public void onDefaultActionEvent(Event event) {
//				HeadLogFilter.this.onDefaultActionEvent(event);
//			}
//		});

		appendChild(logIncludePhrasesDdFilter = (KeyDdFilter) new KeyDdFilter(true, false) {
			@Override
			public void onDefaultActionEvent(Event event) {
				HeadLogFilter.this.onDefaultActionEvent(event);
			}
		}.title("include filter"));
		appendChild(logExcludePhrasesDdFilter = (KeyDdFilter) new KeyDdFilter(false, false) {
			@Override
			public void onDefaultActionEvent(Event event) {
				HeadLogFilter.this.onDefaultActionEvent(event);
			}
		}.title("exclude filter"));

		ZKS.BGCOLOR(this, ColorTheme.BLUE[0]);
		ZKS.PADDING(this, "10px");
		//		ZKS.OPACITY(this, "0.8");
		//		ZKS.FLOAT(this,false);
		//		ZKS.HEIGHT_MIN(this, "40px");

	}

	public ArrayList<String> processFile(LogGetterDate logGetterDate, String pathOrg, boolean explodeMultiline) {
		List<String> lines = LogFile.parseLogLinesBlockAsString(pathOrg, logGetterDate);
		return processLines(logGetterDate, lines, explodeMultiline);
	}

	public ArrayList<String> processLines(LogGetterDate logGetterDate, List<String> linesIn, boolean explodeMultiline) {
		LogProc logProc = LogProc.of(logGetterDate);
		List<ILogFilter> filters = getFilters();
		ZKI.log("Will be used filter:" + filters.stream().map(f -> f.toFilter()).filter(X::NN).map(StringCondition::toStringFnPart).collect(Collectors.joining("; ")));
		for (ILogFilter logFilterCom : filters) {
			StringCondition filter = logFilterCom.toFilter();
			if (filter != null) {
				logProc.addFilter(filter);
			}
		}
		ArrayList<String> linesOut = logProc.process(linesIn);
		ZKI.log("Apply filter before/after %s/%s", X.sizeOf(linesIn), X.sizeOf(linesOut));

		if (explodeMultiline) {
			linesOut = LogFile.explodeMultiline(linesOut);
		}
		return linesOut;
	}

	private List<ILogFilter> getFilters() {
		List<ILogFilter> iLogFilters = RFL.fieldValues(HeadLogFilter.class, this, ILogFilter.class, false);
		return STREAM.filterToList(iLogFilters, X::NN);
	}


	@Override
	public void onDefaultActionEvent(Event event) {
		boolean allowedAction = Cb.isChecked(cbOne, true, true);
		if (!allowedAction) {
			return;
		}
		onHeadOk();
	}

	public void onHeadOk() {

	}

	public String toStringFnPart() {
		return getFilters().stream().filter(X::NN).map(ILogFilter::toStringFnPart).filter(X::NN).collect(Collectors.joining("_"));
	}
}
