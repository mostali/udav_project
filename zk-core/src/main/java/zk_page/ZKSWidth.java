package zk_page;

import lombok.RequiredArgsConstructor;
import mpu.X;
import mpu.core.ARG;
import org.zkoss.zul.Window;

public class ZKSWidth {

    public static void applyWidthHeight(Window w, String[] wh) {

        Vflex wFlex = Vflex.valueOf(wh[0], null);
        if (wFlex != null) {
            wFlex.applyToCom(w, true);
        } else {
            w.setWidth(wh[0]);
        }

        Vflex hFlex = Vflex.valueOf(wh[1], null);
        if (hFlex != null) {
            hFlex.applyToCom(w, false);
        } else {
            w.setHeight(wh[1]);
        }

    }

    @RequiredArgsConstructor
    public enum Vflex {
        MAX("1"), MIN("min"), OFF("0");
        final String val;

        public static Vflex valueOf(String val, Vflex... defRq) {
            for (Vflex value : values()) {
                if (value.val.equals(val)) {
                    return value;
                }
            }
            return ARG.throwMsg(() -> X.f("Except by value [%val]", val), defRq);
        }

        public void applyToCom(Window w, boolean isWidthOrHeight) {
            if (isWidthOrHeight) {
                w.setVflex(val);
            }else {
                w.setHflex(val);
            }
        }
    }


}
