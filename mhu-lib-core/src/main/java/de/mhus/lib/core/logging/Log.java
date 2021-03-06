/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.logging;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;

/**
 * Got the interface from apache-commons-logging. I need to switch because its not working
 * in eclipse plugins correctly.
 * 
 * @author mikehummel
 *
 */
public class Log {

	public enum LEVEL {TRACE,DEBUG,INFO,WARN,ERROR,FATAL};

	protected boolean localTrace = true;
	private static boolean stacktraceTrace = false;
	protected String name;
	protected static LevelMapper levelMapper;
    protected static ParameterMapper parameterMapper;
    protected LogEngine engine = null;
//    protected UUID id = UUID.randomUUID();
	protected static int maxMsgSize = 0;

	public Log(Object owner) {
		
		String name = null;
		if (owner == null) {
			name = "?";
		} else
		if (owner instanceof Class) {
			name = ((Class<?>)owner).getName();
		} else {
			name = String.valueOf(owner);
			if (name == null) 
				name = owner.getClass().getCanonicalName();
//			else {
//				int p = name.indexOf('@');
//				if (p > 0) name = name.substring(0,p);
//			}
		}
		this.name = name;
		localTrace = MApi.isTrace(name);
		
		update();
		
//		register();
	}

//    protected void register() {
//		MApi.registerLogger(this);
//	}
//    
//    protected void unregister() {
//		MApi.unregisterLogger(this);
//    }


    // -------------------------------------------------------- Logging Methods
    
    /**
     * Log a message in trace, it will automatically append the objects if trace is enabled. Can Also add a trace.
     * This is the local trace method. The trace will only written if the local trace is switched on.
     * @param msg 
     */
    public void t(Object ... msg) {
    	log(LEVEL.TRACE, msg);
    }

    public void log(LEVEL level, Object ... msg) {
		if (engine == null) return;

    	if (levelMapper != null) level = levelMapper.map(this, level,msg);
    	
    	switch (level) {
		case DEBUG:
			if (!engine.isDebugEnabled()) return;
			break;
		case ERROR:
			if (!engine.isErrorEnabled()) return;
			break;
		case FATAL:
			if (!engine.isFatalEnabled()) return;
			break;
		case INFO:
			if (!engine.isInfoEnabled()) return;
			break;
		case TRACE:
			if (!engine.isTraceEnabled()) return;
			break;
		case WARN:
			if (!engine.isWarnEnabled()) return;
			break;
		default:
			return;
    	}

    	if (parameterMapper != null) msg = parameterMapper.map(this, msg);
    	
    	StringBuilder sb = new StringBuilder();
    	prepare(sb);
    	Throwable error = MString.serialize(sb, msg,maxMsgSize);
    	
    	switch (level) {
		case DEBUG:
			engine.debug(sb.toString(),error);
			break;
		case ERROR:
			engine.error(sb.toString(),error);
			break;
		case FATAL:
			engine.fatal(sb.toString(),error);
			break;
		case INFO:
			engine.info(sb.toString(),error);
			break;
		case TRACE:
			engine.trace(sb.toString(),error);
			break;
		case WARN:
			engine.warn(sb.toString(),error);
			break;
		default:
			break;
    	}
    	
    	if (stacktraceTrace) {
    		String stacktrace = MCast.toString("stacktracetrace",Thread.currentThread().getStackTrace());
    		engine.debug(stacktrace);
    	}
    	
	}

//	/**
//     * Log a message in trace, it will automatically append the objects if trace is enabled. Can Also add a trace.
//     */
//    public void tt(Object ... msg) {
//    	if (!isTraceEnabled()) return;
//    	StringBuilder sb = new StringBuilder();
//    	prepare(sb);
//    	Throwable error = null;
////    	int cnt=0;
//    	for (Object o : msg) {
//			error = serialize(sb,o, error);
////    		cnt++;
//    	}
//    	trace(sb.toString(),error);
//    }

    /**
     * Log a message in debug, it will automatically append the objects if debug is enabled. Can Also add a trace.
     * @param msg 
     */
    public void d(Object ... msg) {
    	log(LEVEL.DEBUG, msg);
    }

    /**
     * Log a message in info, it will automatically append the objects if debug is enabled. Can Also add a trace.
     * @param msg 
     */
    public void i(Object ... msg) {
    	log(LEVEL.INFO, msg);
    }
    
    /**
     * Log a message in warn, it will automatically append the objects if debug is enabled. Can Also add a trace.
     * @param msg 
     */
    public void w(Object ... msg) {
    	log(LEVEL.WARN, msg);
    }

    /**
     * Log a message in error, it will automatically append the objects if debug is enabled. Can Also add a trace.
     * @param msg 
     */
    public void e(Object ... msg) {
    	log(LEVEL.ERROR, msg);
    }

    /**
     * Log a message in info, it will automatically append the objects if debug is enabled. Can Also add a trace.
     * @param msg 
     */
    public void f(Object ... msg) {
    	log(LEVEL.FATAL, msg);
    }

    protected void prepare(StringBuilder sb) {
    	if (levelMapper != null) {
    		levelMapper.prepareMessage(this,sb);
    	} else {
    		sb.append('[').append(Thread.currentThread().getId()).append(']');
    	}
	}

	public void setLocalTrace(boolean localTrace) {
		this.localTrace = localTrace;
	}

	public boolean isLocalTrace() {
		return localTrace;
	}

//	public static Log getLog() {
//		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
//		// for (StackTraceElement e : stack) System.out.println(e.getClassName());
//		return getLog(stack[2].getClassName());
//	}


	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return MSystem.toString(this, getName());
	}
	
	public static Log getLog(Object owner) {
		return MApi.get().lookupLog(owner);
	}

	public void update() {
		engine = MApi.get().getLogFactory().getInstance(getName());
		localTrace = MApi.isTrace(name);
		levelMapper = MApi.get().getLogFactory().getLevelMapper();
		parameterMapper = MApi.get().getLogFactory().getParameterMapper();
		maxMsgSize = MApi.get().getLogFactory().getMaxMessageSize();
	}

	public ParameterMapper getParameterMapper() {
		return parameterMapper;
	}
	
	/**
	 * Return if the given level is enabled. This function also uses the
	 * levelMapper to find the return value. Instead of the is...Enabled().
	 * 
	 * @param level
	 * @return true if level is enabled
	 */
	public boolean isLevelEnabled(LEVEL level) {
		if (engine == null) return false;

		if (localTrace)
			level = LEVEL.INFO;
		else
		if (levelMapper != null) 
			level = levelMapper.map(this, level);
    	
    	switch (level) {
		case DEBUG:
			return engine.isDebugEnabled();
		case ERROR:
			return engine.isErrorEnabled();
		case FATAL:
			return engine.isFatalEnabled();
		case INFO:
			return engine.isInfoEnabled();
		case TRACE:
			return engine.isTraceEnabled();
		case WARN:
			return engine.isWarnEnabled();
		default:
			return false;
    	}

	}
	
	public void close() {
		if (engine == null) return;
//		unregister();
		engine.close();
		engine = null;
	}

	public static boolean isStacktraceTrace() {
		return stacktraceTrace;
	}

	public static void setStacktraceTrace(boolean stacktraceTrace) {
		Log.stacktraceTrace = stacktraceTrace;
	}
	
//	public UUID getId() {
//		return id;
//	}
	
}
