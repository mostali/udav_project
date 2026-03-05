package zk_os.db.net;


import mpt.IAnonim;
import zk_os.sec.ROLE;

public class AnonimAppWebUsr extends AppWebUsr implements IAnonim {

	public static final AnonimAppWebUsr ANONIM_USER = new AnonimAppWebUsr();

	public static final Long SID = 0L;

	public AnonimAppWebUsr() {
		super(null, ROLE.ROLE_ANONIM);
		super.setId(SID);
		super.setSid(SID);
		super.setMainRole(ROLE.ANONIM);
		super.setLogin(ROLE.ROLE_ANONIM);
	}


}
