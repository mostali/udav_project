package mpf;


import mpu.Sys;
import mpc.console.QuestAnswer;
import mpv.sql_morpheus.PgType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassGen {

	public static class EnumGen {
		final static String PATTERN = "public static enum %s { \n %s; \n }";
		final List<String> values;
		final String nameEnum;

		public EnumGen(String nameEnum, List<String> values, String body) {
			this.nameEnum = nameEnum;
			this.values = values == null ? Arrays.asList(body.split(",")) : values;
		}

		public static String build(String name, String body) {
			return new EnumGen(name, null, body).buildEnumPattern().toString();
		}

		public static String build(String name, List<String> values) {
			return new EnumGen(name, values, null).buildEnumPattern().toString();
		}

		public StringBuilder buildEnumPattern() {
			StringBuilder body = new StringBuilder();
			for (String val : values) {
				body.append(val).append(", ");
			}
			body.delete(body.length() - 2, body.length());

			StringBuilder pat = new StringBuilder();
			pat.append(String.format(PATTERN, nameEnum, body));
			return pat;
		}

		public static void main(String[] args) {
			//			Document doc = UJsoup.url2doc_GET_CACHE("https://developer.mozilla.org/en-US/docs/Web/HTML/Element");
			//			Elements els = doc.select("table.standard-table tbody tr td a code");
			//			String temp = els.stream().map(t -> {
			//				String s = t.text();
			//				return US.sbstr(s, 1, 1, true);
			//			}).collect(Collectors.joining(","));
			//			U.exit(ClassGen.EnumGen.build("EHtml5", temp));
		}
	}


	public static class GenBeanClass {

		private boolean isFinal = false;

		public GenBeanClass setIsFinal(boolean isFinal) {
			this.isFinal = isFinal;
			return this;
		}

		public boolean isFinal() {
			return isFinal;
		}

		final String className;
		final List<GenBeanField> fields;

		public static boolean usePostgressJpaType = false;

		public static GenBeanClass of(String mixed) {
			mixed = mixed.trim();
			boolean isFinal = false;
			if (mixed.startsWith("final ")) {
				isFinal = true;
				mixed = mixed.substring(6);
			}
			String[] args = mixed.split("\\s+");
			String className = args[0];
			if (args.length == 1) {
				return new GenBeanClass(className, (List) null).setIsFinal(isFinal);
			}
			String[] fields = new String[args.length - 1];
			System.arraycopy(args, 1, fields, 0, fields.length);
			return new GenBeanClass(className, fields).setIsFinal(isFinal).pattern(mixed);
		}

		String pattern;

		private GenBeanClass pattern(String pattern) {
			this.pattern = pattern;
			return this;
		}

		public GenBeanClass(String className, String... fields) {
			this(className, fromArray(fields));
		}

		private static List<GenBeanField> fromArray(String[] fields) {
			if (fields.length % 2 != 0) {
				throw new IllegalStateException("Fileds length not even");
			}
			List<GenBeanField> fields_ = new ArrayList<>();
			String type = null;
			for (int i = 0; i < fields.length; i++) {
				if (i == 0 || i % 2 == 0) {
					type = fields[i];
				} else {
					fields_.add(GenBeanField.of(type, fields[i]));
				}
			}
			return fields_;
		}

		public GenBeanClass(String className, GenBeanField... fields) {
			this(className, Arrays.asList(fields));
		}

		public GenBeanClass(String className, List<GenBeanField> fields) {
			this.className = className;
			this.fields = fields;
		}

		public void generateAndPrint() {
			StringBuilder sb = generateClass();
			System.out.println(sb);
		}

		StringBuilder sb = null;

		public StringBuilder generateClass() {
			sb = new StringBuilder();
			sb.append("//" + pattern + "\n");
			sb.append("public class ");
			sb.append(className);
			sb.append(" {\n");

			onGenerateFields();
			onGenerateConsructor();
			onGenerateGetterSetters();

			sb.append("}");
			return sb;

		}

		public StringBuilder generateOnlyFieldsAndConstructor() {
			sb = new StringBuilder();
			onGenerateFields();
			onGenerateConsructor();
			return sb;

		}

		private void onGenerateGetterSetters() {
			if (hasFields()) {
				sb.append(builderMethods());
			}
		}

		private void onGenerateConsructor() {
			sb.append(builderConstructor());
		}

		private void onGenerateFields() {
			if (hasFields()) {
				sb.append(builderDeclareFields());
			}
		}


		/**
		 * Declare Class Fileds
		 */
		private StringBuilder builderDeclareFields() {
			StringBuilder sb = new StringBuilder();
			for (GenBeanField f : fields) {
				if (usePostgressJpaType) {
					sb.append("@Column(name = \"").append(f.fieldName).append("\")");
					sb.append("\n");
				}
				sb.append(isFinal() ? f.builderDeclareFinal() : f.builderDeclareSimple());
				sb.append("\n");
			}
			return sb;
		}

		private boolean hasFields() {
			return fields != null && !fields.isEmpty();
		}

		/**
		 * Constructor
		 */
		public StringBuilder builderConstructor() {
			boolean hasFields = hasFields();
			StringBuilder sb = new StringBuilder();
			sb.append("public ");
			sb.append(className);
			sb.append("(");

			if (hasFields) {// fill arguments
				sb.append(builderMethodArguments());
			}
			sb.append("){\n");
			if (hasFields)// fill appropriation
			{
				sb.append(builderDeclareConstructorBody());
			}
			sb.append("}\n");
			return sb;
		}

		/**
		 * Declare Class Methods
		 */
		private StringBuilder builderMethods() {
			StringBuilder sb = new StringBuilder();
			for (GenBeanField f : fields) {
				sb.append(f.builderGetField());
				sb.append("\n");
				if (!isFinal()) {
					sb.append(f.builderSetField());
					sb.append("\n");
				}
			}
			return sb;
		}

		/**
		 * Method Arguments
		 */
		private StringBuilder builderMethodArguments() {
			StringBuilder sb = new StringBuilder();
			if (fields.isEmpty()) {
				return sb;
			}
			for (GenBeanField f : fields) {
				sb.append(f.builderDeclareType());
				sb.append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb;
		}

		/**
		 * Constructor Body
		 */
		private StringBuilder builderDeclareConstructorBody() {
			StringBuilder sb = new StringBuilder();
			for (GenBeanField f : fields) {
				sb.append(f.builderConstructorField());
				sb.append("\n");
			}
			return sb;
		}

		static class GenBeanField {
			final String fieldType;

			final String fieldName;

			public GenBeanField(String fieldType, String fieldName) {
				this.fieldType = fieldType;
				this.fieldName = fieldName;
			}

			public static GenBeanField of(String type, String name) {
				return new GenBeanField(type, name);
			}

			/**
			 * Declare Left Part
			 */
			public StringBuilder builderDeclareSimple() {
				return builderDeclareModifycators(false, false, true);
			}

			public StringBuilder builderDeclareFinal() {
				return builderDeclareModifycators(false, true, true);
			}

			public StringBuilder builderDeclareModifycators(Boolean isModificatorPublic, Boolean isFinal, Boolean isEnd) {
				StringBuilder sb = new StringBuilder();
				if (isModificatorPublic != null) {
					sb.append(isModificatorPublic ? "public " : "private ");
				}
				if (isFinal != null && true == isFinal) {
					sb.append("final ");
				}
				sb.append(builderDeclareType());
				if (isEnd != null) {
					sb.append(";");
				}
				return sb;
			}

			public StringBuilder builderDeclareType() {
				StringBuilder sb = new StringBuilder();
				if (usePostgressJpaType) {
					sb.append(PgType.getJavaTypeString(fieldType));
				} else {
					sb.append(fieldType);
				}
				sb.append(" ");
				sb.append(fieldName);
				return sb;

			}

			/**
			 * Constructor
			 */
			public StringBuilder builderConstructorField() {
				StringBuilder sb = new StringBuilder();
				sb.append("this.");
				sb.append(fieldName);
				sb.append("=");
				sb.append(fieldName);
				sb.append(";");
				return sb;
			}

			/**
			 * Constructor Get Method
			 */
			public StringBuilder builderGetField() {
				StringBuilder sb = new StringBuilder();
				sb.append("public ");
				sb.append(fieldType);
				sb.append(" get");
				sb.append(StringUtils.capitalize(fieldName));
				sb.append("(){return this.");
				sb.append(fieldName);
				sb.append(";}");
				return sb;
			}

			/**
			 * Constructor Set Method
			 */
			public StringBuilder builderSetField() {
				return builderSetField(true);
			}

			public StringBuilder builderSetField(boolean isReturnVoid) {
				StringBuilder sb = new StringBuilder();
				sb.append("public ");
				sb.append(isReturnVoid ? "void" : fieldType);
				sb.append(" set");
				sb.append(StringUtils.capitalize(fieldName));
				sb.append("(");
				sb.append(builderDeclareType());
				sb.append("){");
				sb.append(builderConstructorField());
				sb.append(isReturnVoid ? "}" : " return this;}");
				return sb;
			}
		}
	}

	public static void main(String[] args) {

		if (true) {
			String classGen = QuestAnswer.QUEST("Input Syntetic String to generate Java Class [ ClassName FieldType fieldName ]");
			GenBeanClass classGenBuilder = GenBeanClass.of(classGen);
			classGenBuilder.generateAndPrint();
			if (true) {
				return;
			}
		}

		/**
		 * Create a template string for class with name 'TestBean' and two
		 * fields
		 */
		GenBeanClass.usePostgressJpaType = true;
		GenBeanClass gen = GenBeanClass.of("TestBean bigserial longField VARCHAR stringField");
		Sys.p(gen.generateOnlyFieldsAndConstructor());
		// GenBeanClass gen = GenBeanClass.of("TestBean Long longField String stringField");
		// new GenBeanClass("TestBean", "Long", "longFiled", "String", "stringFiled");
		// gen.generateAndPrint();

		/**
		 * Create a template string for class with name 'TestBeanFinal' and two
		 * final fields
		 */
		//gen = GenBeanClass.of("final TestBeanFinal Long longFiled String stringFiled");
		//new GenBeanClass("TestBeanFinal", "Long", "longFiled", "String",
		//"stringFiled").setIsFinal(true);
		//gen.generateAndPrint();

	}
}
