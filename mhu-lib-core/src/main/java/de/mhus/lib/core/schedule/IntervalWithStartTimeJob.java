package de.mhus.lib.core.schedule;

import java.util.Observer;

import de.mhus.lib.core.MDate;

/**
 * Executes the task every 'interval' milliseconds after execution finished.
 * @author mikehummel
 *
 */
public class IntervalWithStartTimeJob extends SchedulerJob implements MutableSchedulerJob {

	private long interval;
	private long start;

	public IntervalWithStartTimeJob(long start, long interval, Observer task) {
		super(task);
		this.start = start;
		this.interval = interval;
	}

	@Override
	public void doCaclulateNextExecution() {
		if (isDone())
			setNextExecutionTime(System.currentTimeMillis() + interval);
		else
			setNextExecutionTime(start);
	}

	@Override
	public String toString() {
		return 
			IntervalWithStartTimeJob.class.getSimpleName() + "," + MDate.toIsoDateTime(start) + "," + interval;
	}

	@Override
	public void doReschedule(Scheduler queue, long time) {
		super.doReschedule(queue, time);
	}

	@Override
	public void setDone(boolean done) {
		super.setDone(done);
	}

}