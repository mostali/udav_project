package mpf.arc;

import java.io.IOException;

public interface IUnpacker {
	void unpack(String packFileLocation, String destDirLocation) throws IOException;
}
