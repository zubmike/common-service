package com.github.zubmike.service.utils;

import com.github.zubmike.core.utils.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QueryUtils {

	private static final int MAX_IN_CLAUSE_VALUES = 1000;

	public static <T> Query<T> createPaginationQuery(Session session, CriteriaQuery<T> criteriaQuery, Integer page,
	                                                 Integer limit, Order... orders) {
		return createPaginationQuery(session, criteriaQuery, page, limit, Arrays.asList(orders));
	}

	public static void setPredicates(CriteriaQuery<?> query, Collection<Predicate> predicates) {
		if (CollectionUtils.isEmpty(predicates)) {
			query.where(predicates.toArray(new Predicate[0]));
		}
	}

	public static <T> Query<T> createPaginationQuery(Session session, CriteriaQuery<T> criteriaQuery, Integer page,
	                                                 Integer limit, List<Order> orders) {
		if (page != null && limit != null) {
			criteriaQuery.orderBy(orders);
			Query<T> query = session.createQuery(criteriaQuery);
			query.setFirstResult((page - 1) * limit);
			query.setMaxResults(limit);
			return query;
		}
		return session.createQuery(criteriaQuery);
	}

	public static <T> void setPagination(NativeQuery<T> query, Integer page, Integer limit) {
		if (page != null && limit != null) {
			query.setFirstResult((page - 1) * limit).setMaxResults(limit);
		}
	}

	public static <T> Predicate createInPredicate(CriteriaBuilder builder, Path<T> path, Collection<T> items) {
		var predicates = new ArrayList<Predicate>();
		var itemList = new ArrayList<T>();
		int pages = items.size() / MAX_IN_CLAUSE_VALUES;
		for (int i = 0; i <= pages; i++) {
			List<T> pageItems = CollectionUtils.getPageItems(itemList, i + 1, MAX_IN_CLAUSE_VALUES);
			if (!pageItems.isEmpty()) {
				predicates.add(path.in(pageItems));
			}
		}
		return builder.or(predicates.toArray(new Predicate[0]));
	}

	public static Predicate createLikePredicate(CriteriaBuilder builder, Expression<String> path, String value) {
		return builder.like(builder.lower(path), '%' + value.toLowerCase() + '%');
	}

	public static Predicate createDatePredicates(CriteriaBuilder builder, Path<LocalDateTime> date,
	                                             LocalDateTime from, LocalDateTime to) {
		if (from != null && to != null) {
			return builder.between(date, from, to);
		} else if (from != null) {
			return builder.greaterThanOrEqualTo(date, from);
		} else if (to != null) {
			return builder.lessThanOrEqualTo(date, to);
		}
		throw new IllegalArgumentException("dates is null");
	}

	public static String createNativeQueryPartInClause(String attributeName, String paramName, int collectionSize) {
		var parts = new ArrayList<String>();
		int clauseCount = getInClauseCount(collectionSize);
		for (int i = 0; i <= clauseCount; i++) {
			parts.add(attributeName + " in " + "(:" +paramName + i + ")");
		}
		return " (" + String.join(" or ", parts) + ")";
	}

	private static int getInClauseCount(int collectionSize) {
		return collectionSize % MAX_IN_CLAUSE_VALUES != 0
				? collectionSize / MAX_IN_CLAUSE_VALUES
				: collectionSize / MAX_IN_CLAUSE_VALUES - 1;
	}

	public static <T> void setParameterList(NativeQuery<?> query, String paramName, Collection<T> items) {
		var itemList = items instanceof List ? (List<T>) items : new ArrayList<>(items);
		int clauseCount = getInClauseCount(items.size());
		for (int i = 0; i <= clauseCount; i++) {
			query.setParameterList(paramName + i, CollectionUtils.getPageItems(itemList, i + 1, MAX_IN_CLAUSE_VALUES));
		}
	}

}
