package mpe.cmsg.std;

import mpc.rfl.RFL;
import mpe.cmsg.core.CallMsg;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.core.RW;
import mpu.str.STR;
import mpu.str.TKN;

import java.nio.file.Path;

public class QzCallMsg extends CallMsg {

    public static final String KEY = "qzeval";
    public static final String LINE0 = "qzeval:";

    public final Class<?> jobClassName;
    public final String args;

    public static boolean isValidKey(String msg) {
        return STR.startsWith(msg, LINE0, true);
    }

    public final Method method;

    @Override
    public Method subtype(Object... defRq) {
        return method;
    }

    public String getArgs(String... defRq) {
        return ARG.throwNN(args, "except args", defRq);
    }

    public enum Method {
        UNDEFINED, QPUT, QGET, QDELETE;

        public static Method of(String name, Method... defRq) {
            return ENUM.valueOf(name, Method.class, true, defRq);
        }
    }

    public QzCallMsg(String fullMsg) {
        super(fullMsg, false);

        if (X.empty(getLinesMsg())) {
            addError("Empty qz msg");
            method = Method.UNDEFINED;
            jobClassName = null;
            args = null;
            return;
        }

        String[] two_qMethod = TKN.two(line0, " ", null);
        if (two_qMethod == null) {
            addError("Except two arg QMETHOD + url, but came %s", line0);
            method = Method.UNDEFINED;
            jobClassName = null;
            args = null;
            return;
        }

        method = Method.of(two_qMethod[0].trim(), Method.UNDEFINED);
        if (method == Method.UNDEFINED) {
            addError("Except first QMETHOD from string %s", two_qMethod[0]);
            jobClassName = null;
            args = null;
            return;
        }

        String[] two_class = TKN.two(two_qMethod[1].trim(), " ", null);

        jobClassName = RFL.clazz(two_class[0].trim(), null);
        if (jobClassName == null) {
            addError("Except pattern [%s:class:node]. Set jobClassName", LINE0);
            args = null;
            return;
        }

        String[] two_args = TKN.two(two_qMethod[1].trim(), " ", null);

        args = two_args == null ? null : two_args[0].trim();

//        if (X.empty(nodeId)) {
//            addError("Except pattern [%s:class:nodeId]. Set nodeId", LINE0);
//            break;
//        }
    }

    @Override
    public String toString() {
        return "QzEvalMsg{" +
                "msg='" + msg + '\'' +
                ", line='" + line0 + '\'' +
                ", state=" + state +
                ", class=" + jobClassName +
                ", args=" + args +
                '}';
    }

    public static QzCallMsg of(Path file) {
        QzCallMsg callMsg = of(RW.readString(file));
        callMsg.setFromSrc(file);
        return callMsg;
    }

    public static QzCallMsg of(String msg) {
        return (QzCallMsg) ofQk(msg).throwIsErr();
    }

    public static QzCallMsg ofQk(String msg) {
        return new QzCallMsg(msg);
    }

    public static boolean isValid(String data) {
        return QzCallMsg.of(data).isValid();
    }

}
