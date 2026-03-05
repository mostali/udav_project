package zk_notes.node_srv.core;

import mpe.cmsg.core.NodeSrv;
import mpu.pare.Pare;
import org.zkoss.zul.Window;
import zk_com.core.IZWin;
import zk_notes.node_srv.PlayContainer;

public interface ZService extends NodeSrv {

	default PlayContainer toPlayContainer(PlayContainer.PlayLn playLn) {
		return new PlayContainer(playLn);
	}

	default boolean applyBeStyle(Pare<Window, IZWin> com) {
		return false;
	}

}
