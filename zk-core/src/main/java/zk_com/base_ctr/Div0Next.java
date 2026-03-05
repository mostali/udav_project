package zk_com.base_ctr;

import lombok.Getter;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.core.ARRi;
import org.zkoss.zk.ui.Component;
import zk_com.core.IZComExt;

import java.util.List;

public class Div0Next extends Div0 implements IZComExt {

	private @Getter Div0 divCurrent;
	private @Getter Div0 divNext;

	public Div0 getDivNext() {
		return divNext != null ? divNext : (divNext = new Div0());
	}

	public Div0 getDivCurrent() {
		return divCurrent != null ? divCurrent : (divCurrent = new Div0());
	}

	@Override
	protected void init() {
		super.init();
		super.appendChild(getDivCurrent());
	}

	@Override
	public boolean appendChild(Component child) {
		return getDivCurrent().appendChild(child);
	}

	public boolean appendChildNext(Component child) {
		return getDivNext().appendChild(child);
	}


	private Integer accumulateSize = 1;

	public Div0Next accumulateSize(Integer... accumulateSize) {
		this.accumulateSize = ARG.toDefOr(1, accumulateSize);
		return this;
	}

	public void replaceNextDiv() {

		Div0 divCurrent1 = getDivCurrent();

		List<Component> children = getChildren();
		int curSizeAll = X.sizeOf(children);
		boolean isLarge = curSizeAll >= accumulateSize;
		if (isLarge) {
			boolean remove = children.remove(divCurrent1);
			boolean remove2 = getComs().remove(divCurrent1);
			divCurrent1.detach();
			X.nothing();
		}

		this.divCurrent = null;

		Div0 next = getDivNext();
		super.appendChild(next);

		this.divNext = null;

		if (accumulateSize == 1) {
			this.divCurrent = next;
		} else if (isLarge || curSizeAll == accumulateSize - 1) {
			this.divCurrent = (Div0) ARRi.first(getComs());
		}
	}

//	public void replaceNextDiv() {
//		Div0 divPrev = getDivCurrent();
//		divPrev.detach();
//		getChildren().remove(divPrev);
//
//		this.divCurrent = getDivNext();
////	stream().forEach(c -> c.detach());
////		getChildren().clear();
//		super.appendChild(divCurrent);
//		divNext = null;
//	}
}
