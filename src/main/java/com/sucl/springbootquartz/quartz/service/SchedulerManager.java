package com.sucl.springbootquartz.quartz.service;

import com.sucl.springbootquartz.quartz.entity.SchedulerJob;
import com.sucl.springbootquartz.quartz.entity.SchedulerJobKey;

import java.util.List;
import java.util.Map;

/**
 * @author sucl
 * @date 2019/6/17
 */
public interface SchedulerManager {

    void pause(SchedulerJobKey schedulerJobKey);

    void resume(SchedulerJobKey schedulerJobKey);

    void remove(SchedulerJobKey schedulerJobKey);

    SchedulerJob getSchedulerJob(SchedulerJobKey schedulerJobKey);

    SchedulerJob saveSchedulerJob(SchedulerJob schedulerJobKey);

    List<SchedulerJob> getSchedulerJobsByProperty(String property, String value);

    List<Map<String, String>> getJobProviders();
}
