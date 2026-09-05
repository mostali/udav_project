package app_poi;

import mpe.str.table.Space;
import mpu.X;

import java.nio.file.Paths;
import java.util.List;

public class SpaceWb {

	public static void main(String[] args) {
//		List<List> ll = MatrixWb.toListList_Complete_String(Paths.get("/home/dav/pjm/utl-poi/src/main/java/wb0/db.xlsx"), 0);
		List<List> ll = MatrixWb.toListList_Complete_String(Paths.get("/home/dav/pjm/utl-poi/src/main/java/app_poi/db.xlsx"), 0);

		Space space = Space.of(ll,true);
//		X.exit(space);

//		Space.Cause ььфь = space.firstCause();
//		X.exit(cause);
		List<Space> all = space.findAll(true);

//		Space space2 = all.get(0);
//		Cause cause1 = space2.firstCause();
//		Space space1 = space2.trimCopy();
//		cause = space.firstCause();
//		Space space2 = cause.cleanAndPoolSpace();
//
//		cause = space.firstCause();
//		Space space3 = cause.cleanAndPoolSpace();
//
//		cause = space.firstCause();
//		Space space4 = cause.cleanAndPoolSpace();

//		X.pArr(space1, space2);
//		X.p(space2);
//		X.p(space3);
//		X.p(space4);
		X.exit(all);
		X.exit(space.firstCause().cleanAndPool_asSpace());

	}
}
