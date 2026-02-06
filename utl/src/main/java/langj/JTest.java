package langj;

import mpu.core.EQ;
import mpu.Sys;
import mpu.IT;
import mpc.types.abstype.AbsType;
import mpc.rfl.RFL;
import mpu.str.STR;
import mpu.core.QDate;

import java.util.Objects;

public class JTest {

	QDate now;
	static final JTest child = new JTest(10001);

	public JTest() {
		now = QDate.now();
	}

	public JTest(byte ms) {
		now = QDate.ofEpoch((int) ms);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		JTest jTest = (JTest) o;
		return Objects.equals(now, jTest.now);
	}

	@Override
	public int hashCode() {
		return Objects.hash(now);
	}

	public JTest(int ms) {
		now = QDate.ofEpoch(ms);
	}

//	public JTest(long ms) {
//		now = QDate.ofEpoch((int) (long) ms);
//	}

	public void simple_method_print() {
		Sys.p("simple_method_print");
	}

	public int simple_method_get() {
		return 7;
	}

	public QDate simple_method_get_qdate() {
		return now;
	}

	public JTest simple_method_get_this() {
		return this;
	}

	public static void main(String[] args) {

		do_all_success_test();
//		test8_METHOD_SIMPLE_PRINT_GET_CHILD_FIELD();
	}

	public static void do_all_success_test() {
		ECode.L.info("Go");
		Sys.p("1. test0_CONSTRUCTOR_SIMPLE:" + test1_CONSTRUCTOR_SIMPLE());
		Sys.p("2. test0_CONSTRUCTOR_10001:" + test2_CONSTRUCTOR_10001());
		Sys.p("3. test0_METHOD_SIMPLE:" + test3_METHOD_SIMPLE());
		Sys.p("4. test0_METHOD_SIMPLE_PRINT_VOID:" + test4_METHOD_SIMPLE_PRINT_VOID());
		Sys.p("5. test0`_METHOD_SIMPLE_PRINT_GET:" + test5_METHOD_SIMPLE_PRINT_GET());
		Sys.p("6. test0_METHOD_SIMPLE_PRINT_GET_QDATE:" + test6_METHOD_SIMPLE_PRINT_GET_QDATE());
		Sys.p("7. test0_METHOD_SIMPLE_PRINT_GET_FIELD:" + test7_METHOD_SIMPLE_PRINT_GET_FIELD());
		Sys.p("8. test8_METHOD_SIMPLE_PRINT_GET_CHILD_FIELD:" + test8_METHOD_SIMPLE_PRINT_GET_CHILD_FIELD());
		Sys.p("9. test9_SIMPLE_SYSTEM:" + test9_SIMPLE_SYSTEM());
	}

	public static Host test9_SIMPLE_SYSTEM() {
		long ms = System.currentTimeMillis();
		String code = "System.currentTimeMillis()";
		printCode(code);
		Host eval = Host.eval(code);
		IT.state(eval.isLong());
		return eval;
	}

	public static Host test8_METHOD_SIMPLE_PRINT_GET_CHILD_FIELD() {
		QDate date=new langj.JTest(10001).simple_method_get_this().child.now;
		String code = "new langj.JTest(10001).simple_method_get_this().child.now";
		printCode(code);
		Host eval = Host.eval(code);
		JTest jTest = new JTest(10001);
		IT.isTrue(jTest.now.equals(eval.val()));
		return eval;
	}

	public static Host test7_METHOD_SIMPLE_PRINT_GET_FIELD() {
		String code = "new langj.JTest(10001).simple_method_get_this().now";
		printCode(code);
		Host eval = Host.eval(code);
		IT.isTrue(new JTest(10001).now.equals(eval.val()));
		return eval;
	}

	public static AbsType test6_METHOD_SIMPLE_PRINT_GET_QDATE() {
		String code = "new langj.JTest(10001).simple_method_get_qdate()";
		printCode(code);
		AbsType eval = Host.eval(code);
		IT.isTrue(new JTest(10001).now.equals(eval.val()));
		return eval;
	}

	public static AbsType test5_METHOD_SIMPLE_PRINT_GET() {
		String code = "new langj.JTest(10001).simple_method_get()";
		printCode(code);
		AbsType eval = Host.eval(code);
		IT.isTrue(EQ.eq(eval.val(), 7));
		return eval;
	}

	public static AbsType test4_METHOD_SIMPLE_PRINT_VOID() {
		String code = "new langj.JTest(10001).simple_method_print()";
		printCode(code);
		AbsType eval = Host.eval(code);
		IT.isTrue(RFL.isVoid(eval.type()));
		return eval;
	}

	public static Object test3_METHOD_SIMPLE() {
		String code = "new langj.JTest(10001).toString()";
		printCode(code);
		Object value = Host.eval(code).val();
		IT.isTrue(value.toString().startsWith("JTest::"));
		return value;
	}

	public static Object test2_CONSTRUCTOR_10001() {
		String code = "new langj.JTest(10001)";
		printCode(code);
		Object value = Host.eval(code).val();
		IT.isTrue(value.equals(new JTest(10001)));
		return value;
	}

	public static Object test1_CONSTRUCTOR_SIMPLE() {
		String code = "new langj.JTest()";
		printCode(code);
		Object value = Host.eval(code).val();
		IT.isTrue(value.toString().startsWith("JTest::"));
		return value;
	}

	private static void printCode(String code) {
		Sys.p(STR.repeat("*", code.length() + 2));
		Sys.p(" " + code + " ");
		Sys.p(STR.repeat("*", code.length()+ 2) );
	}

	@Override
	public String toString() {
		return "JTest::" + now;
	}
}
