package mpe.str.simple_props;

import java.util.List;

@Deprecated
public interface IIProps {
	List<String> keys();

	String getValue(String key) throws SimpleProps.SimplePropNotFoundException;

	String toStringProps() throws SimpleProps.SimplePropNotFoundException;

	List<String> getValues(String key) throws SimpleProps.SimplePropNotFoundException;
}
