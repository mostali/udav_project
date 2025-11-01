package zk_os.db.net;


import mpu.core.ARR;
import mpu.str.JOIN;
import mpu.core.QDate;
import zk_os.sec.ROLE;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "usr")
public class SimpleBearWebUsr extends ZkosWebUsr {

	public SimpleBearWebUsr() {
		this(null);
	}

	public SimpleBearWebUsr(String hash) {
		super();
		setSid(Long.MAX_VALUE);
		setFirst_name("Bear");
		setLogin("bear");
		setPhc("123");
		setRoles(JOIN.argsBy(" ", JOIN.allBy(" ", ARR.as(ROLE.OWNER.name()))));
		setLast_name(QDate.now().f(QDate.F.MONO20NF));
	}
}
