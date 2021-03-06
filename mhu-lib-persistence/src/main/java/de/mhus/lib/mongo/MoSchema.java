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
package de.mhus.lib.mongo;

import java.util.LinkedList;
import java.util.List;

import org.mongodb.morphia.mapping.Mapper;

import de.mhus.lib.adb.Persistable;
import de.mhus.lib.adb.transaction.LockStrategy;

public abstract class MoSchema {

	@SuppressWarnings("unused")
	private LinkedList<Class<? extends Persistable>> objectTypes;
	protected LockStrategy lockStrategy; // set this object to enable locking
	
	public abstract void findObjectTypes(List<Class<? extends Persistable>> list);

	public void initMapper(Mapper mapper) {
		
	}

	public abstract String getDatabaseName();
	
	
}
