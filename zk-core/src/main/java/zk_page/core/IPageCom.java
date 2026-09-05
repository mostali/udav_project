package zk_page.core;

import zk_com.base_ext.DoubleLn;
import zk_com.core.IZState;

public interface IPageCom {
	static boolean isPagecom(IZState izState) {
		if (izState instanceof IPageCom) {
			return true;
		}
		return izState instanceof DoubleLn && ((DoubleLn) izState).getComs().get(0) instanceof IPageCom;
	}

	String getPageComName();
}
