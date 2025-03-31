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
		super.setSid(ROOT_ID);
		if (hash != null) {
			setFirst_name("Root");
			setLogin("bearoot");
			setRoles(ROLE.ROLE_OWNER);
			setLast_name("lastName_" + QDate.now().f(QDate.F.MONO20NF));
			setPhc(hash);
		}
	}
}
