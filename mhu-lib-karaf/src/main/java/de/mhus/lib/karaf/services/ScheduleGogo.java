package de.mhus.lib.karaf.services;

import java.io.ByteArrayInputStream;
import java.util.Observable;
import java.util.Observer;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;

import de.mhus.lib.basics.Named;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.logging.Log.LEVEL;
import de.mhus.lib.core.logging.StreamToLogAdapter;
import de.mhus.lib.core.schedule.CronJob;
import de.mhus.lib.core.util.TimerFactory;
import de.mhus.lib.core.util.TimerIfc;
import de.mhus.lib.karaf.MOsgi;

public class ScheduleGogo extends MLog implements SimpleServiceIfc {
		
	private String interval;
	private String command;
	private TimerIfc timer;
	private Observer job;
	private String name;
	
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
		log().d(name,"get interval",interval);
		doInit();
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setTimerFactory(TimerFactory factory) {
		timer = factory.getTimer();
		log().d(name,"get timer factory");
		doInit();
	}
	
	private void doInit() {
		if (timer == null || interval == null) return;
		if (job != null) return;
		log().d(name,"start",interval);
		job = new MyJob();
		timer.schedule(new CronJob(interval, job));
	}

	protected void doExecute() {
		if (command == null || timer == null) return;
		log().i(name,"execute",command);
		
		try {
		  SessionFactory commandProcessor=MOsgi.getService(SessionFactory.class);
		  
		  ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		  StreamToLogAdapter out = new StreamToLogAdapter(LEVEL.INFO, null);
		  StreamToLogAdapter err = new StreamToLogAdapter(LEVEL.ERROR, null);
		  
		  Session commandSession=commandProcessor.create(in,out,err);						
		  
		  commandSession.put("interactive.mode", false);
		  commandSession.put("APPLICATION",System.getProperty("karaf.name","root"));
		  commandSession.put("USER","karaf");

		  commandSession.execute(command);
		} catch (Throwable t) {
			log().w(name,t);
		}
		
	}
	
	public void init() {
	}

	@Override
	protected void finalize() throws Throwable {
		destroy();
	};
	
	public void destroy() {
		if (timer != null) {
			log().d(name,"deactivate");
			timer.cancel();
		}
		timer = null;
		job = null;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getSimpleServiceInfo() {
		return interval;
	}
	@Override
	public String getSimpleServiceStatus() {
		if (timer == null) return "no timer";
		if (job == null) return "not started";
		return "running";
	}
	@Override
	public void doSimpleServiceCommand(String cmd, Object... param) {
//		if (cmd.equals("restart"))
	}
	
	private class MyJob implements Observer, Named {

		@Override
		public String getName() {
			return "SchedulerGogo:" + name;
		}

		@Override
		public void update(Observable o, Object arg) {
			doExecute();
		}
		
	}
}
