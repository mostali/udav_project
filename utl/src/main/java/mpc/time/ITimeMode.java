package mpc.time;

import java.util.List;

public interface ITimeMode {

	boolean isTime(int hh);

	List<ITimeMode> modeValues();

	String modeName();

}
