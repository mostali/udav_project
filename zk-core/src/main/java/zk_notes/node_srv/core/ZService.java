package zk_notes.node_srv.core;

import mpc.exception.CleanDataResponseException;
import mpc.exception.NI;
import mpe.cmsg.core.INode;
import mpe.cmsg.core.NodeSrv;
import mpu.core.QDate;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.core.IZWin;
import zk_form.ext.BiVF;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.PlayContainer;
import zk_os.tasks.TaskManager;

import java.util.function.Supplier;

public interface ZService extends NodeSrv {

    default PlayContainer toPlayContainer(PlayContainer.PlayLn playLn) {
        return new PlayContainer(playLn);
    }

    default CleanDataResponseException evalAsSendResponse(NodeDir nodeDir, EvalOpts opts) {

        Supplier<String> runner = () -> nodeDir.stdSrvAny().evalAsString(nodeDir, opts);

        if (opts.isAsync) {

            //!! init track context in current thread

            String taskName = "async-" + nodeDir.nodeName() + "-" + QDate.now().mono4_h2m2();

            TaskManager.addTaskAsync(taskName, runner);

            throw CleanDataResponseException.OK("RunAsync=" + taskName);

        } else {

            String rsp = runner.get();
//
            rsp = opts.applyJpXp(rsp);

            throw CleanDataResponseException.ofNotEmptyRsp_or400(rsp, nodeDir.nodeName());
        }
    }

    String evalAsString(INode node, EvalOpts opts);

    default boolean applyBeStyle(Pare<Window, IZWin> com) {
        return false;
    }

    default Component buildView(INode node, BiVF biVF) {
        NI.stop("unsupported node " + node);
        return null;
    }
}
