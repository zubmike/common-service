package ru.zubmike.service.types;

import ru.zubmike.core.types.TreeDictItem;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BasicEntityTreeDictItem extends BasicEntityDictItem implements TreeDictItem<Integer> {

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
}
