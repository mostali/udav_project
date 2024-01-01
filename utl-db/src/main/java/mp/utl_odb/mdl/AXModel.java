package mp.utl_odb.mdl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@DatabaseTable
public class AXModel<M extends AXModel> extends AModel<M> {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@DatabaseField(generatedId = true)
	private Long id;

	@Override
	public String toString() {
		return SYM_PARENT + "A{" +
			   "id=" + id +
			   '}';
	}

	public AXModel(long id) {
		setId(id);
	}

	public AXModel() {
	}

}
