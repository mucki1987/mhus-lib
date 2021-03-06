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
package de.mhus.lib.adb.transaction;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.mhus.lib.adb.DbManager;
import de.mhus.lib.adb.DbObject;
import de.mhus.lib.adb.Persistable;
import de.mhus.lib.adb.model.Field;
import de.mhus.lib.adb.model.Table;
import de.mhus.lib.errors.NotSupportedException;
import de.mhus.lib.errors.TimeoutRuntimeException;

/**
 * <p>TransactionLock class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class TransactionLock extends LockBase {

	private Persistable[] objects;
	private DbManager manager;
	private boolean locked;
	private TreeMap<String, Persistable> orderedKeys;

	/**
	 * <p>Constructor for TransactionLock.</p>
	 *
	 * @param manager a {@link de.mhus.lib.adb.DbManager} object.
	 * @param objects a {@link de.mhus.lib.adb.Persistable} object.
	 */
	public TransactionLock(DbManager manager, Persistable ... objects) {
		this.manager = manager;
		this.objects = objects;
	}
	
	/**
	 * <p>Constructor for TransactionLock.</p>
	 *
	 * @param objects a {@link de.mhus.lib.adb.Persistable} object.
	 */
	public TransactionLock(Persistable ... objects) {
		this.objects = objects;
		manager = null;
		for (Persistable o : objects)
			if (o instanceof DbObject) {
				manager = (DbManager) ((DbObject)o).getDbHandler();
				break;
			}
	}

	/** {@inheritDoc} */
	@Override
	public void lock(long timeout)  throws TimeoutRuntimeException {
		if (objects == null) throw new NotSupportedException("Transaction already gone");
		if (manager == null) throw new NotSupportedException("DbManager not found, need direct manager or DbObject implementation to grep the manager");
		LockStrategy strategy = manager.getSchema().getLockStrategy();
		if (strategy == null) return;

		getLockKeys();
		
		long start = System.currentTimeMillis();
		for (Map.Entry<String, Persistable> entry : orderedKeys.entrySet()) {
			try {
				strategy.lock(entry.getValue(), entry.getKey(), this, timeout);
			} catch (Throwable t) {log().d(t);}
			if (System.currentTimeMillis() - start > timeout) {
				for (Map.Entry<String, Persistable> entry2 : orderedKeys.entrySet()) {
					try {
						strategy.releaseLock(entry2.getValue(), entry2.getKey(), this);
					} catch (Throwable t) {log().d(t);}
				}
				throw new TimeoutRuntimeException();
			}
				
		}
		
		locked = true;
	}

	/**
	 * <p>createKey.</p>
	 *
	 * @param o a {@link de.mhus.lib.adb.Persistable} object.
	 * @return a {@link java.lang.String} object.
	 */
	protected String createKey(Persistable o) {
		// find db manager of the object, fallback is my manager
		DbManager m = manager;
		if (o instanceof DbObject) m = (DbManager) ((DbObject)o).getDbHandler();
		
		String regName = m.getRegistryName(o);
		Table table = m.getTable(regName);
		StringBuilder key = new StringBuilder().append(regName);
		for (Field pKey : table.getPrimaryKeys()) {
			String value = "";
			try {
				value = String.valueOf(pKey.get(o));
			} catch (Exception e) {log().d(e);}
			key.append(",").append(value);
		}
		return key.toString();
	}

	/** {@inheritDoc} */
	@Override
	public void release() {
		if (!locked || objects == null || manager == null) return;
		
		LockStrategy strategy = manager.getSchema().getLockStrategy();
		if (strategy == null) return;
		
		for (Persistable o : objects) {
			String key = createKey(o);
			try {
				strategy.releaseLock(o, key, this);
			} catch (Throwable t) {log().d(t);}
		}
		
		manager = null;
		objects = null;
		locked = false;
		
	}
	
	/** {@inheritDoc} */
	@Override
	public synchronized void pushNestedLock(LockBase transaction) {
		// validate lock objects
		Set<String> keys = transaction.getLockKeys();
		getLockKeys();
		if (keys != null) {
			for (String key : keys) {
				if (!orderedKeys.containsKey(key)) {
					throw new TransactionNestedException("Nested Key Not Locked in MainLock: " + key);
				}
			}
		}
		super.pushNestedLock(transaction);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void finalize() {
		//TODO error message !
		release();
	}

	/** {@inheritDoc} */
	@Override
	public DbManager getDbManager() {
		return manager;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized Set<String> getLockKeys() {
		if (orderedKeys == null) {
			orderedKeys = new TreeMap<>();
			if (objects != null) {
				for (Persistable o : objects) {
					String key = createKey(o);
					orderedKeys.put(key, o);
				}
			}
		}
		return orderedKeys.keySet();
	}

}
