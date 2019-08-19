package com.github.zubmike.service.dao.db;

import com.github.zubmike.service.utils.DbDataSourceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.validation.constraints.Null;
import java.util.function.Consumer;
import java.util.function.Function;

public class BasicDao {

	private final SessionFactory sessionFactory;

	public BasicDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected <R> R runAndReturn(Function<Session, R> action) {
		return runAndReturn(action, e -> new DbDataSourceException("", e));
	}

	protected <R> R runAndReturn(Function<Session, R> action, Function<Exception, RuntimeException> handle) {
		Transaction transaction = null;
		try {
			Session session = getSession();
			transaction = getTransaction();
			beginTransaction(transaction);
			R result = action.apply(session);
			commitTransaction(transaction);
			return result;
		} catch (Exception e) {
			rollbackTransaction(transaction);
			throw handle.apply(e);
		}
	}

	protected void run(Consumer<Session> action) {
		run(action, e -> new DbDataSourceException("", e));
	}

	protected void run(Consumer<Session> action, Function<Exception, RuntimeException> handle) {
		Transaction transaction = null;
		try {
			Session session = getSession();
			transaction = getTransaction();
			beginTransaction(transaction);
			action.accept(session);
			commitTransaction(transaction);
		} catch (Exception e) {
			rollbackTransaction(transaction);
			throw handle.apply(e);
		}
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Null
	protected Transaction getTransaction() {
		Transaction transaction = getSession().getTransaction();
		var active = transaction.isActive();
		return active ? null : transaction;
	}

	protected static void beginTransaction(@Null Transaction transaction) {
		if (transaction != null) {
			transaction.begin();
		}
	}

	protected static void commitTransaction(@Null Transaction transaction) {
		if (transaction != null) {
			transaction.commit();
		}
	}

	protected static void rollbackTransaction(@Null Transaction transaction) {
		if (transaction != null) {
			transaction.rollback();
		}
	}
}
