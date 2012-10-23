package com.github.ctarrington.amqSamples.try01;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SimpleConsumer implements MessageListener
{
	private Connection connection;
	private int received;
	
	public void init() throws JMSException, InterruptedException
	{
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		connection = factory.createConnection();
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("TEST.FOO");
		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener(this);
		
		boolean done = false;
		while (!done)
		{
			Thread.sleep(1000);
			if (received == 0)
			{
				done = true;
			}
			else
			{
				received = 0;
			}
		}
	}
	
	@Override
	public void onMessage(Message msg)
	{		
		try
		{
			received++;
			TextMessage txtMsg = (TextMessage) msg;
			System.out.println(txtMsg.getText());
		} catch (JMSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void close()
	{
		try
		{
			connection.close();
		} 
		catch (Throwable ignore)
		{
		}
	}
	
	
	
	public static void main(String[] args)
	{
		SimpleConsumer sc = new SimpleConsumer();
		
		try
		{
			sc.init();			
		} 
		catch (JMSException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			sc.close();
		}
	}
}
