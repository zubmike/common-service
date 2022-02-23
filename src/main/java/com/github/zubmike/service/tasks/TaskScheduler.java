package com.github.zubmike.service.tasks;

import com.github.zubmike.core.utils.DateTimeUtils;
import com.github.zubmike.service.conf.PlanTaskProperties;
import com.github.zubmike.service.conf.RepeatTaskProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

public class TaskScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskScheduler.class);

	private static final int DEFAULT_KILLER_INITIAL_DELAY_TIME = 5;
	private static final int DEFAULT_KILLER_PERIOD_TIME = 5;
	private static final TimeUnit DEFAULT_KILLER_TIME_UNIT = TimeUnit.MINUTES;

	private final long maxTaskRuntimeMillis;

	protected final ConcurrentHashMap<Class<?>, ScheduledExecutorService> scheduledExecutorServiceMap = new ConcurrentHashMap<>();
	protected final Map<Class<?>, ScheduledFuture<?>> scheduledFutureMap = new LinkedHashMap<>();
	protected final Map<Class<?>, Task> taskMap = new LinkedHashMap<>();

	public TaskScheduler(long maxTaskRuntimeMillis) {
		this(maxTaskRuntimeMillis, DEFAULT_KILLER_INITIAL_DELAY_TIME, DEFAULT_KILLER_PERIOD_TIME, DEFAULT_KILLER_TIME_UNIT);
	}

	public TaskScheduler(long maxTaskRuntimeMillis, long killerInitialDelay, long killerPeriod, TimeUnit killerTimeUnit) {
		this.maxTaskRuntimeMillis = maxTaskRuntimeMillis;
		addTask(this::checkTasks, killerInitialDelay, killerPeriod, killerTimeUnit);
	}

	private ScheduledFuture<?> addTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
		ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorServiceMap.put(task.getClass(), scheduledExecutorService);
		return scheduledExecutorService.scheduleAtFixedRate(task, initialDelay, period, unit);
	}

	public void register(Task task) {
		switch (task.getProperties().getType()) {
			case REPEAT -> registerRepeatTask(task);
			case PLAN -> registerPlanTask(task);
		}
	}

	public void registerRepeatTask(Task task) {
		RepeatTaskProperties taskProperties = (RepeatTaskProperties) task.getProperties();
		taskMap.put(task.getClass(), task);
		if (taskProperties != null) {
			if (taskProperties.isEnabled()) {
				long period = taskProperties.getPeriod();
				TimeUnit timeUnit = taskProperties.getTimeUnit();
				task.init();
				long initDelaySeconds = taskProperties.isStartAtOnce() ? 0 : getInitDelaySeconds(taskProperties);
				LOGGER.info("enable task: {}, period: {} {}, start in: {} sec", task.getClass().getSimpleName(), period, timeUnit, initDelaySeconds);
				ScheduledFuture<?> scheduledFuture = addTask(task, initDelaySeconds, timeUnit.toSeconds(period), TimeUnit.SECONDS);
				scheduledFutureMap.put(task.getClass(), scheduledFuture);
			} else {
				LOGGER.info("disable task: {}", task.getClass().getSimpleName());
			}
		}
	}

	private static long getInitDelaySeconds(RepeatTaskProperties taskConfiguration) {
		LocalDateTime now = LocalDateTime.now();
		long nowSeconds = DateTimeUtils.getSeconds(now);
		long taskSeconds = taskConfiguration.getTimeUnit().toSeconds(taskConfiguration.getPeriod());
		long nextStartTimeSeconds = DateTimeUtils.getSeconds(now.toLocalDate().atStartOfDay());
		while (nextStartTimeSeconds < nowSeconds) {
			nextStartTimeSeconds += taskSeconds;
		}
		return nextStartTimeSeconds - nowSeconds;
	}

	public void registerPlanTask(Task task) {
		PlanTaskProperties taskProperties = (PlanTaskProperties) task.getProperties();
		taskMap.put(task.getClass(), task);
		if (taskProperties != null) {
			if (taskProperties.isEnabled()) {
				task.init();
				LOGGER.info("enable task: {}, plan: {}:{}", task.getClass().getSimpleName(),
						taskProperties.getHour(),
						taskProperties.getMinute());
				LocalDateTime startDateTime = getNextStartDateTime(taskProperties);
				long initDelaySeconds = DateTimeUtils.getSeconds(startDateTime) - DateTimeUtils.getSeconds(LocalDateTime.now());
				ScheduledFuture<?> scheduledFuture = addTask(task, initDelaySeconds, DateTimeUtils.SECONDS_PER_DAY, TimeUnit.SECONDS);
				scheduledFutureMap.put(task.getClass(), scheduledFuture);
			} else {
				LOGGER.info("disable task: {}", task.getClass().getSimpleName());
			}
		}
	}

	private static LocalDateTime getNextStartDateTime(PlanTaskProperties taskConfiguration) {
		LocalDateTime now = LocalDateTime.now();
		int planHour = taskConfiguration.getHour();
		int planMinute = taskConfiguration.getMinute();
		int nowHour = now.getHour();
		int nowMinute = now.getMinute();
		return nowHour < planHour || nowHour == planHour && nowMinute <= planMinute
				? LocalDateTime.of(now.toLocalDate(), LocalTime.of(planHour, planMinute))
				: LocalDateTime.of(now.plusDays(1).toLocalDate(), LocalTime.of(planHour, planMinute));
	}

	private void checkTasks() {
		try {
			for (Class<?> taskClass : scheduledFutureMap.keySet()) {
				Task task = taskMap.get(taskClass);
				if (task.isRunning()) {
					long runTimeMillis = System.currentTimeMillis() - task.getLastStartMillis();
					if (runTimeMillis >= maxTaskRuntimeMillis) {
						killTask(taskClass);
						LOGGER.warn("re-register task: {}", taskClass.getSimpleName());
						register(task);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("can't kill tasks", e);
		}
	}

	private void killTask(Class<?> taskClass) {
		LOGGER.warn("kill task: {}", taskClass.getSimpleName());
		ScheduledExecutorService scheduledExecutorService = scheduledExecutorServiceMap.get(taskClass);
		scheduledFutureMap.get(taskClass).cancel(true);
		scheduledExecutorService.shutdownNow();
		scheduledFutureMap.remove(taskClass);
		scheduledExecutorServiceMap.remove(taskClass);
	}

	public void destroy() {
		scheduledExecutorServiceMap.values().forEach(ExecutorService::shutdownNow);
	}
}
