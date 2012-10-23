package com.github.ctarrington.amqSamples.try01;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SimpleProducer
{
	public static void main(String[] args)
	{
		Connection connection = null;
		try
		{
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
			connection = factory.createConnection();
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("TEST.FOO");
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			for (int ctr=0; ctr< 2000; ctr++)
			{
				TextMessage message = session.createTextMessage("Hi There "+ctr);
				producer.send(message);
				Thread.sleep(Math.round(Math.random()*500));
			}
		} 
		catch (JMSException e)
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			try
			{
				connection.close();
			} catch (Throwable ignore)
			{
			}
		}

	}
}
