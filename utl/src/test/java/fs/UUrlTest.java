package fs;

import mpc.url.UUrl;
import org.junit.Test;

public class UUrlTest {
	@Test
	public void test() throws Exception {
		UUrl.TestUrlPath.main(null);
		UUrl.TestUrlPathFn.main(null);
		UUrl.TestUrlPathFirstItem.main(null);
		UUrl.TestUrlPathExtension.main(null);
		UUrl.TestUrlPathExtension_First.main(null);
	}
}
