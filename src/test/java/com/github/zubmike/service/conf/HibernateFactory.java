package com.github.zubmike.service.conf;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class HibernateFactory {

	public static SessionFactory createSessionFactory(Class<?>... entities) {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:ddl.sql'");
		configuration.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "org.hibernate.context.internal.ThreadLocalSessionContext");
		for (Class<?> entity : entities) {
			configuration.addAnnotatedClass(entity);
		}
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties());
		return configuration.buildSessionFactory(builder.build());
	}

}
