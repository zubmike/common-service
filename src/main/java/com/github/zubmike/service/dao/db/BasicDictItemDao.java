package com.github.zubmike.service.dao.db;

import com.github.zubmike.service.dao.DictItemDao;
import com.github.zubmike.core.types.DictItem;
import org.hibernate.SessionFactory;

public class BasicDictItemDao<T extends DictItem<Integer>> extends BasicItemDao<Integer, T> implements DictItemDao<Integer, T> {

	public BasicDictItemDao(SessionFactory sessionFactory, Class<T> clazz) {
		super(sessionFactory, clazz);
	}

	@Override
	public String getName(Integer id) {
		return get(id).map(DictItem::getName).orElse(null);
	}

}
