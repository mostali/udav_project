package mpe.str.simple_props;


import mpe.str.ARGS;

@Deprecated
public class SplitCommand {

	private String[] command;
	public final String original;
	protected final String delimetr;
	private final boolean isTrimArgs;

	public SplitCommand(String command) {
		this(command, "\\s+", false);
	}

	public SplitCommand(String command, String delimetr, boolean isTrimArgs) {
		this.delimetr = delimetr;
		this.isTrimArgs = isTrimArgs;
		this.original = command;
	}

	public String[] two() {
		return two(delimetr);
	}

	public String[] two(String delimetr) {
		return original.split(delimetr, 2);
	}

	public static String normWhiteSpace(String cmd) {
		cmd = cmd.trim().replaceAll("\\s++", " ");
		return cmd;
	}

	public String[] command() {
		if (command == null) {
			this.command = original.split(delimetr);
			if (isTrimArgs) {
				for (int i = 0; i < this.command.length; i++) {
					this.command[i] = this.command[i].trim();
				}
			}
		}
		return command;
	}

	public String paramAsString(int index) {
		return ARGS.argsAsStr(command(), index);
	}

	public Integer paramAsInt(int index) {
		return paramAsInt(index, null);
	}

	public Integer paramAsInt(int index, Integer def) {
		return ARGS.argsAsInt(command(), index, def);
	}

	public Double paramAsDouble(int index, Double def) {
		return ARGS.argsAsDbl(command(), index, def);
	}

}