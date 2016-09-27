package com.agfa.he.sh.cris.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agfa.he.sh.cris.service.JobContainer;

@RestController
public class JobController {
	
	@Autowired
	private JobContainer jobContainer;

	@RequestMapping("/job/start")
	public String start(@RequestParam(name="forceRestart", required=false) Boolean forceRestart, 
			@RequestParam(name="parallel", required=false) Integer parallel) {
		
		if (parallel == null) {
			parallel = Runtime.getRuntime().availableProcessors()*2;
		}
		if (forceRestart == null) {
			forceRestart = false;
		}
		
		try {
			String result = jobContainer.start(forceRestart, parallel);
			return result;
		} catch(Exception e) {
			e.printStackTrace();			
		}
		return "ERROR";
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
