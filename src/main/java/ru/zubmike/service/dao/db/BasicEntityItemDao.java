package ru.zubmike.service.dao.db;

import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import ru.zubmike.core.dao.EntityItemDao;
import ru.zubmike.core.utils.InvalidParameterException;
import ru.zubmike.service.utils.DbDataSourceException;
import ru.zubmike.service.utils.DuplicateException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Collection;

public class BasicEntityItemDao <I extends Serializable, T> extends BasicItemDao<I, T> implements EntityItemDao<I, T> {

	public BasicEntityItemDao(SessionFactory sessionFactory, Class<T> clazz) {
		super(sessionFactory, clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public I add(T item) {
		return runAndReturn(session -> (I) session.save(item),
				e -> createException(e, "can't add item", item.toString()));
	}

	@Override
	public void addAll(Collection<T> items) {
		run(session -> {
			for (T item : items) {
				session.save(item);
			}
		}, e -> createException(e, "can't add items", null));
	}

	@Override
	public void update(T item) {
		run(session -> session.saveOrUpdate(item),
				e -> createException(e, "can't update item", item.toString()));
	}

	@Override
	public void updateAll(Collection<T> items) {
		run(session -> {
			for (T item : items) {
				session.saveOrUpdate(item);
			}
		}, e -> createException(e, "can't update items", ""));
	}

	@Override
	public void remove(T item) {
		run(session -> session.delete(item),
				e -> createException(e, "can't remove item", item.toString()));
	}

	@Override
	public void remove(I id) {
		run(session ->{
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaDelete<T> query = builder.createCriteriaDelete(clazz);
			Root<T> root = query.from(clazz);
			query.where(builder.equal(root.get("id"), id));
			session.createQuery(query).executeUpdate();
		}, e -> createException(e, "can't remove item by id", id.toString()));
	}

	@Override
	public void removeAll() {
		run(session -> session.createQuery("delete from " + clazz.getSimpleName()),
				e -> createException(e, "can't remove all items", ""));
	}

	private static RuntimeException createException(Exception e, String message, String itemDetail) {
		if (e instanceof DataException) {
			return new InvalidParameterException();
		} else if (e instanceof ConstraintViolationException) {
			return createConstraintException(e, itemDetail);
		} if (e.getCause() instanceof ConstraintViolationException) {
			return createConstraintException((ConstraintViolationException) e.getCause(), itemDetail);
		} else {
			return new DbDataSourceException(message + " " + itemDetail, e);
		}
	}

	private static RuntimeException createConstraintException(Exception e, String itemDetail) {
		return isDuplicateException((ConstraintViolationException) e)
				? new DuplicateException("duplicate item " + itemDetail, e)
				: new InvalidParameterException();
	}

	protected static boolean isDuplicateException(ConstraintViolationException e) {
		String sqlState = e.getSQLState();
		return sqlState.equals("23000") && e.getErrorCode() == 1 // Oracle
				|| sqlState.equals("23505") // PostgreSQL
				|| e.getErrorCode() == 1062; // MySql
	}
}
