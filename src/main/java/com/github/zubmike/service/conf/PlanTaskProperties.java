package com.github.zubmike.service.conf;

import com.github.zubmike.service.tasks.TaskType;

public class PlanTaskProperties extends TaskProperties {

	private static final long serialVersionUID = 1L;

	private int hour;
	private int minute;

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	@Override
	public TaskType getType() {
		return TaskType.PLAN;
	}
}
