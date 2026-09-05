package mpc.time;

import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;

import java.util.concurrent.TimeUnit;

public class QDelay extends Pare<QDelayType, Long> {

    public static QDelay ofShortPattern(String shortPattern, QDelay... defRq) {
        Pare<QDelayType, Long> qDelayLongPare = QDelayType.valueOfAsPare(shortPattern, null);
        if (qDelayLongPare != null) {
            return ARG.throwMsg(() -> X.f("Except valid qd pattern [%s]", shortPattern), defRq);
        }
        return new QDelay(qDelayLongPare.key(), qDelayLongPare.val());
    }

    public QDelay(QDelayType key, Long val) {
        super(key, val);
    }

    public Long getDelay() {
        return val();
    }

    public QDelayType getDelayType() {
        return key();
    }

    public Long getDelay(TimeUnit timeUnit) {
        return key().getDelay(getDelay(), timeUnit);
    }
}