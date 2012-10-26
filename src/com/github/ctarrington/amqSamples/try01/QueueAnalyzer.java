package com.github.ctarrington.amqSamples.try01;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;

public class QueueAnalyzer
{
	private String uri;

	public QueueAnalyzer(String uri)
	{
		this.uri = uri;
	}

	public void analyze() throws JMSException
	{
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(this.uri);
		Connection connection = factory.createConnection();
		ActiveMQConnection amqc = (ActiveMQConnection) connection;
		connection.start();

		Set<ActiveMQQueue> currentMessageQueues = amqc.getDestinationSource().getQueues();
		Iterator<ActiveMQQueue> messageQueueIterator = currentMessageQueues.iterator();

		while (messageQueueIterator.hasNext())
		{
			ActiveMQQueue currentQueue = messageQueueIterator.next();
			QueueSession queueSession = amqc.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
			QueueBrowser browser = queueSession.createBrowser(currentQueue);
			Enumeration<?> messagesInQueue = browser.getEnumeration();

			while (messagesInQueue.hasMoreElements())
			{
				Message queueMessage = (Message) messagesInQueue.nextElement();

				if (queueMessage instanceof ActiveMQTextMessage)
				{
					ActiveMQTextMessage textMessage = (ActiveMQTextMessage) queueMessage;
					String raw = textMessage.getText();
					System.out.println("raw = "+raw);
				}
			}
		}

		connection.close();

	}

	public static void main(String[] args) throws JMSException
	{
		QueueAnalyzer qa = new QueueAnalyzer("tcp://localhost:61616");
		qa.analyze();
	}

}
