package com.github.ctarrington.amqSamples.try01;

import java.util.Iterator;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

public class TopicAnalyzer implements MessageListener
{
	private String uri;

	public TopicAnalyzer(String uri)
	{
		this.uri = uri;
	}

	public void analyze() throws JMSException, InterruptedException
	{
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(this.uri);
		Connection connection = factory.createConnection();
		ActiveMQConnection amqc = (ActiveMQConnection) connection;
		connection.start();

		Set<ActiveMQTopic> currentMessageTopics = amqc.getDestinationSource().getTopics();
		Iterator<ActiveMQTopic> messageTopicIterator = currentMessageTopics.iterator();

		while (messageTopicIterator.hasNext())
		{
			ActiveMQTopic currentTopic = messageTopicIterator.next();
			QueueSession queueSession = amqc.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = queueSession.createConsumer(currentTopic, null);
			consumer.setMessageListener(this);
		}

		Thread.sleep(60*1000);
		connection.close();

	}

	public static void main(String[] args) throws JMSException, InterruptedException
	{
		TopicAnalyzer qa = new TopicAnalyzer("tcp://localhost:61616");
		qa.analyze();
	}

	@Override
	public void onMessage(Message msg)
	{
		try
		{
			TextMessage txtMsg = (TextMessage) msg;
			System.out.println(txtMsg.getText());
		} catch (JMSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
