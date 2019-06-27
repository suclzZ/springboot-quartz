package com.sucl.springbootquartz.quartz;

import org.quartz.JobExecutionContext;

/**
 * @author sucl
 * @date 2019/6/17
 */
public interface JobProvider {

    void runJob(JobExecutionContext context, String name);
}
