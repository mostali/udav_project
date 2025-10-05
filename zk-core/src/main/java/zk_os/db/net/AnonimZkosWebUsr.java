package zk_os.db.net;


import zk_os.sec.SecApp;

public class AnonimZkosWebUsr extends ZkosWebUsr implements IAnonim {

	public AnonimZkosWebUsr() {
		super.setId(0L);
		super.setSid(0L);
		super.setFirst_name(SecApp.ANONIM);
	}

}
