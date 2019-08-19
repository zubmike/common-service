package com.github.zubmike.service.tasks;

import java.time.Instant;

public abstract class AbstractTask implements Task {

	private boolean running = false;
	private long lastStartMillis = 0L;
	private long lastFinishMillis = 0L;
	private boolean fail = false;

	@Override
	public void init() {
		setRunning(false);
		setLastStartMillis(0L);
		setLastFinishMillis(0L);
		setFail(false);
	}

	public abstract void execute();

	@Override
	public void run() {
		if (canStart()) {
			setLastStartMillis(getCurrentMillis());
			execute();
			setLastFinishMillis(getCurrentMillis());
		}
	}

	protected boolean canStart() {
		if (!running) {
			setRunning(true);
			return true;
		} else {
			return false;
		}
	}

	private static long getCurrentMillis() {
		return Instant.now().toEpochMilli();
	}

	protected void success() {
		setRunning(false);
		setFail(false);
	}

	protected void fail() {
		setRunning(false);
		setFail(true);
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	protected void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public long getLastStartMillis() {
		return lastStartMillis;
	}

	protected void setLastStartMillis(long lastStartMillis) {
		this.lastStartMillis = lastStartMillis;
	}

	@Override
	public long getLastFinishMillis() {
		return lastFinishMillis;
	}

	protected void setLastFinishMillis(long lastFinishMillis) {
		this.lastFinishMillis = lastFinishMillis;
	}

	@Override
	public boolean isFail() {
		return fail;
	}

	protected void setFail(boolean fail) {
		this.fail = fail;
	}
}
