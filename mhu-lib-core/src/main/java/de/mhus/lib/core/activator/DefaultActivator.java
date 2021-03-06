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
package de.mhus.lib.core.activator;

import java.io.Closeable;
import java.util.HashMap;

import de.mhus.lib.basics.ActivatorObjectLifecycle;
import de.mhus.lib.core.MActivator;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.config.IFlatConfig;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.errors.MException;

public class DefaultActivator extends MActivator implements MutableActivator {

	protected HashMap<String, Object> mapper = new HashMap<String, Object>();
	protected HashMap<String, Object> instances = new HashMap<String, Object>();

	public DefaultActivator() {
	}
	
	public DefaultActivator(ClassLoader loader) {
		super(loader);
	}
	
	public DefaultActivator(ResourceNode<?> cactivator, ClassLoader loader) throws MException {
		super(loader);
		if (cactivator != null) {
			if (cactivator instanceof IFlatConfig) {
				for (String key : cactivator.getPropertyKeys())
					addMap(key, cactivator.getExtracted(key));
			} else {
				for (ResourceNode<?> entry : cactivator.getNodes("map"))
					addMap(entry.getExtracted("name",""), entry.getExtracted("class",""));
			}
		}
	}

	@Override
	public void addObject(Class<?> ifc, String name, Object obj) {
		if (ifc == null) {
			setInstance(name, obj);
		} else {
			setInstance(ifc.getCanonicalName() + (name != null ? ":" + name : ""), obj);
		}
	}
	
	@Override
	public void setInstance(String name, Object obj) {
		Object old = null;
		if (obj == null) {
			old = instances.remove(name);
		} else {
			// lifecycle
			if (obj instanceof ActivatorObjectLifecycle) {
				old = instances.get(name);
				try {
					((ActivatorObjectLifecycle)obj).objectActivated(name,old);
				} catch (Throwable t) {
					MApi.dirtyLog(this,name,t);
				}
			}
			old = instances.put(name, obj);
		}
		// lifecycle
		if (old != null && old instanceof ActivatorObjectLifecycle) {
			try {
				((ActivatorObjectLifecycle)obj).objectDeactivated();
			} catch (Throwable t) {
				MApi.dirtyLog(this,name,t);
			}
		}
	}

	public void addMap(String name, String clazz) {
		if (name == null || clazz == null) return;
		//base.log().t("add map",name,clazz);
		mapper.put(name, clazz);
	}
	
	@Override
	public void addMap(Class<?> ifc,String name, Class<?> clazz) {
		addMap(ifc.getCanonicalName() + ":" + name, clazz);
	}
	
	@Override
	public void addMap(String name, Class<?> clazz) {
		if (name == null || clazz == null) return;
		//base.log().t("add map",name,clazz);
		mapper.put(name, clazz);
	}

	@Override
	public Object mapName(String name) {
		if (name == null) return name;
		if (mapper.containsKey(name))
			return mapper.get(name);
		return name;
	}

	@Override
	public boolean isInstance(String ifc) {
		return instances.containsKey(ifc);
	}

	@Override
	public void destroy() {
		super.destroy();
		for (Object obj : instances.values() )
			if (obj instanceof Closeable) {
				try {
					((Closeable)obj).close();
				} catch (Throwable t) {}
			}
		mapper = null;
	}

	@Override
	protected Object getInstance(String name) {
		return instances.get(name);
	}

	@Override
	public void addMap(Class<?> ifc, Class<?> clazz) {
		addMap(ifc.getCanonicalName(), clazz);
	}

	@Override
	public void removeMap(String name) {
		mapper.remove(name);
	}

	@Override
	public void removeObject(String name) {
		removeObject(null,name);
	}

	@Override
	public boolean removeObject(Class<?> ifc, String name) {
		if (ifc == null) {
			return instances.remove(name) != null;
		} else {
			return instances.remove(ifc.getCanonicalName() + (name != null ? ":" + name : "")) != null;
		}
	}
	
	@Override
	public String[] getMapNames() {
		return mapper.keySet().toArray(new String[mapper.size()]);
	}
	
	@Override
	public String[] getObjectNames() {
		return instances.keySet().toArray(new String[mapper.size()]);
	}

}
