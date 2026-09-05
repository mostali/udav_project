package mpc.exception;

import mpc.rfl.RFL;
import mpe.core.U;

public class WhatIsTypeException extends IllegalArgumentException {
    public WhatIsTypeException() {
        super();
    }

    public WhatIsTypeException(Enum type) {
        this(type == null ? null : type.name());
    }

    public WhatIsTypeException(Number type) {
        this(type == null ? null : type.toString());
    }

    public WhatIsTypeException(Class type) {
        this(type.getName());
    }

    public WhatIsTypeException(int onlyClass, Object obj) {
        this(RFL.scn(obj, null));
    }

    public WhatIsTypeException(String message) {
        super(message == null ? "$" + U.__NULL__ + "$" : message);
    }

    public WhatIsTypeException(String message, Object... args) {
        this(String.format(message, args));
    }

    public WhatIsTypeException(Throwable throwable, String message) {
        super(message, throwable);
    }

    public WhatIsTypeException(Throwable throwable, String message, Object... args) {
        this(throwable, String.format(message, args));
    }


}
