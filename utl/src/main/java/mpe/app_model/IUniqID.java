package mpe.app_model;

import lombok.RequiredArgsConstructor;

public interface IUniqID {
	Long getUid();

	Long getOid();

	@RequiredArgsConstructor
	class Simple {
		final Long uid, oid;
	}
}
