package mpc.rfl;

//Reflection
public interface IRfl {

	default String scn() {
		return getClass().getSimpleName();
	}

	default String className() {
		return getClass().getName();
	}

}
