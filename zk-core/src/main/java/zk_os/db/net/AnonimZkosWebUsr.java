package zk_os.db.net;


import zk_os.sec.SecMan;

public class AnonimZkosWebUsr extends ZkosWebUsr implements IAnonim {

	public AnonimZkosWebUsr() {
		super.id = 0L;
		super.sid = 0L;
		super.first_name = SecMan.ANONIM;
	}

}
