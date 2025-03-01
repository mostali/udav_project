package mp.utl_odb.mdl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@DatabaseTable
public class IdModel<M extends IdModel> extends AModel<M> {

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

	public IdModel(long id) {
		setId(id);
	}

	public IdModel() {
	}

}
