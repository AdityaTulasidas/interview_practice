package com.aditya.dataconnect.executionengine.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
public class AsyncConfig {
    // This configuration class sets up a ThreadPoolTaskExecutor for asynchronous task execution
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100); // minimum number of threads which are alive in the pool
        executor.setMaxPoolSize(200); // maximum number of threads which can be created in the pool
        executor.setQueueCapacity(100); // Determines how many tasks can be queued for execution if all threads are busy.
        executor.setThreadNamePrefix("DataSync-"); // thread name prefix, useful for monitoring
        executor.setKeepAliveSeconds(90);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);  // Ensures tasks complete on application shutdown
        executor.initialize();
        return executor;
    }
}
