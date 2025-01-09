package mp.utl_odb.tree.trees;

import mp.utl_odb.tree.UTree;
import mpu.core.ARG;
import mpu.core.QDate;

public abstract class WalkerTreeMM<M> {

	private final QDate year_i_month;

	protected WalkerTreeMM() {
		this(QDate.now());
	}

	protected WalkerTreeMM(QDate year_i_month) {
		this.year_i_month = year_i_month;
	}

	public M walkAndFind(int iterateCountMonth, M... defRq) {
		QDate year_i_month = this.year_i_month;
		do {
			M m = find(year_i_month, getTree(year_i_month));
			if (m != null) {
				return m;
			}
			year_i_month = year_i_month.addMonth(-1);
		} while (--iterateCountMonth >= 0);

		return ARG.toDefRq(defRq);
	}

	public abstract M find(QDate year_i_month, UTree tree);

	public abstract UTree getTree(QDate date);

}
