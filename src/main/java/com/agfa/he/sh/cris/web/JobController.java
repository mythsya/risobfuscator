package com.agfa.he.sh.cris.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

	@RequestMapping("/job/start")
	public String start(@RequestParam(name="parallel", required=false) Integer parallel) {
		if (parallel == null) {
			parallel = Runtime.getRuntime().availableProcessors()*2;
		}
		return "OK";
	}
	
	@RequestMapping("/job/stop")
	public String stop() {
		return "OK";
	}
	
	@RequestMapping("/job/status")
	public String status() {
		return "OK";
	}
	
	@RequestMapping("/job/cancel")
	public String cancel() {
		return "OK";
	}
}
