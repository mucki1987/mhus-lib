package de.mhus.lib.karaf;

import java.util.Timer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import de.mhus.lib.core.util.TimerFactory;
import de.mhus.lib.core.util.TimerIfc;
import de.mhus.lib.core.util.TimerImpl;
import de.mhus.lib.errors.NotFoundException;

public class MOsgi {
	
	private static Timer localTimer; // fallback timer

	public static <T> T getService(Class<T> ifc) {
		BundleContext context = FrameworkUtil.getBundle(ifc).getBundleContext();
		if (context == null) throw new NotFoundException("service context not found", ifc);
		ServiceReference<T> ref = context.getServiceReference(ifc);
		if (ref == null) throw new NotFoundException("service reference not found", ifc);
		T obj = context.getService(ref);
		if (obj == null) throw new NotFoundException("service not found", ifc);
		return obj;
	}

	public static synchronized TimerIfc getTimer() {
		TimerIfc timer = null;
		try {
			timer = getService(TimerFactory.class).getTimer();
		} catch (Throwable t) {}
		if (timer == null) {
			// oh oh
			if (localTimer == null)
				localTimer = new Timer("de.mhu.lib.localtimer",true);
			timer = new TimerImpl( localTimer );
		}
		return timer;
	}
	
}
