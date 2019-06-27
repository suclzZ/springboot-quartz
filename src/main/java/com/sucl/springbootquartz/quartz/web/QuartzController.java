package com.sucl.springbootquartz.quartz.web;

import com.sucl.springbootquartz.quartz.entity.JobDetails;
import com.sucl.springbootquartz.quartz.entity.SchedulerJob;
import com.sucl.springbootquartz.quartz.entity.SchedulerJobKey;
import com.sucl.springbootquartz.quartz.service.SchedulerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sucl
 * @date 2019/6/17
 */
@RestController
@RequestMapping("/quartz")
public class QuartzController {

    @Autowired
    private SchedulerManager schedulerManager;

    @GetMapping
    public SchedulerJob getSchedulerJob(@RequestParam("schedName") String schedName, @RequestParam("triggerName") String triggerName, @RequestParam("triggerGroup") String triggerGroup) {
        SchedulerJobKey schedulerJobKey = new SchedulerJobKey();
        schedulerJobKey.setSchedName(schedName);
        schedulerJobKey.setTriggerGroup(triggerGroup);
        schedulerJobKey.setTriggerName(triggerName);
        return schedulerManager.getSchedulerJob(schedulerJobKey);
    }

    @PostMapping
    public void saveSchedulerJob( SchedulerJob job) {
        schedulerManager.saveSchedulerJob(job);
    }

    @RequestMapping("/remove")
    public void removeSchedulerJob( @RequestParam("schedName") String schedName, @RequestParam("triggerName") String triggerName, @RequestParam("triggerGroup") String triggerGroup) {
        SchedulerJobKey schedulerJobKey = new SchedulerJobKey();
        schedulerJobKey.setSchedName(schedName);
        schedulerJobKey.setTriggerGroup(triggerGroup);
        schedulerJobKey.setTriggerName(triggerName);
        this.schedulerManager.remove(schedulerJobKey);
    }

    @RequestMapping("/pause")
    public void pause(@RequestParam("schedName") String schedName, @RequestParam("triggerName") String triggerName, @RequestParam("triggerGroup") String triggerGroup) {
        SchedulerJobKey schedulerJobKey = new SchedulerJobKey();
        schedulerJobKey.setSchedName(schedName);
        schedulerJobKey.setTriggerGroup(triggerGroup);
        schedulerJobKey.setTriggerName(triggerName);
        this.schedulerManager.pause(schedulerJobKey);
    }

    @RequestMapping("/resume")
    public void resume(@RequestParam("schedName") String schedName, @RequestParam("triggerName") String triggerName, @RequestParam("triggerGroup") String triggerGroup) {
        SchedulerJobKey schedulerJobKey = new SchedulerJobKey();
        schedulerJobKey.setSchedName(schedName);
        schedulerJobKey.setTriggerGroup(triggerGroup);
        schedulerJobKey.setTriggerName(triggerName);
        this.schedulerManager.resume(schedulerJobKey);
    }

    @GetMapping("/jobdetails")
    public List<JobDetails> getSchedulerJobs(HttpServletRequest request, HttpServletResponse response) {
        List<SchedulerJob> schedulerJobs = this.schedulerManager.getSchedulerJobsByProperty("jobDetails.jobGroup", "batchAlertRuleManager");
        List<JobDetails> jobDetails = new ArrayList();
        for (SchedulerJob job : schedulerJobs) {
            jobDetails.add(job.getJobDetails());
        }
        return jobDetails;
    }

    @GetMapping("/jobs")
    public List<Map<String, String>> getJobProviders(HttpServletRequest request, HttpServletResponse response) {
        return this.schedulerManager.getJobProviders();
    }
}
