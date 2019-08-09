package ru.zubmike.service.dao.db;

import org.hibernate.SessionFactory;
import ru.zubmike.core.dao.ItemDao;
import ru.zubmike.core.utils.CollectionUtils;
import ru.zubmike.service.utils.QueryUtils;

import javax.persistence.criteria.*;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BasicItemDao<I extends Serializable, T> extends BasicDao implements ItemDao<I, T> {

	protected final Class<T> clazz;

	public BasicItemDao(SessionFactory sessionFactory, Class<T> clazz) {
		super(sessionFactory);
		this.clazz = clazz;
	}

	@Override
	public Optional<T> get(@Null I id) {
		if (id == null) {
			return Optional.empty();
		}
		return runAndReturn(session ->
				Optional.ofNullable(session.get(clazz, id)));
	}

	@Override
	public List<T> getAll(Collection<I> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		return runAndReturn(session -> {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<T> query = builder.createQuery(clazz);
			Root<T> root = getRootAndSelect(query);
			query.where(QueryUtils.createInPredicate(builder, root.get("id"), ids));
			return session.createQuery(query).list();
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll() {
		return runAndReturn(session ->
				session.createQuery("from " + clazz.getSimpleName()).list());
	}

	protected Root<T> getRootAndSelect(CriteriaQuery<T> query) {
		Root<T> root = query.from(clazz);
		query.select(root);
		return root;
	}

	protected static void setPredicates(AbstractQuery query, Collection<Predicate> predicates) {
		if (CollectionUtils.isNotEmpty(predicates)) {
			query.where(predicates.toArray(new Predicate[0]));
		}
	}

}
