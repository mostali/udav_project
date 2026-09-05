package zk_notes.node_srv.types.jqlMsg;

import mpc.json.GsonMap;
import mpc.str.sym.SYMJ;
import mpu.core.QDate;
import mpu.pare.Tuple;
import mpu.str.STR;

public class JtComment extends Tuple {

	public final GsonMap map;

	public QDate created() {
		return (QDate) super.objs[0];
	}

	public String author() {
		return (String) super.objs[1];
	}

	public String msg() {
		return (String) super.objs[2];
	}

	@Override
	public String toString() {
//            return JOIN.argsByNL(SYMJ.THINK + " " + created() + " | " + author(), msg());
//		return SYMJ.THINK + " " + created() + " | " + author() + " :: " + msg();
//		return SYMJ.THINK + " " + author() + " " + SYMJ.TIME_SANDGLASS + " | " + msg();
//		return SYMJ.THINK + " " + author() + " | " + created() + " | " + msg();
		return SYMJ.THINK + " " + author() + " | " + created() + STR.NL + msg();
	}

	public JtComment(GsonMap map, Object... objs) {
		super(objs);
		this.map = map;
	}

	public static JtComment of(GsonMap map) {
		String name = map.getAsGsonMap("author").getAsString("name");
		QDate created = map.getAsGsonMap("creationDate").getAsQDateMs("iMillis");
		String msg = map.getAsString("body");
		return new JtComment(map, created, name, msg);
	}

}
