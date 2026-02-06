package zk_form.ext;

import lombok.Getter;
import mpc.arr.STREAM;
import mpc.ui.ColorTheme;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ENUM;
import mpu.func.FunctionV3;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Bt;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0M;
import zk_page.ZKS;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemsPicker<T> extends Div0M {

	@Getter
	protected List<IItem<T>> loadItems;

	protected Set<T> choicedItems;

	public List<IItem<T>> loadItems() {
		return loadItems;
	}

	boolean allowSelectManyItems = false;

	public ItemsPicker withAllowSelectManyItems(boolean... allowSelectManyItems) {
		this.allowSelectManyItems = ARG.isDefNotEqFalse(allowSelectManyItems);
		return this;
	}

	public ItemsPicker() {
		super();
	}

	public ItemsPicker(Collection<? extends CharSequence> items) {
		super();
		this.loadItems = (List) STREAM.mapToList(items, IItem::of);
	}

	public ItemsPicker(Class<? extends Enum> itemType) {
		super();
		this.loadItems = (List) ENUM.getValues(itemType).stream().map(IItem::of).collect(Collectors.toList());
	}

	public ItemsPicker(List<IItem<T>> loadItems) {
		super();
		this.loadItems = loadItems;
	}

	@Override
	protected void init() {
		super.init();

		List<IItem<T>> items = loadItems();

		AtomicReference<Bt> choiceBtRef = new AtomicReference();
		if (allowSelectManyItems) {
			Bt choiceBt = appendBt(null, "Select");
			choiceBt.absolute().fixed();
			choiceBt.addSTYLE("top:3%;left:30%");
			choiceBt.setVisible(false);
			choiceBtRef.set(choiceBt);
			choiceBt.onCLICK(e -> onHappensClickItems(e, choicedItems));
		}

		if (allowSelectManyItems) {
			choicedItems = new LinkedHashSet();
		}

		FunctionV3<Ln, T, Boolean> onChoice = (com, src, on) -> {
			if (on) {
				com.borderRed();
				com.attr_put("chp", true);
				choicedItems.add(src);
			} else {
				ZKS.BORDER(com, null);
				com.attr_rm("chp");
				choicedItems.remove(src);
			}
			Supplier<String> onChoiceLabel = () -> X.f("Select [" + choicedItems.size() + "] items %s", choicedItems);
			Bt btChoiceProjects = choiceBtRef.get();
			btChoiceProjects.setLabel(onChoiceLabel.get());
			btChoiceProjects.setVisible(!choicedItems.isEmpty());
		};

		for (IItem<T> item : items) {
			Ln lb = (Ln) appendLn(null, item.getLabelName()).block();
			applyStyleForItem(lb);
			if (allowSelectManyItems) {
				if (isSelected(item.getSrcItem())) {
					onChoice.apply(lb, item.getSrcItem(), true);
				}
				lb.onCLICK(e -> onChoice.apply(lb, item.getSrcItem(), !lb.attr_is("chp", false)));
			} else {
				lb.onCLICK((Event e) -> onHappensClickItems(e, ARR.asHSET(item.getSrcItem())));
			}
		}
	}

	@Override
	protected void applyStyle(Div0M modalCom) {
		super.applyStyle(modalCom);
		ZKS.ABSOLUTE(this);

		ZKS.ZINDEX(this, 9999);
		ZKS.LEFT(this, 0);
		ZKS.TOP(this, 0);

		ZKS.WIDTH_HEIGHT(this, 100.0, X.sizeOf(loadItems()) * 200);
	}

	protected void applyStyleForItem(Ln lb) {
		ZKS.BGCOLOR(lb, ColorTheme.GRAY[0]);
		ZKS.MARGIN(lb, "50px");
		ZKS.PADDING(lb, "50px");
		ZKS.BORDER(lb, "1px solid silver");
	}

	public boolean isSelected(T srcItem) {
		return false;
	}

	protected void onHappensClickItems(Event event, Collection<T> selectedItems) {
		onHappensClosePciker();
	}
}
