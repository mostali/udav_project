package mp.utl_odb.mdl.ext_um1;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@DatabaseTable
public class UniOldModel<M extends UuidOldModel> extends UuidOldModel<M> {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@DatabaseField
	private String name, key, val, ext, data;

	@Override
	public String toString() {
		return cn() + "{" +
			   "name='" + name + '\'' +
			   ", key='" + key + '\'' +
			   ", val='" + val + '\'' +
			   ", ext='" + ext + '\'' +
			   ", data='" + data + '\'' +
			   ", ?'" + super.toString() + '\'' +
			   '}';
	}

	public UniOldModel(long id) {
		setId(id);
	}

	public UniOldModel() {
	}

}
