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
package de.mhus.lib.form;

import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.errors.MException;

public abstract class UiComponent {

	public static final String FULL_SIZE = "fullSize";
	public static final String FULL_SIZE_DEFAULT = "fullSizeDefault";
	private static final String WIZARD = null;
	
	private MForm form;
	private IConfig config;

	public void doInit(MForm form, IConfig config) {
		this.form = form;
		this.config = config;
	}

	public MForm getForm() {
		return form;
	}

	public IConfig getConfig() {
		return config;
	}

	public abstract void doRevert() throws MException;

	public abstract void doUpdateValue() throws MException;
	
	public abstract void doUpdateMetadata() throws MException;
	
	public abstract void setVisible(boolean visible) throws MException;
	
	public abstract boolean isVisible() throws MException;
	
	public abstract void setEnabled(boolean enabled) throws MException;
	
	public abstract void setEditable(boolean editable) throws MException;
	
	public abstract boolean isEnabled() throws MException;

	public boolean isFullSize() {
		return config.getBoolean(FULL_SIZE, config.getBoolean(FULL_SIZE_DEFAULT, false));
	}

	public UiWizard getWizard() {
		Object obj = config.getProperty(WIZARD);
		if (obj == null) return null;
		if (obj instanceof UiWizard) return (UiWizard)obj;
		try {
			if (obj instanceof String) return getForm().getAdapterProvider().createWizard((String)obj);
		} catch (Exception e) {
			MLogUtil.log().d(e);
		}
		return null; // TODO
	}

	public String getName() {
		return config.getString("name", "");
	}
	
	public abstract void setError(String error);
	
	public abstract void clearError();

	public String getConfigString(String name, String def) {
		IConfig c = getConfig();
		if (c == null) return def;
		return c.getString(name, def);
	}


}
