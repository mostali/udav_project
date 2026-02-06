package zk_notes.node;

import mpc.str.sym.SYMJ;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_notes.node_state.EntityState;
import zk_notes.node_state.ProxyRW;
import zk_os.core.Sdn;

import java.nio.file.Path;
import java.util.Objects;

public abstract class SitePersEntity<FORMSTATE extends EntityState> {

	public static final Logger L = LoggerFactory.getLogger(SitePersEntity.class);

	//
	//
	protected FORMSTATE formState;

	public ProxyRW.StateProxyRW getProxyRW(boolean... injected) {
		return ProxyRW.StateProxyRW.of(siteDirPath, formState);
	}

	private final Pare<String, String> sdn;

	public Pare<String, String> sdnPare() {
		return sdn;
	}

	public Sdn sdn() {
		return Sdn.of(sdn);
	}

	protected String siteDirPath;
	protected transient Path siteDirPath0;

	public SitePersEntity(Pare sdn) {
		this.sdn = sdn;
	}

	public SitePersEntity(Path siteDir, Pare sdn) {
		this.siteDirPath = siteDir.toString();
		this.siteDirPath0 = siteDir;
		this.sdn = sdn;
	}


	public FORMSTATE state() {
		return formState != null ? formState : (formState = newAfcState());
	}

	protected abstract FORMSTATE newAfcState();

	//
	// native

	@Override
	public String toString() {
		String head = getClass().getSimpleName() + SYMJ.ARROW_RIGHT_SPEC + sdnPare().key() + "/" + sdnPare().val();
		return head;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof SitePersEntity)) {
			return false;
		}
		SitePersEntity siteDir = (SitePersEntity) o;
		return Objects.equals(siteDirPath, siteDir.siteDirPath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(siteDirPath);
	}

}
