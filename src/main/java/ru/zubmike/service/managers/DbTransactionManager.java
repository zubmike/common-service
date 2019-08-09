package ru.zubmike.service.managers;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.validation.constraints.Null;
import java.util.function.Supplier;

public class DbTransactionManager implements TransactionManager {

	private final SessionFactory sessionFactory;

	public DbTransactionManager(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void run(Runnable action) {
		Transaction transaction = null;
		try {
			transaction = getTransaction();
			beginTransaction(transaction);
			action.run();
			commitTransaction(transaction);
		} catch (Exception e) {
			rollbackTransaction(transaction);
			throw e;
		}
	}

	@Override
	public <T> T runAndReturn(Supplier<T> action) {
		Transaction transaction = null;
		try {
			transaction = getTransaction();
			beginTransaction(transaction);
			T item = action.get();
			commitTransaction(transaction);
			return item;
		} catch (Exception e) {
			rollbackTransaction(transaction);
			throw e;
		}
	}

	@Null
	private Transaction getTransaction() {
		Transaction transaction = sessionFactory.getCurrentSession().getTransaction();
		return transaction.isActive() ? null : transaction;
	}

	private static void beginTransaction(@Null Transaction transaction) {
		if (transaction != null) {
			transaction.begin();
		}
	}

	private static void commitTransaction(@Null Transaction transaction) {
		if (transaction != null) {
			transaction.commit();
		}
	}

	private static void rollbackTransaction(@Null Transaction transaction) {
		if (transaction != null) {
			transaction.rollback();
		}
	}
}
