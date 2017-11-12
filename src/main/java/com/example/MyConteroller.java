package com.example;

import org.springframework.stereotype.Controller;
/**
 * Controller class to direct request to Home page in templates folder
 */
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class MyConteroller {
	@RequestMapping("/")
	public String getHtml() {
		return "index";
	}
}
