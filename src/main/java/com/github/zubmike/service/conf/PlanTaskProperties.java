package com.github.zubmike.service.conf;

import com.github.zubmike.service.tasks.TaskType;

import java.io.Serial;

public class PlanTaskProperties extends TaskProperties {

	@Serial
	private static final long serialVersionUID = 3044625498666949932L;

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
