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
		sid = Long.MAX_VALUE;
		first_name = "Bear";
		login = "bear";
		phc = "123";
		roles = JOIN.argsBy(" ", JOIN.allBy(" ", ARR.as(ROLE.OWNER.name())));
		last_name = QDate.now().f(QDate.F.MONO20NF);
	}
}
