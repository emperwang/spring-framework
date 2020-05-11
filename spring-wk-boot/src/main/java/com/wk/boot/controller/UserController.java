package com.wk.boot.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

	@GetMapping("c1.do")
	@ResponseBody
	public String test1(){

		return "hello world";
	}

	@GetMapping("json.do")
	@ResponseBody
	public Object retJson(){
		Map<String, String> retMap = new HashMap<>();
		retMap.put("name", "zhangsan");
		return retMap;
	}
}
