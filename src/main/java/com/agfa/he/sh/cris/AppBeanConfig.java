package com.agfa.he.sh.cris;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.agfa.he.sh.cris.service.JobContainer;


@Configuration
public class AppBeanConfig {

	@Bean(name="jobContainer", initMethod="init", destroyMethod= "dispose")
	@Scope("singleton")
	public JobContainer jobContainer() {
		return new JobContainer();
	}
	
}
