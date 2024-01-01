package mp.utl_odb.mdl.ext_um2;

import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public abstract class StateUniModel<P extends UuidModel> extends UuidModel<P> {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@DatabaseField
	private Long status;

	@Getter
	@Setter
	@DatabaseField
	private String state, type, types;

	@Override
	public String toString() {
		return "AYModel{" + super.toString() +
				", status=" + status +
				", state='" + state + '\'' +
				", type='" + type + '\'' +
				", types='" + types + '\'' +
				'}';
	}

	public StateUniModel() {
	}

	public StateUniModel(long id) {
		super(id);
	}

	public List<String> getTypesAsList(String del) {
		return getStringAsListBy(getTypes(), del);
	}

}
