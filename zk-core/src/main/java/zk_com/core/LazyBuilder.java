package zk_com.core;

import org.zkoss.zk.ui.Component;

import java.io.Serializable;

/**
 * @author dav 08.01.2022   14:56
 */
public interface LazyBuilder<C extends Component> extends Serializable {
	void buildAndAppend(C host) throws Exception;
}
