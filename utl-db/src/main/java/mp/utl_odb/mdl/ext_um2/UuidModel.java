package mp.utl_odb.mdl.ext_um2;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mp.utl_odb.mdl.AModel;

import java.util.UUID;

@DatabaseTable
public class UuidModel<M extends UuidModel> extends AModel<M> {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@DatabaseField(generatedId = true)
	private Long id;

	@Getter
	@Setter
	@DatabaseField
	private Long uid, uidl;

	@Getter
	@Setter
	@DatabaseField
	private String guid;

	public M setRandomGuid() {
		setGuid(UUID.randomUUID().toString());
		return (M) this;
	}

	@Override
	public String toString() {
		return "UuidModel{" +
			   "id=" + id +
			   ", uid=" + uid +
			   ", uidl=" + uidl +
			   ", guid='" + guid + '\'' +
			   '}';
	}

	public UuidModel(long id) {
		setId(id);
	}

	public UuidModel() {
	}

}
