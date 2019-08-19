package com.github.zubmike.service.managers;

import com.github.zubmike.service.conf.HibernateFactory;
import com.github.zubmike.service.dao.db.BasicDao;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbTransactionManagerTest {

	private static SessionFactory sessionFactory;
	private static DbTransactionManager transactionManager;
	private static TestDao testDao;

	@BeforeClass
	public static void init() {
		sessionFactory = HibernateFactory.createSessionFactory();
		transactionManager = new DbTransactionManager(sessionFactory);
		testDao = new TestDao(sessionFactory);
	}

	@Test
	public void commit() {
		int before = testDao.getCount();
		transactionManager.run(() -> {
			testDao.add("qwerty1");
			testDao.add("qwerty2");
		});
		int after = testDao.getCount();
		Assert.assertEquals(before + 2, after);
	}

	@Test
	public void commitDeep() {
		int before = testDao.getCount();
		transactionManager.run(() -> {
			testDao.add("qwerty1");
			transactionManager.run(() ->
					testDao.add("qwerty2"));
			testDao.add("qwerty3");
		});
		int after = testDao.getCount();
		Assert.assertEquals(before + 3, after);
	}

	@Test
	public void rollback() {
		int before = testDao.getCount();
		try {
			transactionManager.run(() -> {
				testDao.add("qwerty");
				testDao.add("qwerty qwerty qwerty qwerty qwerty qwerty");
			});
		} catch (Exception e) {
			int after = testDao.getCount();
			Assert.assertEquals(before, after);
		}
	}

	@AfterClass
	public static void destroy(){
		sessionFactory.close();
	}

	public static class TestDao extends BasicDao {

		public TestDao(SessionFactory sessionFactory) {
			super(sessionFactory);
		}

		public void add(String value) {
			run(session -> session.doWork(connection -> {
				PreparedStatement statement = connection.prepareStatement("insert into test_values values (?)");
				statement.setString(1, value);
				statement.execute();
			}));
		}

		public int getCount() {
			return runAndReturn(session -> session.doReturningWork(connection -> {
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("select count(*) from test_values");
				int count = 0;
				if (resultSet.next()) {
					count = resultSet.getInt(1);
				}
				return count;
			}));
		}
	}


}
