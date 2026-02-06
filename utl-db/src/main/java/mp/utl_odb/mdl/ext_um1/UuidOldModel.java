package mp.utl_odb.mdl.ext_um1;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mp.utl_odb.mdl.TimedIdModel;

@DatabaseTable
public class UuidOldModel<M extends UuidOldModel> extends TimedIdModel<M> {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@DatabaseField
	private Long uid;
	@Getter
	@Setter
	@DatabaseField
	private String guid;

	@Override
	public String toString() {
		return "AXDU2{" +
			   "uid=" + uid +
			   ", guid='" + guid + '\'' +
			   ", >>>" + super.toString() +
			   '}';
	}

	public UuidOldModel(long id) {
		setId(id);
	}

	public UuidOldModel() {
	}

}
