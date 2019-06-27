package com.sucl.springbootquartz.quartz.service;

import com.sucl.springbootquartz.quartz.entity.SchedulerJob;
import com.sucl.springbootquartz.quartz.entity.SchedulerJobKey;

import java.util.List;

/**
 * @author sucl
 * @date 2019/6/17
 */
public interface SchedulerJobService{

    SchedulerJob getById(SchedulerJobKey schedulerJobKey);

    List<SchedulerJob> getAll(String property, String value);
}
