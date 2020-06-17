package com.jixiang.service;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class JMSSender {
	private static Log logger = LogFactory.getLog(JMSSender.class);
	@Resource(name = "ifsQuartzJmsTemplate")
	private JmsTemplate jmsTemplate;
	public void sendTextMessage(String destination, final String msg) {
		if (msg == null) {
			logger.error("can't send empty text message.");
			return;
		}
		logger.debug(msg);
		jmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(msg);
			}
		});
	}
	
}
