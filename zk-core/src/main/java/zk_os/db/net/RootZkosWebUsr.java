package zk_os.db.net;


import mpu.core.QDate;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.STR;
import zk_os.db.WebUsrService;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.nio.file.Paths;


@Entity
@Table(name = "usr")
public class RootZkosWebUsr extends ZkosWebUsr {

	public static final long ROOT_SID = 1L;
	public static final String ROOT_NAME = "bea";
	public static final String ROOT_LOGIN = "bearoot";

	public RootZkosWebUsr() {
		this(null);
	}

	public RootZkosWebUsr(String hash) {
		super();
		super.setSid(ROOT_SID);
		if (hash != null) {
			setFirst_name(ROOT_NAME);
			setLogin(ROOT_LOGIN);
			setRoles(ROLE.ROLE_OWNER);
			setLast_name("lastName_" + QDate.now().f(QDate.F.MONO20NF));
			setPhc(hash);
		}
	}


	public static Pare<WebUsr, String> init() {
		String[] phc = Sec.Phc.generateRandomPass();
		WebUsr webUsr = new RootZkosWebUsr(phc[1]);
		RW.write(Paths.get("logs/init.log"), webUsr.getLogin() + STR.NL + phc[0]);
		return Pare.of(webUsr, "FirstInit:true:" + webUsr.getLogin() + STR.NL + phc[0]);
	}

	public static WebUsr load() {
		return WebUsrService.findWebUsrRoot();
	}
}
