package zk_pages.zznsi_pages.znsi_eiview;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UMdmStand {

	public static List<ConturMdm> getValuesMdm() {
		return Arrays.stream(ConturMdm.values()).filter(mdmStand -> mdmStand.mdm != null).collect(Collectors.toList());
	}

	public static List<ConturMdm> getValuesNifi() {
		return Arrays.stream(ConturMdm.values()).filter(mdmStand -> mdmStand.nifi != null && mdmStand.nifir != null).collect(Collectors.toList());
	}
}
