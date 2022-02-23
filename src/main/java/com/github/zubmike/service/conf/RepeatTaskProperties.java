package com.github.zubmike.service.conf;

import com.github.zubmike.service.tasks.TaskType;

import java.io.Serial;
import java.util.concurrent.TimeUnit;

public class RepeatTaskProperties extends TaskProperties {

	@Serial
	private static final long serialVersionUID = 8104999229211456897L;

	private boolean startAtOnce = false;
	private long period;
	private TimeUnit timeUnit;

	public boolean isStartAtOnce() {
		return startAtOnce;
	}

	public void setStartAtOnce(boolean startAtOnce) {
		this.startAtOnce = startAtOnce;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	@Override
	public TaskType getType() {
		return TaskType.REPEAT;
	}
}
