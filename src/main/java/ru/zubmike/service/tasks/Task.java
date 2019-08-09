package ru.zubmike.service.tasks;

import ru.zubmike.service.conf.TaskProperties;

public interface Task extends Runnable {

	void init();

	boolean isRunning();

	long getLastStartMillis();

	long getLastFinishMillis();

	boolean isFail();

	TaskProperties getProperties();

}
