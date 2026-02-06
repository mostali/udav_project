package mp.utl_odb.netapp.mdl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mp.utl_odb.mdl.TimedIdModel;
import org.jetbrains.annotations.NotNull;

@DatabaseTable
public class NetSrcModel<M extends TimedIdModel> extends TimedIdModel<M> {

	private static final long serialVersionUID = 1L;

	/**
	 * Name
	 */
	@Setter
	@Getter
	@DatabaseField
	protected String nm;

	/**
	 * System Id (SourceId)
	 */
	@Getter
	@Setter
	@DatabaseField(uniqueIndex = true, throwIfNull = true)
	protected long sid;

	/**
	 * Net Id
	 */
	@Setter
	@Getter
	@DatabaseField(canBeNull = false)
	@NotNull
	protected String nt;

	/**
	 * Native Id
	 */
	@Getter
	@Setter
	@DatabaseField(canBeNull = false)
	@NotNull
	protected String nid;

	public NetSrcModel(long id) {
		setId(id);
	}

	public NetSrcModel() {
	}

	@Override
	public String toString() {
		return SYM_PARENT + "NetSrcModel{" +
				"nm='" + nm + '\'' +
				", sid=" + sid +
				", nt=" + nt +
				", nid='" + nid + '\'' +
				", '" + super.toString() +
				'}';
	}
}
