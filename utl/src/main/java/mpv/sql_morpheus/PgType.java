package mpv.sql_morpheus;

import mpu.core.ENUM;
import mpu.str.STR;

import static java.sql.Types.*;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

/**
 * Google:java get postgres types
 * https://www.postgresql.org/message-id/AANLkTikkkxN+-UUiGVTzj8jdfS4PdpB8_tDONMFHNqHk@mail.gmail.com
 * <p>
 * 1 data_type_id Data Type Id java.lang.Integer int4 11
 * 2 smallint_type Smallint Type java.lang.Integer int2 6
 * 3 int_type Int Type java.lang.Integer int4 11
 * 4 bigint_type Bigint Type java.lang.Long int8 20
 * 5 decimal_type Decimal Type java.math.BigDecimal numeric 18
 * 6 numeric_type Numeric Type java.math.BigDecimal numeric 12
 * 7 real_type Real Type java.lang.Float float4 14
 * 8 doubleprecision_type Doubleprecision Type java.lang.Double float8 24
 * 9 serial_type Serial Type java.lang.Integer int4 11
 * 10 bigserial_type Bigserial Type java.lang.Long int8 20
 * 11 varchar_type Varchar Type java.lang.String varchar 30
 * 12 char_type Char Type java.lang.String bpchar 30
 * 13 text_type Text Type java.lang.String text 2147483647
 * 14 bytea_type Bytea Type [B bytea 2147483647
 * 15 date_type Date Type java.sql.Date date 13
 * 16 time_type Time Type java.sql.Time time 15
 * 17 timetz_type Timetz Type java.sql.Time timetz 21
 * 18 timestamp_type Timestamp Type java.sql.Timestamp timestamp 29
 * 19 timestamptz_type Timestamptz Type java.sql.Timestamp timestamptz 35
 * 20 interval_type Interval Type org.postgresql.util.PGInterval interval 49
 * 21 boolean_type Boolean Type java.lang.Boolean bool 1
 * <p>
 * <p>
 * <p>
 * 22 point_type Point Type org.postgresql.geometric.PGpoint point 2147483647 23
 * linesegment_type Linesegment Type org.postgresql.geometric.PGlseg lseg
 * 2147483647
 * <p>
 * <p>
 * <p>
 * 24 box_type Box Type org.postgresql.geometric.PGbox box 2147483647
 * 25 path_type Path Type org.postgresql.geometric.PGpath path 2147483647
 * <p>
 * <p>
 * <p>
 * 26 polygon_type Polygon Type org.postgresql.geometric.PGpolygon polygon
 * 2147483647 27 circle_type Circle Type org.postgresql.geometric.PGcircle
 * circle 2147483647
 * <p>
 * <p>
 * <p>
 * 28 cidr_type Cidr Type java.lang.Object cidr 2147483647
 * 29 inet_type Inet Type java.lang.Object inet 2147483647
 * 30 macaddr_type Macaddr Type java.lang.Object macaddr 2147483647
 * 31 bit2_type Bit2 Type java.lang.Boolean bit 2
 * 32 bitvarying5_type Bitvarying5 Type java.lang.Object varbit 5
 * <p>
 * ================================================================================
 * https://www.instaclustr.com/blog/postgresql-data-types-mappings-to-sql-jdbc-and-java-data-types/
 * ==========================EXAMPLES 2 ============================================
 * PostgreSQL Data Type	| SQL/JDBC Data Type | Java Type
 * bool	BIT	boolean
 * bit	BIT	boolean
 * int8	BIGINT	long
 * bigserial	BIGINT	long
 * oid	BIGINT	long
 * bytea	BINARY	byte[]
 * char	CHAR	String
 * bpchar	CHAR	String
 * numeric	NUMERIC	java.math.BigDecimal
 * int4	INTEGER	int
 * serial	INTEGER	int
 * int2	SMALLINT	short
 * smallserial	SMALLINT	short
 * float4	REAL	float
 * float8	DOUBLE	double
 * money	DOUBLE	double
 * name	VARCHAR	String
 * text	VARCHAR	String
 * varchar	VARCHAR	String
 * date	DATE	java.sql.Date
 * time	TIME	java.sql.Time
 * timetz	TIME	java.sql.Time
 * timestamp	TIMESTAMP	java.sql.Timestamp
 * timestamptz	TIMESTAMP	java.sql.Timestamp
 * cardinal_number	DISTINCT	Mapping of underlying type
 * character_data	DISTINCT	Mapping of underlying type
 * sql_identifier	DISTINCT	Mapping of underlying type
 * time_stamp	DISTINCT	Mapping of underlying type
 * yes_or_no	DISTINCT	Mapping of underlying type
 * xml	SQLXML	java.sql.SQLXML
 * refcursor	REF_CURSOR	Undefined
 * _abc	ARRAY	java.sql.array
 */
public enum PgType {
	bool(BIT, boolean.class),//
	bit(BIT, boolean.class),//
	int8(BIGINT, Long.class),//
	bigserial(BIGINT, long.class),//
	oid(BIGINT, long.class),//
	bytea(BINARY, byte[].class),//
	_char_(CHAR, String.class),//
	bpchar(CHAR, String.class),//
	numeric(NUMERIC, java.math.BigDecimal.class),//
	int4(INTEGER, int.class),//
	serial(INTEGER, int.class),//
	int2(SMALLINT, short.class),//
	smallserial(SMALLINT, short.class),//
	float4(REAL, float.class),//
	float8(DOUBLE, double.class),//
	money(DOUBLE, double.class),//
	name(VARCHAR, String.class),//
	text(VARCHAR, String.class),//
	varchar(VARCHAR, String.class),//
	date(DATE, java.sql.Date.class),//
	time(TIME, java.sql.Time.class),//
	timetz(TIME, java.sql.Time.class),//
	timestamp(TIMESTAMP, java.time.LocalDateTime.class),//java.sql.Timestamp.class
	timestamptz(TIMESTAMP, java.time.LocalDateTime.class),//java.sql.Timestamp.class
	xml(SQLXML, java.sql.SQLXML.class),//

	//	refcursor	REF_CURSOR	Undefined
	_abc(ARRAY, java.sql.Array.class),

	uuid(BINARY, UUID.class),//What is Types.????
	json(BINARY, Map.class),//What is Types.????
	bigint(BINARY, BigInteger .class);//What is Types.????

//	cardinal_number	DISTINCT	Mapping of underlying type
//	character_data	DISTINCT	Mapping of underlying type
//	sql_identifier	DISTINCT	Mapping of underlying type
//	time_stamp	DISTINCT	Mapping of underlying type
//	yes_or_no	DISTINCT	Mapping of underlying type;
//
	//OLD
//	BIT(Types.BIT, Boolean.class),
//	BOOLEAN(Types.BOOLEAN, Boolean.class),
//	INTEGER(Types.INTEGER, Integer.class),
//	TINYINT(Types.TINYINT, Integer.class),
//	SMALLINT(Types.SMALLINT, Integer.class),
//	BIGINT(Types.BIGINT, Long.class),
//	DOUBLE(Types.DOUBLE, Double.class),
//	NUMERIC(Types.NUMERIC, Double.class),
//	DECIMAL(Types.DECIMAL, Double.class),
//	FLOAT(Types.FLOAT, Double.class),
//	REAL(Types.REAL, Double.class),
//	NVARCHAR(Types.NVARCHAR, String.class),
//	CHAR(Types.CHAR, String.class),
//	VARCHAR(Types.VARCHAR, String.class),
//	CLOB(Types.CLOB, String.class),
//	DATE(Types.DATE, LocalDate.class),
//	TIME(Types.TIME, LocalTime.class),
//	DATETIME(Types.TIMESTAMP, LocalDateTime.class);

	private final int typeCode;
	private final Class<?> typeClass;


	/**
	 * Constructor
	 *
	 * @param typeClass the type class
	 */
	PgType(int typeCode, Class<?> typeClass) {
		this.typeCode = typeCode;
		this.typeClass = typeClass;
	}

	public static String getJavaTypeString(String fieldType) {
		String clazz = ENUM.valueOf(fieldType, PgType.class, true).typeClass.getSimpleName();
		if (Character.isUpperCase(clazz.charAt(0))) {
			return clazz;
		} else {
			switch (clazz) {
				case "int":
					return "Integer";

				default:
					return STR.capitalize(clazz);
			}
		}
	}

	/**
	 * Returns the class this type maps to
	 *
	 * @return the class this type maps to
	 */
	public Class<?> typeClass() {
		return typeClass;
	}

	/**
	 * Returns the SQL type code for this type
	 *
	 * @return the SQL type code
	 * @see Types
	 */
	public int getTypeCode() {
		return typeCode;
	}


	/**
	 * Returns the type resolver for the platform specified
	 *
	 * @param platform the SQL platform code
	 * @return the type resolver
	 */
//	public static TypeResolver getTypeResolver(SQLPlatform platform) {
//		switch (platform) {
//			case SQLITE:
//				return new SqliteTypeResolver();
//			default:
//				return new DefaultTypeResolver();
//		}
//	}

//	public static PgType of(String name) {
//		PgType type = EN.valueOf(name, PgType.class, null);
//		if (type != null) {
//			return type;
//		}
//		switch (name) {
//			case "TEXT":
//				return PgType.VARCHAR;
//			default:
//				throw new WhatIsTypeException(name);
//		}
//	}


	/**
	 * An interface to a tyoe resolver given a sql code and tyoe name
	 */
	public interface TypeResolver {

		/**
		 * Returns the type given the JDBC type code and type name
		 *
		 * @param sqlCode  the jdbc type code from java.sql.Types
		 * @param typeName the type name
		 * @return the matching Type
		 */
		PgType getType(int sqlCode, String typeName);
	}


	/**
	 * The default TypeResolver implementation
	 */
//	private static class DefaultTypeResolver implements TypeResolver {
//		@Override
//		public PgType getType(int sqlCode, String typeName) {
//			switch (sqlCode) {
//				case Types.BIT:
//					return PgType.BIT;
//				case Types.BOOLEAN:
//					return PgType.BOOLEAN;
//				case Types.INTEGER:
//					return PgType.INTEGER;
//				case Types.TINYINT:
//					return PgType.TINYINT;
//				case Types.SMALLINT:
//					return PgType.SMALLINT;
//				case Types.BIGINT:
//					return PgType.BIGINT;
//				case Types.DOUBLE:
//					return PgType.DOUBLE;
//				case Types.NUMERIC:
//					return PgType.NUMERIC;
//				case Types.DECIMAL:
//					return PgType.DECIMAL;
//				case Types.FLOAT:
//					return PgType.FLOAT;
//				case Types.REAL:
//					return PgType.REAL;
//				case Types.NVARCHAR:
//					return PgType.NVARCHAR;
//				case Types.CHAR:
//					return PgType.CHAR;
//				case Types.VARCHAR:
//					return PgType.VARCHAR;
//				case Types.CLOB:
//					return PgType.CLOB;
//				case Types.DATE:
//					return PgType.DATE;
//				case Types.TIME:
//					return PgType.TIME;
//				case Types.TIMESTAMP:
//					return PgType.DATETIME;
//				default:
//					throw new RuntimeException("Unsupported data type for " + typeName + ", sqlType: " + sqlCode);
//			}
//		}
//	}
//
//
//	/**
//	 * A SQLITE specific TypeResolver implementation that deals with type affinity issues
//	 */
//	private static class SqliteTypeResolver implements TypeResolver {
//
//		private static final Map<String, PgType> typeMap = new HashMap<>();
//		private static TypeResolver defaultResolver = new DefaultTypeResolver();
//
//		/**
//		 * Static initializer
//		 */
//		static {
//			typeMap.put("BIT", PgType.BIT);
//			typeMap.put("BOOLEAN", PgType.BOOLEAN);
//			typeMap.put("TINYINT", PgType.TINYINT);
//			typeMap.put("SMALLINT", PgType.SMALLINT);
//			typeMap.put("INTEGER", PgType.INTEGER);
//			typeMap.put("BIGINT", PgType.BIGINT);
//			typeMap.put("FLOAT", PgType.FLOAT);
//			typeMap.put("REAL", PgType.REAL);
//			typeMap.put("NUMERIC", PgType.NUMERIC);
//			typeMap.put("DOUBLE", PgType.DOUBLE);
//			typeMap.put("DECIMAL", PgType.DECIMAL);
//			typeMap.put("CHAR", PgType.CHAR);
//			typeMap.put("VARCHAR", PgType.VARCHAR);
//			typeMap.put("DATE", PgType.DATE);
//			typeMap.put("TIME", PgType.TIME);
//			typeMap.put("DATETIME", PgType.DATETIME);
//			typeMap.put("TIMESTAMP", PgType.DATETIME);
//		}
//
//
//		@Override
//		public PgType getType(int sqlCode, String typeName) {
//			for (String token : typeName.toUpperCase().split("\\s+")) {
//				final PgType type = typeMap.get(token);
//				if (type != null) {
//					return type;
//				}
//			}
//			return defaultResolver.getType(sqlCode, typeName);
//		}
//	}

}
