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
package de.mhus.lib.portlet.callback;

import java.util.LinkedList;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.util.MNls;
import de.mhus.lib.core.util.MNlsProvider;
import de.mhus.lib.errors.MException;

/**
 * <p>CallContext class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class CallContext implements MNlsProvider {

	/** Constant <code>DEFAULT_DATA_NAME="data"</code> */
	public static final String DEFAULT_DATA_NAME = "data";
	
	private JsonGenerator response;
	private ObjectMapper mapper;
	private ObjectNode node;
	private MNls nls;
	private LinkedList<Alert> alerts;
	private boolean success = true;
	private ArrayNode result;
	private Log log;
	private ResourceRequest request;
	private IProperties properties;

	/**
	 * <p>Constructor for CallContext.</p>
	 *
	 * @param callback a {@link de.mhus.lib.portlet.callback.AbstractAjaxCallback} object.
	 * @param request a {@link javax.portlet.ResourceRequest} object.
	 * @param response a {@link org.codehaus.jackson.JsonGenerator} object.
	 */
	public CallContext(AbstractAjaxCallback callback, ResourceRequest request, JsonGenerator response) {
		this.request = request;
		this.response = response;
//		this.callback = callback;
		this.log = callback.getLog();
		this.nls = callback.getNls();
		
		mapper = new ObjectMapper();
		node = mapper.createObjectNode();
		result = node.putArray("results");
		properties =callback.createProperties(request);

	}

	/**
	 * <p>addSuccess.</p>
	 *
	 * @param msg a {@link java.lang.String} object.
	 */
	public void addSuccess(String msg) {
		addAlert(new Alert(Alert.PRIORITY.SUCCESS, msg), true);
	}
	
	/**
	 * <p>addError.</p>
	 *
	 * @param error a {@link java.lang.String} object.
	 */
	public void addError(String error) {
		addAlert(new Alert(Alert.PRIORITY.ERROR, error), true);
	}

	/**
	 * <p>addWarning.</p>
	 *
	 * @param warning a {@link java.lang.String} object.
	 */
	public void addWarning(String warning) {
		addAlert(new Alert(Alert.PRIORITY.WARNING, warning), true);
	}

	/**
	 * <p>addInforamtion.</p>
	 *
	 * @param info a {@link java.lang.String} object.
	 */
	public void addInforamtion(String info) {
		addAlert(new Alert(Alert.PRIORITY.INFO, info), true);
	}
	
	/**
	 * <p>addAlert.</p>
	 *
	 * @param alert a {@link de.mhus.lib.portlet.callback.CallContext.Alert} object.
	 * @param useNls a boolean.
	 */
	public void addAlert(Alert alert, boolean useNls) {
		
		String msg = alert.msg;
		alert.msg = MNls.find(this, msg);
		
		if (alerts == null)
			alerts = new LinkedList<Alert>();
		alerts.add(alert);
	}
	
	/**
	 * <p>setRuntimeError.</p>
	 *
	 * @param t a {@link java.lang.Throwable} object.
	 */
	public void setRuntimeError(Throwable t) {
		log.e(t);
		if (alerts != null) alerts.clear();
		addError(t.toString());
		setSuccess(false);
	}

	/**
	 * <p>Setter for the field <code>success</code>.</p>
	 *
	 * @param success a boolean.
	 */
	public void setSuccess(boolean success) {
		this.success  = success;
	}

	/**
	 * <p>doSend.</p>
	 */
	public void doSend() {
		if (!success) {
			result.removeAll();
		}
		node.put("success", success ? 1 : 0);
		
		if (alerts != null) {
			ArrayNode sub = node.putArray("alerts");
			for (Alert alert : alerts) {
				ObjectNode line = sub.addObject();
				alert.print(line);
			}
		}

		response.setCodec(mapper);
		try {
			response.writeTree(node);
		} catch (Exception e) {
			log.e(e);
		}

	}

	/** {@inheritDoc} */
	@Override
	public MNls getNls() {
		return nls;
	}

	/**
	 * <p>Setter for the field <code>nls</code>.</p>
	 *
	 * @param mNls a {@link de.mhus.lib.core.util.MNls} object.
	 */
	public void setNls(MNls mNls) {
		this.nls = mNls;
	}

	/**
	 * <p>Getter for the field <code>request</code>.</p>
	 *
	 * @return a {@link javax.portlet.ResourceRequest} object.
	 */
	public ResourceRequest getRequest() {
		return request;
	}
	
	/**
	 * <p>log.</p>
	 *
	 * @return a {@link de.mhus.lib.core.logging.Log} object.
	 */
	public Log log() {
		return log;
	}
	
	/**
	 * <p>addResult.</p>
	 *
	 * @return a {@link org.codehaus.jackson.node.ObjectNode} object.
	 */
	public ObjectNode addResult() {
		return result.addObject();
	}
	
	public static class Alert {
		private PRIORITY type;
		private String msg;

		public Alert(PRIORITY type, String msg) {
			this.type = type;
			this.msg = msg;
		}

		public void print(ObjectNode line) {
			line.put("message", msg);
			line.put("priority", type.name().toLowerCase());
			if (type == PRIORITY.ERROR) {
				line.put("fade", 0);
			}
		}

		public enum PRIORITY {ERROR,INFO,WARNING,SUCCESS};
	}
	
	/**
	 * <p>getSession.</p>
	 *
	 * @return a {@link javax.portlet.PortletSession} object.
	 */
	public PortletSession getSession() {
		return getRequest().getPortletSession();
	}
	
	/**
	 * <p>Getter for the field <code>properties</code>.</p>
	 *
	 * @return a {@link de.mhus.lib.core.IProperties} object.
	 */
	public IProperties getProperties() {
		return properties;
	}

	/**
	 * <p>getData.</p>
	 *
	 * @return a {@link org.codehaus.jackson.JsonNode} object.
	 * @throws de.mhus.lib.errors.MException if any.
	 */
	public JsonNode getData() throws MException {
		return getData(DEFAULT_DATA_NAME);
	}
	
	/**
	 * <p>getData.</p>
	 *
	 * @param attributeName a {@link java.lang.String} object.
	 * @return a {@link org.codehaus.jackson.JsonNode} object.
	 * @throws de.mhus.lib.errors.MException if any.
	 */
	public JsonNode getData(String attributeName) throws MException {
		JsonNode in = null;
		String dataStr = getProperties().getString(attributeName);
		if (dataStr != null && !dataStr.equals("undefined")) {
			try {
				JsonFactory f = new JsonFactory();
				ObjectMapper mapper = new ObjectMapper();
				JsonParser parser = f.createJsonParser(dataStr);
				in = mapper.readTree(parser);
			} catch (Throwable t) {}
		}
		return in;
	}

}
