package ru.zubmike.service.dao.db;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.zubmike.core.utils.InvalidParameterException;
import ru.zubmike.service.conf.HibernateFactory;
import ru.zubmike.service.types.TestDictItem;

import java.util.Arrays;
import java.util.Optional;

public class BasicEntityItemDaoTest {

	private static SessionFactory sessionFactory;
	private static BasicEntityItemDao<Integer, TestDictItem> testDictItemDao;

	@BeforeClass
	public static void init() {
		sessionFactory = HibernateFactory.createSessionFactory(TestDictItem.class);
		testDictItemDao = new BasicEntityItemDao<>(sessionFactory, TestDictItem.class);
	}

	@Test
	public void getValueFromNativeQuery() {
		String result = testDictItemDao.runAndReturn(session ->
				(String) session.createNativeQuery("select 'qwerty' from dual").uniqueResult());
		Assert.assertNotNull("qwerty", result);
	}

	@Test
	public void addItem() {
		TestDictItem testItem = new TestDictItem();
		testItem.setName("Test");
		int id = testDictItemDao.add(testItem);
		Optional<TestDictItem> savedItem = testDictItemDao.get(id);

		Assert.assertTrue(savedItem.isPresent());
		Assert.assertEquals(testItem.getName(), savedItem.get().getName());
	}

	@Test(expected = InvalidParameterException.class)
	public void addInvalidItem() {
		TestDictItem testItem = new TestDictItem();
		testItem.setName("Test Test Test Test Test Test Test Test Test");
		testDictItemDao.add(testItem);
	}

	@Test
	public void updateItem() {
		TestDictItem testItem = new TestDictItem();
		int id = testDictItemDao.add(testItem);
		TestDictItem createdItem = testDictItemDao.get(id).orElseThrow();
		createdItem.setName("Test");
		testDictItemDao.update(createdItem);
		TestDictItem updateItem = testDictItemDao.get(id).orElseThrow();
		Assert.assertEquals(createdItem, updateItem);
	}

	@Test
	public void removeItem() {
		TestDictItem testItem = new TestDictItem();
		int id = testDictItemDao.add(testItem);
		testDictItemDao.remove(id);
		Assert.assertTrue(testDictItemDao.get(id).isEmpty());
	}

	@Test
	public void getItems() {
		testDictItemDao.addAll(Arrays.asList(new TestDictItem(), new TestDictItem(), new TestDictItem()));
		Assert.assertFalse(testDictItemDao.getAll().isEmpty());
	}

	@AfterClass
	public static void destroy(){
		sessionFactory.close();
	}

}
