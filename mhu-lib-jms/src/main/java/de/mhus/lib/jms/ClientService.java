package de.mhus.lib.jms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.codehaus.jackson.JsonNode;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MJson;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.MRuntimeException;
import de.mhus.lib.errors.NotSupportedException;

public class ClientService<T> extends ClientJms implements JmsChannelService {
	
	private ServiceDescriptor desc;
	private T proxy;
	private ClassLoader classLoader = getClass().getClassLoader();

	public ClientService(JmsDestination dest, ServiceDescriptor desc ) {
		super(dest);
		this.desc = desc;
		createProxy();
	}

	@SuppressWarnings("unchecked")
	protected void createProxy() {
		Class<?> ifc = desc.getInterface();
		if (ifc == null) return;
		
	    InvocationHandler handler = new MyInvocationHandler();

		proxy = (T) Proxy.newProxyInstance(ifc.getClassLoader(),
                new Class[] { ifc },
                handler);
		
	}

	public T getClientProxy() {
		return proxy;
	}

	private class MyInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			String name = method.getName().toLowerCase();
			
			FunctionDescriptor fDesc = desc.getFunction(name);
			if (fDesc == null) {
//				log().w("function not found", name);
				throw new MRuntimeException("function not found", name);
			}
			
			Message msg = null;
			if (args != null && args.length == 1 && args[0] instanceof Message) {
				msg = (Message) args[0];
				msg.setBooleanProperty("direct", true);
			} else
				msg = getSession().createObjectMessage(args);
			
			msg.setStringProperty("function", method.getName().toLowerCase());
			
			if (fDesc.isOneWay() || dest.isTopic() && fDesc.getReturnType() == Void.class) {

				try {
					sendJmsOneWay(msg);
				} catch (Exception e) {
					log().w("internal error",desc.getInterface().getCanonicalName(),method.getName(),e);
				}
			} else
			if (dest.isTopic() && fDesc.getReturnType() == List.class ) {
				try {
					Message[] answers = sendJmsBroadcast(msg);
					
					LinkedList<Message> out = new LinkedList<>();
					
					for (Message answer : answers) {
						if (answer.getStringProperty("exception") == null) {
							out.add(answer);
						}
					}
					
					return out;
				} catch (Exception e) {
					log().w("internal error",desc.getInterface().getCanonicalName(),method.getName(),e);
				}

			} else {
				try {
					Message res = sendJms(msg);
					// check success and throw exceptions
					if (res == null)
						throw new MRuntimeException("internal error: result is null",desc.getInterface().getCanonicalName(),method.getName());
					
					String exceptionType = msg.getStringProperty("exception");
					if (exceptionType != null) {
						Class<?> exceptionClass = getClassLoader().loadClass(exceptionType);
						Throwable exception = null;
						try {
							Constructor<?> constructor = exceptionClass.getConstructor(String.class);
							exception = (Throwable) constructor.newInstance(
									res.getStringProperty("exceptionMessage") + " [" + 
									res.getStringProperty("exceptionClass") + "." + 
									res.getStringProperty("exceptionMethod") + "]" );
						} catch (Throwable t) {
							exception = (Throwable) exceptionClass.newInstance();
						}
						throw exception;
					}
					try {
						if (Message.class.isAssignableFrom(method.getReturnType()) && res.getBooleanProperty("direct"))
							return res;
					} catch (JMSException e) {}
					
					if (res instanceof ObjectMessage) {
						ObjectMessage om = (ObjectMessage)res;
						return om.getObject();
					}
					
					if (res instanceof BytesMessage) {
						BytesMessage bm = (BytesMessage)res;
						long len = Math.min(bm.getBodyLength(), 1024 * 1024 * 1024 * 100); // 100 MB
						byte[] buffer = new byte[(int) len];
						bm.readBytes(buffer);
						return buffer;
					}
					
					if (res instanceof TextMessage) {
						TextMessage tm = (TextMessage)res;
						return tm.getText();
					}
					
//					if (res instanceof MapMessage) {
//						MapMessage mm = (MapMessage)res;
//					}

					throw new NotSupportedException("message type is not supported",res.getClass().getCanonicalName());
				} catch (Exception e) {
					log().w("internal error",desc.getInterface().getCanonicalName(),method.getName(),e);
				}

			}
			
			return null;
		}
		
	}

	@Override
	public Class<?> getInterface() {
		return desc.getInterface();
	}

	public ClassLoader getClassLoader() {
		return classLoader ;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I> I getObject(Class<? extends I> ifc) {
		return (I)getClientProxy();
	}
		
	public void sendJsonOneWay(IProperties prop, JsonNode json) throws JMSException, IOException {
		
		ByteArrayOutputStream w = new ByteArrayOutputStream();
		try {
			MJson.save(json, w);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		}
		
		open();
		BytesMessage msg = getDestination().getConnection().getSession().createBytesMessage();
		MJms.setProperties(prop, msg);
		msg.writeBytes(w.toByteArray());
		sendJmsOneWay(msg);
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
}