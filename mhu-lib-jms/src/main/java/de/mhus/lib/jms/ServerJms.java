package de.mhus.lib.jms;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;


public abstract class ServerJms extends JmsChannel implements MessageListener {

    public ServerJms(JmsDestination dest) {
		super(dest);
	}

	MessageConsumer consumer;

	private MessageProducer replyProducer;
	
	@Override
	public synchronized void open() throws JMSException {
		if (isClosed()) throw new JMSException("server closed");
		if (consumer == null || getSession() == null) {
			dest.open();
			dest.getConnection().registerChannel(this);
			log().i("consume",dest);
            consumer = dest.getConnection().getSession().createConsumer(dest.getDestination());
            consumer.setMessageListener(this);
		}
	}
	
	public synchronized void openAnswer() throws JMSException {
		if (replyProducer == null || getSession() == null) {
			open();
	        replyProducer = dest.getConnection().getSession().createProducer(null);
	        replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		}
	}
	
	@Override
	public void reset() {
		log().i("reset",dest);
		try {
			consumer.close();
		} catch (Throwable t) {log().t(t);}
		try {
			replyProducer.close();
		} catch (Throwable t) {log().t(t);}
	}

	public abstract void receivedOneWay(Message msg) throws JMSException;
	
	public abstract Message received(Message msg) throws JMSException;

	protected void sendAnswer(Message msg, Message answer) throws JMSException {
		openAnswer();
		if (answer == null) answer = getSession().createTextMessage(null); // other side is waiting for an answer - send a null text
		answer.setJMSMessageID(createMessageId());
		answer.setJMSCorrelationID(msg.getJMSCorrelationID());
        replyProducer.send(msg.getJMSReplyTo(), answer);
	}

	@Override
	public void onMessage(Message message) {
		try {
			if (message.getJMSReplyTo() != null) {
				Message answer = received(message);
				sendAnswer(message, answer);
			} else {
				receivedOneWay(message);
			}
		} catch (Throwable t) {
			log().w(t);
		}
	}

	@Override
	public void doBeat() {
		if (isClosed()) return;
		try {
			open(); // try to reopen and re-listen
		} catch (JMSException e) {
			log().t(e);
		}
	}

	@Override
	public String getName() {
		return "openwire:/server" + dest.getName();
	}

	@Override
	public boolean isConnected() {
		return !(consumer == null || getSession() == null);
	}

}