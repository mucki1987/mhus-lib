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
package de.mhus.lib.core.console;

import java.io.IOException;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.system.CfgManager;
import de.mhus.lib.errors.MException;

public class JmxConsole extends VirtualConsole {

	@SuppressWarnings("unused")
	private JmxConsoleProxy jmxProxy;

	public JmxConsole() throws IOException, MException {
		super();
		width = 80;
		height = 40;
		echo = true;
		ResourceNode<?> config = MApi.lookup(CfgManager.class).getCfg(this, null);
		if (config != null) {
			width = config.getInt("width",width);
			height = config.getInt("height", height);
		}
		reset();
		jmxProxy = new JmxConsoleProxy(this);
	}

}
