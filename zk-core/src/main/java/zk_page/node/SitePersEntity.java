package zk_page.node;

import mpc.str.sym.SYMJ;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_page.node_state.EntityState;

import java.nio.file.Path;
import java.util.Objects;

public abstract class SitePersEntity<FORMSTATE extends EntityState> {

	public static final Logger L = LoggerFactory.getLogger(SitePersEntity.class);

	//
	//
	protected FORMSTATE formState;

	private final Pare<String, String> sdn;

	public Pare sdn() {
		return sdn;
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
		return formState != null ? formState : (formState = newState());
	}

//	public FORMSTATE stateExt() {

	/// /		Path comState = FormState.ofPathComFile_OrCreate(sdn(),)AFC.getRpaComStatePath(sdn.key(), sdn.val(), UF.fn(siteDirPath), EXT.JSON);
//		throw new UnsupportedOperationException();
//	}
	public abstract FORMSTATE newState();

	//
	// native

	@Override
	public String toString() {
		String head = getClass().getSimpleName() + SYMJ.ARROW_RIGHT_SPEC + sdn().key() + "/" + sdn().val();
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
