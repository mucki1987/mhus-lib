package de.mhus.lib.mutable;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import de.mhus.lib.core.MActivator;
import de.mhus.lib.core.MConstants;
import de.mhus.lib.core.MHousekeeper;
import de.mhus.lib.core.activator.DefaultActivator;
import de.mhus.lib.core.lang.Base;
import de.mhus.lib.core.lang.BaseControl;
import de.mhus.lib.core.logging.LogFactory;
import de.mhus.lib.core.system.CfgManager;
import de.mhus.lib.core.system.IApi;
import de.mhus.lib.core.system.IApiInternal;
import de.mhus.lib.core.system.ApiInitialize;
import de.mhus.lib.karaf.services.TimerFactoryImpl;
import de.mhus.lib.logging.JavaLoggerFactory;

/**
 * TODO: Map config to service
 * TODO: Add MActivator with mapper to OSGi Services
 * 
 * @author mikehummel
 *
 */
public class KarafMApiImpl implements IApi, ApiInitialize, IApiInternal {
	
	private LogFactory logFactory;
	private BaseControl baseControl;
	private CfgManager configProvider;
	private boolean fullTrace = false;
	private HashSet<String> logTrace = new HashSet<>();

	private KarafHousekeeper housekeeper;
	
	private File baseDir = new File("data/mhus");
	{
		baseDir.mkdirs();
	}
	
	@Override
	public synchronized BaseControl getBaseControl() {
		if (baseControl == null) {
			baseControl = new BaseControl();
		}
		return baseControl;
	}

	@Override
	public MActivator createActivator() {
		return new DefaultActivator();
	}

	@Override
	public LogFactory getLogFactory() {
		return logFactory;
	}

	@Override
	public synchronized CfgManager getCfgManager() {
		if (configProvider == null) {
			configProvider = new CfgManager(this);
		}
		return configProvider;
	}


	@Override
	public void doInitialize(ClassLoader coreLoader) {
		logFactory = new JavaLoggerFactory();
		
		getCfgManager(); // init
		TimerFactoryImpl.doCheckTimers();
		
		try {
			housekeeper = new KarafHousekeeper();
			getBaseControl().getCurrentBase().addObject(MHousekeeper.class, housekeeper);
		} catch (Throwable t) {
			System.out.println("Can't initialize housekeeper base: " + t);
		}
		
		getCfgManager().reConfigure();

		//logFactory.setLevelMapper(new ThreadBasedMapper() );
	}

	@Override
	public boolean isTrace(String name) {
		return fullTrace || logTrace.contains(name);
	}

	public void setFullTrace(boolean trace) {
		fullTrace = trace;
	}
	
	public void setTrace(String name) {
		logTrace.add(name);
	}
	
	public void clearTrace() {
		logTrace.clear();
	}

	public String[] getTraceNames() {
		return logTrace.toArray(new String[logTrace.size()]);
	}

	public boolean isFullTrace() {
		return fullTrace;
	}

	@Override
	public Base base() {
		return getBaseControl().getCurrentBase();
	}

	@Override
	public void setLogFactory(LogFactory logFactory) {
		this.logFactory = logFactory;
	}

	@Override
	public Set<String> getLogTrace() {
		return logTrace;
	}

	@Override
	public void setBaseDir(File file) {
		baseDir = file;
		baseDir.mkdirs();
	}

	@Override
	public File getFile(String dir) {
		return new File(baseDir, dir);
	}

	@Override
	public String getSystemProperty(String name, String def) {
		String value = System.getProperty(name);
		if (value == null) {
			switch (name) {
			case MConstants.PROP_CONFIG_FILE: return "etc/" + def;
			case MConstants.PROP_TIMER_CONFIG_FILE: return "etc/" + def;
			default:
				return def;
			}
		}
		return value;
	}
	
}
