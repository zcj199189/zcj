package com.jixiang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jixiang.dao.UserRegisterDao;
import com.jixiang.model.UserRegister;

import io.swagger.annotations.ApiOperation;
//http://localhost:8080/swagger-ui.html#/
@Controller("userController")
public class UserController {
	@Autowired
	UserRegisterDao userRegisterDao;
	
	//method = RequestMethod.POST不能忘记声明，不然swagger会很多这个方法
	@RequestMapping(value = "/getUser", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "获取用户手机号", notes = "--")
	public String getUser(@RequestParam("name") String name) {
		UserRegister u = userRegisterDao.queryModelUserRegister(name);
		return u.getPhone();
	}
}
