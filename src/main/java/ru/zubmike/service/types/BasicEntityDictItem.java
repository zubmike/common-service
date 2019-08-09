package ru.zubmike.service.types;

import ru.zubmike.core.types.DictItem;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
public class BasicEntityDictItem implements DictItem<Integer> {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "name")
	private String name;

	public BasicEntityDictItem() {
	}

	public BasicEntityDictItem(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BasicEntityDictItem)) {
			return false;
		}
		BasicEntityDictItem that = (BasicEntityDictItem) o;
		return id == that.id && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		return id + " " + name;
	}

}
