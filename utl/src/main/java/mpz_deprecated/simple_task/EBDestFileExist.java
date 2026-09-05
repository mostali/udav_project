package mpz_deprecated.simple_task;

import mpz_deprecated.EER;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Deprecated
public enum EBDestFileExist {
	SKIP, IGNORE, DELETE, THROW;

	public boolean applyBehaviourIfDestExist_IS_SKIP(String targetFile) throws IOException {
		return applyBehaviourIfDestExist_IS_SKIP(Paths.get(targetFile), this);
	}

	public boolean applyBehaviourIfDestExist_IS_SKIP(Path targetFile) throws IOException {
		return applyBehaviourIfDestExist_IS_SKIP(targetFile, this);
	}

	public static boolean applyBehaviourIfDestExist_IS_SKIP(Path targetFile, EBDestFileExist ebDestFileExist) throws IOException {
		{
			boolean isDestExist = Files.exists(targetFile);
			if (isDestExist) {
				switch (ebDestFileExist) {
					case SKIP:
						return true;
					case IGNORE:
						return false;
					case DELETE:
						Files.delete(targetFile);
						break;
					default:
						throw EER.IS("Dest File is exist ::: " + targetFile);
				}
			}
		}
		return false;
	}
}
