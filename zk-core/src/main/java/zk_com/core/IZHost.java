package zk_com.core;

public interface IZHost extends IZComExt {

	default boolean isRevert() {
		return false;
	}

	IZHost isRevert(boolean isRevert);

}
