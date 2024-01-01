package mpf.contract;

import java.io.Serializable;
import java.util.Map;

public interface IContract extends Serializable {
	Map<String, Object> mapc();//return clean map with properties

	Map<String, Object> mapdb();//return original map


}
