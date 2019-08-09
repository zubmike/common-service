package ru.zubmike.service.managers;

import java.util.function.Supplier;

public interface TransactionManager {

	void run(Runnable action);

	<T> T runAndReturn(Supplier<T> action);

}
