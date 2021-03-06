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
package de.mhus.lib.core;

import de.mhus.lib.core.logging.Log;

public abstract class MHousekeeperTask extends MTimerTask {
	
	private Log log = Log.getLog(this.getClass());

	public MHousekeeperTask() {
		
	}
	
	public MHousekeeperTask(String name) {
		setName(name);
	}
	
	protected Log log() {
		return log;
	}
}
