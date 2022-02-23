package com.github.zubmike.service.dao;

import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemDao<I extends Serializable, T> {

	Optional<T> get(@Null I id);

	List<T> getAll(Collection<I> ids);

	List<T> getAll();

}
