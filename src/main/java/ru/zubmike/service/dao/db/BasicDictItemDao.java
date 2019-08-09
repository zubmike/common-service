package ru.zubmike.service.dao.db;

import org.hibernate.SessionFactory;
import ru.zubmike.core.dao.DictItemDao;
import ru.zubmike.core.types.DictItem;

public class BasicDictItemDao<T extends DictItem<Integer>> extends BasicItemDao<Integer, T> implements DictItemDao<Integer, T> {

	public BasicDictItemDao(SessionFactory sessionFactory, Class<T> clazz) {
		super(sessionFactory, clazz);
	}

	@Override
	public String getName(Integer id) {
		return get(id).map(DictItem::getName).orElse(null);
	}

}
