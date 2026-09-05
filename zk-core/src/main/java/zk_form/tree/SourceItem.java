package zk_form.tree;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SourceItem implements ISourceItem {

	private final @Getter String code, name;

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("code", code)
				.add("name", name)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null || !SourceItem.class.isAssignableFrom(o.getClass())) {
			return false;
		}
		SourceItem that = (SourceItem) o;
		return Objects.equal(this.code, that.code) &&
				Objects.equal(this.name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(code, name);
	}
}
