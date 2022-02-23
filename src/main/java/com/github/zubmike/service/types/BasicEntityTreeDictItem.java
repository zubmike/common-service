package com.github.zubmike.service.types;

import com.github.zubmike.core.types.TreeDictItem;

import javax.persistence.MappedSuperclass;
import java.io.Serial;
import java.util.Objects;

@MappedSuperclass
public class BasicEntityTreeDictItem extends BasicEntityDictItem implements TreeDictItem<Integer> {

	@Serial
	private static final long serialVersionUID = 5857138216244567944L;

	private Integer parentId;

	public BasicEntityTreeDictItem() {
	}

	public BasicEntityTreeDictItem(int id, String name) {
		super(id, name);
	}

	public BasicEntityTreeDictItem(int id, String name, Integer parentId) {
		super(id, name);
		this.parentId = parentId;
	}

	@Override
	public Integer getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BasicEntityTreeDictItem that)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		return Objects.equals(parentId, that.parentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parentId);
	}

	@Override
	public String toString() {
		return super.toString() + " " + parentId;
	}
}
