package com.github.zubmike.service.conf;

import com.github.zubmike.service.tasks.TaskType;

import java.io.Serial;
import java.io.Serializable;

public abstract class TaskProperties implements Serializable {

	@Serial
	private static final long serialVersionUID = 5113984139054510041L;

	private boolean enabled = false;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public abstract TaskType getType();

}
