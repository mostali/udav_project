package mp.utl_odb.mdl.ext_um2;

import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.Setter;
import mpc.exception.NI;
import mpu.core.QDate;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.typedb.TypeDb;

import java.util.List;

public abstract class UniModel<P extends StateUuidModel> extends StateUuidModel<P> {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@DatabaseField
	private String key, name, value, values, ext, data;

	@Getter
	@Setter
	@DatabaseField
	private java.sql.Date date;

	public QDate getQDate() {
		return QDate.of(date);
	}

	@Getter
	@Setter
	@DatabaseField
	private long time;

	public UniModel<P> setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String toString() {
		return "UniModel{" + super.toString() +
				", key='" + key + '\'' +
				", name='" + name + '\'' +
				", value='" + value + '\'' +
				", values='" + values + '\'' +
				", ext='" + ext + '\'' +
				", data='" + data + '\'' +
				", date='" + date + '\'' +
				", time='" + time + '\'' +
				'}';
	}

	public UniModel() {
	}

	public UniModel(long id) {
		super(id);
	}

	public String getValues() {
		return values;
	}

	public String getValues(String defValues) {
		String vals = getValues();
		return vals == null ? defValues : vals;
	}

	public List<String> getValuesAsList(String del) {
		return getStringAsListBy(getValues(), del);
	}

	@Deprecated
	public TypeDb getTypeDb() {
		throw new NI("set db");
	}

	@Deprecated
	public <M extends UniModel> List<M> getModels(QP... qps) {
		return getTypeDb().getModels(qps);
	}
}
