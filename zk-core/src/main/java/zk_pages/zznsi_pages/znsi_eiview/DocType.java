package zk_pages.zznsi_pages.znsi_eiview;

public enum DocType {
	REF_EGRIP_20, REF_IPandKFH, REF_OKATOtoOKTMO, REF_OKTMO, REF_ProvidersZHKH,//
	REF_OKVED,
	REF_TypeKS,
	;

	public String toUrlPartRegisterOrDict() {
		switch (this) {
			case REF_IPandKFH:
				return "register";
			default:
				return "lookup";
		}
	}

}
