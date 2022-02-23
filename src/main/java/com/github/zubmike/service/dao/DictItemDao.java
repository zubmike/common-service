package com.github.zubmike.service.dao;

import com.github.zubmike.core.types.DictItem;

import javax.validation.constraints.Null;
import java.io.Serializable;

public interface DictItemDao<I extends Serializable, T extends DictItem<I>> extends ItemDao<I, T> {

	@Null
	String getName(@Null I id);
}
