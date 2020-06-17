package com.jixiang.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import com.jixiang.dao.UserRegisterDao;
import com.jixiang.model.UserRegister;
import com.jixiang.service.JMSSender;
import com.jixiang.service.RedisService;
import io.swagger.annotations.ApiOperation;
import redis.clients.jedis.Jedis;
//http://localhost:8080/swagger-ui.html#/
@Controller("userController")
public class UserController {
	private Log log = LogFactory.getLog(UserController.class);
	@Autowired
	UserRegisterDao userRegisterDao;
	@Autowired
	RedisService redis;
	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;
	@Autowired
	private JMSSender jmsSender;
	
	@RequestMapping(value = "/tryJms", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "测试队列", notes = "--")
	public String tryJms(@RequestParam("name") String name) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		jmsSender.sendTextMessage("zcj", JSON.toJSONString(map));
		return "发送成功";
	}
	
	//method = RequestMethod.POST不能忘记声明，不然swagger会很多这个方法
	@RequestMapping(value = "/getUser", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "获取用户手机号", notes = "--")
	public String getUser(@RequestParam("name") String name) {
		UserRegister u = userRegisterDao.queryModelUserRegister(name);
		redis.set(name,u.getPhone(), 10, TimeUnit.MINUTES);
		return redis.get(name);
	}
	//method = RequestMethod.POST不能忘记声明，不然swagger会很多这个方法
	@RequestMapping(value = "/getLock", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "获取锁", notes = "--")
	public String getLock(@RequestParam("name") String name) {
		
		if (this.tryLock(name) == false) {
			log.error("没有获取到并发锁:name=" + name);
			return "没有获取到并发锁";
		}
		
		return "获取到并发锁";
	}
	
	public boolean tryLock(String key) {
		int i = 1;
		while (i++ <= 9) {
			if (this.getJedisLock(key, 300)) {
				return true;
			}
			try {
				Thread.sleep(50L);
			} catch (Exception e) {
				log.error(e);
			}
		}
		return false;
	}
	
	public boolean getJedisLock(String key, int seconds) {
		JedisConnection jedisConnection = null;
		try {
			key = "myKey"+ key;
			long begin = System.currentTimeMillis();
			jedisConnection = (JedisConnection) jedisConnectionFactory.getConnection();
			Jedis jedis = jedisConnection.getNativeConnection();
			// NX是不存在时才set， XX是存在时才set， EX是秒，PX是毫秒
			String result = jedis.set(key, "lock", "NX", "EX", seconds);
			long useTime = System.currentTimeMillis() - begin;
			if ("OK".equalsIgnoreCase(result)) {
				if (useTime > 10)
					log.info(key + "----获取锁 耗时 ：" + useTime);
				return true;
			} else {
				if (useTime > 1)
					log.info(key + "----未获取锁 耗时 ：" + useTime);
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		} finally {
			if (jedisConnection != null) {
				try {
					jedisConnection.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
}
