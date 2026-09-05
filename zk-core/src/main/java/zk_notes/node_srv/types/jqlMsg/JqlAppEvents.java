package zk_notes.node_srv.types.jqlMsg;

import lombok.RequiredArgsConstructor;
import mpc.str.sym.SYMJ;
import mpe.cmsg.std.JqlCallMsg;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base_ctr.Menupopup0;
import zk_notes.events.AppEvents;
import zk_page.ZKR;

public class JqlAppEvents extends AppEvents {

    public static Pare<String, SerializableEventListener> applyEvent_openTaskPage(Component clickableSrc, JqlCallMsg callMsg, String... event) {
//        if (!UFS.existFile(path)) {
//            return null;
//        }
        Pare<String, SerializableEventListener> enventDesc = Pare.of(SYMJ.BOOK_OPEN + " Open Task Page ", new OpenTaskPageSEL(callMsg));
        return applyTo(enventDesc, clickableSrc, event);
    }


    @RequiredArgsConstructor
    public static class OpenTaskPageSEL implements SerializableEventListener {
        final JqlCallMsg jqlMsg;

        public static void addToMenu(Menupopup0 playMenu, JqlCallMsg jqlMsg) {
            playMenu.addMI("Open task page", new OpenTaskPageSEL(jqlMsg));
        }

        @Override
        public void onEvent(Event event) throws Exception {
            String keyAsTaskUrl = jqlMsg.getKeyAsTaskUrl(null);
            if (keyAsTaskUrl != null) {
                ZKR.openWindow800_1200(keyAsTaskUrl);
            }
        }
    }


}
