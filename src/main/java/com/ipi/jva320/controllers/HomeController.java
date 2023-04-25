package com.ipi.jva320.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ResourceBundle;

@Controller
public class HomeController {
	ResourceBundle messageBundle = ResourceBundle.getBundle("messages");
	@GetMapping(value = "/")
	public String home(final ModelMap model) {
		model.put("htmlMessage", messageBundle.getString("msg.salarie"));
		return "home";
	}
}
