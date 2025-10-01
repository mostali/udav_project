package zk_com.listbox;

import lombok.Getter;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.exception.FIllegalStateException;
import mpc.types.abstype.AbsType;
import mpe.core.U;
import mpe.ftypes.core.FDate;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.core.QDate;
import mpu.str.UST;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.editable.EditableValue;
import zk_form.notify.ZKI;
import zk_page.ZKS;
import zk_page.events.ZKE;

import java.nio.file.Path;
import java.util.List;

public class CellEditableValue extends EditableValue {

	private final @Getter AbsType val;
	private final @Getter Path pathDb;
	private final @Getter Integer[] xyIndex;

	//	private SubmitEvent submitEvent;
	public CellEditableValue(AbsType vl, Path pathDb, Integer[] xyIndex) {
		super(vl.getValueAsString());
		this.val = vl;
		this.pathDb = pathDb;
		this.xyIndex = xyIndex;
//		if (vl.val() == null) {
//		}
//		ZKS.HEIGHT_MAX(getViewCom(), 100);
		ZKS.WIDTH(getViewCom(), 100.0);
		ZKS.HEIGHT(getViewCom(), 100);
//		ZKS.WIDTH_HEIGHT100(getViewCom());
		ZKS.BLOCK(getViewCom());

		ZKS.WIDTH(getEditCom(), 100.0);
		setEditableRows(6);
		ZKE.addEventListenerCtrl(this, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_CTRL_S, new SerializableEventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
//				InputEvent e = (InputEvent) event;
				onUpdatePrimaryText(getEditCom().getValue());
			}
		});

	}


	private Long getModelId() {
		return UST.LONG((String) getParent().getParent().getAttribute("i"));
	}

	ICtxDb.CtxModel getCtxTimeModelById(ICtxDb.CtxModel... defRq) {
		try {
			List<ICtxDb.CtxModel> models = getCtxDb().getModels(QP.pID(getModelId()));
			return ARRi.first(models);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	private ICtxDb getCtxDb() {
		return ICtxDb.of(pathDb);
	}

	@Override
	protected void onUpdatePrimaryText(String newValue) {

		ICtxDb.CtxModel model = getCtxTimeModelById(null);
		if (model != null) {
			onUpdatePrimaryText_UTree(model, newValue);
		} else {
			//			if (index < 0) {
			//				return;
			//			}
//			ZKI.alert();
//			throw new FIllegalStateException("Could not update without column 'id':" + ARR.as(xyIndex) + ":" + newValue);
			throw new FIllegalStateException("Could not update without primary key (column 'id')");
		}

	}

	protected void onUpdatePrimaryText_UTree(ICtxDb.CtxModel model, String newValue) {

		boolean isNull = U.is__NULL__(newValue);
		if (isNull) {
			newValue = null;
		}
		switch (val.name()) {
			case "id": {
				Long ind = getModelId();
				ZKI.alert("primary key " + ind);
				return;
			}
			case "key": {
				model.setKey(newValue);
				break;
			}
			case "val": {
				model.setValue(newValue);
				break;
			}
			case "ext": {
				model.setExt(newValue);
				break;
			}
			case "time": {
				if (newValue == null) {
					ZKI.alert("Illegal state with NULL and time field");
					return;
				} else {
					QDate ms = QDate.of(newValue, FDate.UTC_MS);
					model.setTime(ms.getTime());
				}
				break;
			}
			default:
				model.setObjectField(val.name(), newValue);
				break;
//				throw new WhatIsTypeException(val.name());
		}

		getCtxDb().saveModelAsUpdate(model);

		ZKI.infoAfterPointer("Update row #" + model.getId(), ZKI.Level.INFO);

		super.onUpdatePrimaryText(isNull ? "null" : newValue);
	}


}
