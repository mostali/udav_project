package zk_os.db.net;


import mpu.core.QDate;
import zk_os.sec.ROLE;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "usr")
public class RootZkosWebUsr extends ZkosWebUsr {

	public static final long ROOT_ID = 1L;

	public RootZkosWebUsr() {
		this(null);
	}

	public RootZkosWebUsr(String hash) {
		super();
		super.sid = ROOT_ID;
		if (hash != null) {
			first_name = "Root";
			login = "bearoot";
			roles = ROLE.ROLE_OWNER;
			last_name = "lastName_" + QDate.now().f(QDate.F.MONO20NF);
			this.phc = hash;
		}
	}
}
