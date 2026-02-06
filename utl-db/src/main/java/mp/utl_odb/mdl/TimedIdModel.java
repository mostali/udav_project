package mp.utl_odb.mdl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mpu.core.QDate;

import java.sql.Date;

@DatabaseTable
public class TimedIdModel<M extends TimedIdModel> extends IdModel<M> {

	private static final long serialVersionUID = 1L;

	@Getter
	@DatabaseField
	private Date dt;

	@Getter
	@Setter
	@DatabaseField
	private String dtru;

	@Override
	public String toString() {
		return SYM_PARENT + "AXD{" +
				"dt=" + dt +
				", dtru='" + dtru + '\'' +
				", " + super.toString() +
				'}';
	}

	public TimedIdModel(long id) {
		setId(id);
	}

	public TimedIdModel() {
	}

	public void setDateNow() {
		setDt(QDate.now());
	}

	public void setDt(Date dt) {
		this.dt = dt;
		this.dtru = QDate.of(dt).f(QDate.F.MONO20NF);
	}

	public void setDt(QDate date) {
		this.dt = date.toSqlDate();
		this.dtru = date.f(QDate.F.MONO20NF);
	}


	public QDate getQDt() {
		return QDate.of(getDt());
	}
}
