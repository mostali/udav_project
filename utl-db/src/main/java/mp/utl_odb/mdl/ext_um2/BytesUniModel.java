package mp.utl_odb.mdl.ext_um2;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.Setter;
import mpu.X;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BytesUniModel<M extends UniModel> extends UniModel<M> {
	public BytesUniModel(long id) {
		super(id);
	}

	public BytesUniModel() {
		super();
	}

	@Getter
	@Setter
	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	private byte[] bytes;

	public BytesUniModel setBytesFromFile(Path path) {
		try {
			setBytes(Files.readAllBytes(path));
		} catch (IOException e) {
			X.throwException(e);
		}
		return this;
	}
}
