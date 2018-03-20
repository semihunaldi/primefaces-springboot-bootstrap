package com.semihunaldi.primefaces.springboot.bootstrap.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("testController")
@Scope("request")
public class TestController {

	public String getGreetingText() {
		return "Primefaces on spring boot";
	}

	public void throwException() throws Throwable {
		throw new Throwable();
	}
}
