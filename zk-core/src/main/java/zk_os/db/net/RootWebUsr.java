package zk_os.db.net;


import mpu.core.QDate;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.STR;
import zk_os.db.WebUsrService;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;

import javax.persistence.Entity;
import java.nio.file.Paths;


@Entity
//@Table(name = NetUserModel.TABLE)
public class RootWebUsr extends AppWebUsr {

	public static final long SID = 1L;
	public static final String ROOT_NAME = AppWebUsr.getDefaultUserName();
	public static final String ROOT_LOGIN = AppWebUsr.getDefaultUserName() + "root";

	public RootWebUsr() {
		this(null);
	}

	public RootWebUsr(String hash) {
		super();
		super.setSid(SID);
		if (hash != null) {
			setFirst_name(ROOT_NAME);
			setLogin(ROOT_LOGIN);
			setMainRole(ROLE.OWNER);
			setLast_name("lastName_" + QDate.now().f(QDate.F.MONO20NF));
			setPhc(hash);
		}
	}


	public static Pare<WebUsr, String> init() {
		String[] phc = Sec.Phc.generateRandomPass();
		WebUsr webUsr = new RootWebUsr(phc[1]);
		RW.write(Paths.get("logs/init.log"), webUsr.getLogin() + STR.NL + phc[0]);
		return Pare.of(webUsr, "FirstInit:true:" + webUsr.getLogin() + STR.NL + phc[0]);
	}

	public static WebUsr load() {
		return WebUsrService.findAppUsrRoot();
	}
}
