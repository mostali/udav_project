package zk_os.db.net;


import mpu.core.QDate;
import zk_os.sec.ROLE;


//@Entity
//@Table(name = NetUserModel.TABLE)
public class SimpleBearWebUsr extends AppWebUsr {

	public SimpleBearWebUsr() {
		this(null);
	}

	public SimpleBearWebUsr(String hash) {
		super(null, "Bear");
		setSid(Long.MAX_VALUE);
		setLogin("bear");
		setPhc("123");
		setMainRole(ROLE.OWNER);
		setLast_name(QDate.now().f(QDate.F.MONO20NF));
	}
}
