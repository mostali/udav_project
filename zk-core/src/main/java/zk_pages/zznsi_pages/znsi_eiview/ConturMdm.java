package zk_pages.zznsi_pages.znsi_eiview;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConturMdm {
	//	DEV(null, "http://fk-eb-ref-dev-mdm-redos.otr.ru:8080", null, null),
	DEV2(null, "http://fk-eb-ref-dev-2-mdm-redos.otr.ru:8080", null, null),
	DEMO("http://fk-eb-ref-dev-mdm2-redos.otr.ru:8081", "http://fk-eb-ref-dev-mdm2-redos.otr.ru:8080", null, null),
	DEMO2(null, "http://fk-eb-ref-dev-mdm-demo2.otr.ru:8080", "http://fk-eb-ref-dev-mdm-demo2:8081/nifi", "http://fk-eb-ref-dev-mdm-demo2:18080/nifi-registry"),
	LOCAL("http://q.com:8080", null, "http://q.com:8070/nifi/", "http://q.com:8069/"), //http://q.com:8069/nifi-registry/
	LOCAL2(null, null, "http://q.com:9070/nifi", "http://q.com:9069"), //http://q.com:9069/nifi-registry/
	;

	public final String xnode, mdm, nifi, nifir;

}
