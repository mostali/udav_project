package zk_notes.control.maintbx.mvelconsole;

import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 1. Create test <br>
 * 2. History Map not synchronized, use(Collections.synchronizedMap) <br>
 *
 * @param <T>
 * @author root
 */
public class MvelTask<T> {
	protected static final int HISTORY_MAX = 30;

	public static final String VARNAME_THIS = "__this__";

	private final String uuid;

	private String code;
	private Serializable compiledCode;
	private Map<String, Object> context;
	private VariableResolverFactory variableResolverFactory;

	private T result;
	private Exception exception;

	private static Map<String, MvelTask<?>> history;

	public MvelTask(String code, Map<String, Object> context) {
		this.code = code;
		this.context = context;
		this.uuid = UUID.randomUUID().toString();
	}

	public static MvelTask<?> getHistoryFirst() {
		return getHistory().values().iterator().next();
	}

	public static Map<String, MvelTask<?>> getHistory() {
		if (history == null) {
			history = new LinkedHashMap(HISTORY_MAX) {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean removeEldestEntry(Map.Entry eldest) {
					return size() > HISTORY_MAX;
				}
			};
		}
		return history;
	}

	public boolean hasException() {
		return this.exception != null;
	}

	public Map<String, Object> getContextInit() {
		if (this.context == null) {
			this.context = new LinkedHashMap<String, Object>();
		}
		return this.context;
	}

	public VariableResolverFactory getContextResult() {
		if (this.variableResolverFactory == null) {
			VariableResolverFactory variableResolverFactory = new MapVariableResolverFactory();
			this.variableResolverFactory = variableResolverFactory;
		}
		return this.variableResolverFactory;
	}

	public Serializable getCompiledCode(String code) {
		if (this.compiledCode == null) {
			Serializable compiledExpression = MVEL.compileExpression(code);
			this.compiledCode = compiledExpression;
		}
		return this.compiledCode;
	}

	public MvelTask<T> exec() {
		Serializable compiledExpression = getCompiledCode(this.code);
		T result = null;
		try {
			result = (T) MVEL.executeExpression(compiledExpression, getContextInit(), getContextResult());
		} catch (Exception ex) {
			this.exception = ex;
		}
		setReturnedResult(result);
		store2history();
		return this;

	}

	public void setReturnedResult(T result) {
		this.result = result;
	}

	public T getReturnedResult() {
		return this.result;
	}

	private void store2history() {
		getHistory().put(this.uuid, this);
	}

	public static Map<String, Object> objectsContext2mapContextSE(Object... arrayContext) {
		try {
			return MvelTask.objectsContext2mapContextTE(arrayContext);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Array with context must be even, and key's must be a String's, error-message :" + e.getMessage());
		}
	}

	/**
	 * Create map context from array with object. <br>
	 * Example, array ["i", 1,"s","2"] will be converted to map =>
	 * [{"i":1},{"s":"2"}] <br>
	 * Every even argument(Object name) must be String, otherwise throw
	 * Exception's:<br>
	 * 1. IllegalStateException - if length of array not even 2.
	 * ClassCastException - if the even argument is not instance of String
	 */
	public static Map<String, Object> objectsContext2mapContextTE(Object... array) throws Exception {
		if (array.length % 2 != 0) {
			throw new IllegalStateException("Fileds length not even");
		}
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String type = null;
		for (int i = 0; i < array.length; i++) {
			if (i == 0 || i % 2 == 0) {
				type = (String) array[i];
			} else {
				map.put(type, array[i]);
			}
		}
		return map;
	}

	// TODO
	public static void main(String[] args) {
		MvelTask.createTask("return 777;").print();
		if (true) {
			return;
		}
		MvelTask task = MvelTask.createTask("int c=a+b;return a1;");

		task.putContextInit("a", 1).putContextInit("b", 2);

		task.exec();

		task.print();

		// task.getContextEnv().getVariableResolver("c").getValue()

	}

	public void print() {
		System.out.println(this);
		if (exception != null) {
			System.err.println(exception);
		} else {
			System.out.println(buildStringPattern("result", String.valueOf(result)));
			System.out.println(buildStringPattern("context", String.valueOf(getContextResult().getKnownVariables())));
		}
	}

	public String buildStringPattern(String name, String text) {
		return ":" + text + "$ " + text;
	}

	@Override
	public String toString() {
		return buildStringPattern(uuid.substring(0, 8), code);
	}

	public MvelTask<T> putContextInit(Map<String, Object> context) {
		if (context == null) {
			return this;
		}
		getContextInit().putAll(context);
		return this;
	}

	public MvelTask<T> putContextInit(String name, Object object) {
		getContextInit().put(name, object);
		return this;
	}

	public static MvelTask createTask(String code) {
		return new MvelTask(code, null);
	}

	public static MvelTask exec_args(String code, Object... mapContext) throws Exception {
		Map<String, Object> objectsContext2mapContext = objectsContext2mapContextTE(mapContext);
		return exec_map(code, objectsContext2mapContext);
	}

	public static <T> MvelTask exec_map(String code, Map<String, Object> context) throws Exception {
		MvelTask task = new MvelTask<T>(code, context);
		return task.exec();
	}

	public Exception getException() {
		return exception;
	}
}
