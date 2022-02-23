package com.github.zubmike.service.dao;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

public interface EntityItemDao<I extends Serializable, T> extends ItemDao<I, T> {

	I add(@NotNull T item);

	void addAll(Collection<T> items);

	void update(@NotNull T item);

	void updateAll(Collection<T> items);

	void remove(@NotNull I id);

	void remove(@NotNull T item);

	void removeAll();
}
