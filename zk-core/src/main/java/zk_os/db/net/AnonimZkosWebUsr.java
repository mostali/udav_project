package zk_os.db.net;


import zk_os.sec.SecMan;

public class AnonimZkosWebUsr extends ZkosWebUsr implements IAnonim {

	public AnonimZkosWebUsr() {
		super.setId(0L);
		super.setSid(0L);
		super.setFirst_name(SecMan.ANONIM);
	}

}
