package zk_notes.apiv1;


//Safe characters	Alphanumeric [0-9a-zA-Z], special characters $-_.+!*'() >>>	NO ENCODING
//Reserved characters	; / ? : @ = &	ENCODING*
//Unsafe characters	Includes the blank/empty space and " < > # % { } | \ ^ ~ [ ] `	ENCODING
public class NodeApiChars {

	public static final char UP_CHAR = '*';
	public static final char DOWN_CHAR = '!';
	public static final char COM_CHAR = '$';

	public static final String UP = "*";
	public static final String DOWN = DOWN_CHAR + "";

	public static final String UP_COM = UP + COM_CHAR;
	public static final String DOWN_COM = DOWN + COM_CHAR;

	public static boolean isCallPathStartWithUpDownPart(String callPath) {
		return callPath.startsWith(NodeApiCallType.DOWN.ctrlSymPart_) || callPath.startsWith(NodeApiCallType.UP.ctrlSymPart_);
	}

	public static boolean isCallPathStartWithUpDown(String callPath) {
		return callPath.startsWith(UP) || callPath.startsWith(DOWN);
	}


	//
	//
}
