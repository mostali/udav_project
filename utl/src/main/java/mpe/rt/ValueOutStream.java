package mpe.rt;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.core.ARG;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.function.Function;

@RequiredArgsConstructor
public class ValueOutStream<T> extends ByteArrayOutputStream {

	final Function<byte[], T> mapper;

	public ValueOutStream() {
		this(bytes -> (T) new String(bytes));
	}

	private T val;

	@SneakyThrows
	public static ValueOutStream of(InputStream content) {
		ValueOutStream<Object> output = new ValueOutStream<>();
		IOUtils.copy(content, output);
		try{
			output.getValue();
			return output;
		}finally {
			IOUtils.closeQuietly(content, output);
		}
	}

	@SneakyThrows
	public static String toString(InputStream errorStream) {
		return IOUtils.toString(errorStream);
	}

	public T getValue(boolean... fresh) {
		if (val == null || ARG.isDefEqTrue(fresh)) {
			val = mapper.apply(buf);
		}
		return val;
	}

}
