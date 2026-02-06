package mpc.str.sym;


import mpu.core.ARG;
import mpc.rfl.RFL;
import mpu.str.STR;

public class SYM {
	//
	public static final String EMPTY = "";

	public static final String NEWLINE = System.getProperty("line.separator");

	public static final char TAB_ = '\t';
	public static final String TAB = String.valueOf(TAB_);

	//
	public static final char SIMPLE_SPACE_ = ' ';
	public static final String WHITESPACE = String.valueOf(SIMPLE_SPACE_);
	//
	public static final char DOLLAR_ = '$';
	public static final String DOLLAR = String.valueOf(DOLLAR_);
	//
	public static final char SPACE_ = ' ';
	public static final String SPACE = String.valueOf(SPACE_);
	//
	public static final char UNDER_ = '_';
	public static final String UNDER = String.valueOf(UNDER_);
	//
	public static final char DOT_ = '.';
	public static final String DOT = String.valueOf(DOT_);
	//
	public static final char COMMA_ = ',';
	public static final String COMMA = String.valueOf(COMMA_);
	//
	public static final char EXMARK_ = '!';
	public static final String EXMARK = String.valueOf(EXMARK_);
	//
	public static final char QSTMARK_ = '?';
	public static final String QSTMARK = String.valueOf(QSTMARK_);

	//
	public static final char DASH_ = '-';
	public static final String DASH = String.valueOf(DASH_);
	//
	public static final char COLON_ = ':';
	public static final String COLON = String.valueOf(COLON_);
	//
	public static final char SCOLON_ = ';';
	public static final String SCOLON = String.valueOf(SCOLON_);
	//
	public static final char EQ_ = '=';
	public static final String EQ = String.valueOf(EQ_);
	//
	public static final char DQ_ = '"';
	public static final String DQ = String.valueOf(DQ_);
	//
	public static final String SQ = "'";
	public static final char SQ_ = SQ.charAt(0);
	//
	public static final char RQ_ = '`';
	public static final String RQ = String.valueOf(RQ_);
	//
	public static final char DOG_ = '@';
	public static final String DOG = String.valueOf(DOG_);
	//
	public static final char HOUSE_ = '^';
	public static final String HOUSE = String.valueOf(HOUSE_);
	//
	public static final char STAR_ = '*';
	public static final String STAR = String.valueOf(STAR_);

	//
	public static final char GT_ = '>';
	public static final String GT = String.valueOf(GT_);
	//
	public static final char LT_ = '<';
	public static final String LT = String.valueOf(LT_);

	//
	public static final String WORD_EN = STR.ALPHABETIC_FULL + UNDER_;
	public static final String WORD_EN_NUM = STR.ALPHABETIC_FULL__NUM + UNDER_;
	public static final String WORD_EN_NUM_DASH = WORD_EN_NUM + DASH_;

	public static final String VOID = String.valueOf(Character.MIN_VALUE);
	public static final String VOID65533 = String.valueOf('�');
	public static final String DASH_LONG = "—";
	public static final String DASH_MIDDLE = "–";

	public static Character getCharByFieldName(String fname, Character... character) {
		try {
			return (char) RFL.fieldValueSt(SYM.class, fname.endsWith("_") ? fname : fname + "_", true);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, character);
		}
	}


	//


}