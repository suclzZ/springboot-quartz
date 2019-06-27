package com.sucl.springbootquartz.quartz.service.impl;

import com.sucl.springbootquartz.quartz.JobDesc;
import com.sucl.springbootquartz.quartz.JobProvider;
import com.sucl.springbootquartz.quartz.entity.CronTrigger;
import com.sucl.springbootquartz.quartz.entity.SchedulerJob;
import com.sucl.springbootquartz.quartz.entity.SchedulerJobKey;
import com.sucl.springbootquartz.quartz.service.SchedulerJobService;
import com.sucl.springbootquartz.quartz.service.SchedulerManager;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * @author sucl
 * @date 2019/6/17
 */
@Service("schedulerService")
public class SchedulerManagerImpl implements SchedulerManager, ApplicationContextAware, DisposableBean {

    @Autowired(required = false)
    private Scheduler scheduler;
    @Autowired(required = false)
    private JobDetailImpl jobDetail;
    @Autowired
    private SchedulerJobService schedulerJobService;

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        System.setProperty("org.terracotta.quartz.skipUpdateCheck", "true");
    }

    public List<Map<String, String>> getJobProviders() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, JobProvider> jobProviderMap = applicationContext.getBeansOfType(JobProvider.class);
        if(MapUtils.isNotEmpty(jobProviderMap)){
            list.add(convertToMap(jobProviderMap));
        }
        return list;
    }

    private Map<String,String> convertToMap(Map<String,JobProvider> jobProviderMap) {
        Map<String,String> map = new HashMap<>();
        for(Map.Entry<String,JobProvider> entry:jobProviderMap.entrySet()){
            map.put("code",entry.getKey());
            JobProvider jobProvider = entry.getValue();
            JobDesc jobDesc = jobProvider.getClass().getAnnotation(JobDesc.class);
            if(jobDesc!=null && StringUtils.isNotEmpty(jobDesc.name())){
                map.put("name",jobDesc.name());
            }else{
                map.put("name",entry.getKey());
            }
        }
        return map;
    }


    public SchedulerJob getSchedulerJob(SchedulerJobKey schedulerJobKey) {
        return schedulerJobService.getById(schedulerJobKey);
    }

    public SchedulerJob saveSchedulerJob(SchedulerJob schedulerJob){
        validateJob(schedulerJob);
        try {
            jobDetail.setDescription(schedulerJob.getJobDetails().getDescription());
            jobDetail.setGroup(schedulerJob.getJobDetails().getJobGroup());
            if (StringUtils.isEmpty(schedulerJob.getJobDetails().getJobName()))
                jobDetail.setName(UUID.randomUUID().toString());
            else {
                jobDetail.setName(schedulerJob.getJobDetails().getJobName());
            }

            CronTrigger sCronTrigger = schedulerJob.getCronTrigger();

            if (sCronTrigger.getTriggerName() == null) {
                sCronTrigger.setTriggerName(UUID.randomUUID().toString());
                sCronTrigger.setTriggerGroup("DEFAULT");
            }
            TriggerKey triggerKey = new TriggerKey(sCronTrigger.getTriggerName(), sCronTrigger.getTriggerGroup());

            Trigger cronTrigger = scheduler.getTrigger(triggerKey);
            CronTriggerImpl cronTriggerImpl;
            if (cronTrigger == null && (cronTrigger instanceof CronTriggerImpl))
                cronTriggerImpl = (CronTriggerImpl) cronTrigger;
            else {
                cronTriggerImpl = new CronTriggerImpl();
            }

            cronTriggerImpl.setName(sCronTrigger.getTriggerName());
            cronTriggerImpl.setGroup(sCronTrigger.getTriggerGroup());
            cronTriggerImpl.setJobKey(jobDetail.getKey());
            cronTriggerImpl.setDescription(schedulerJob.getDescription());

            try {
                cronTriggerImpl.setCronExpression(sCronTrigger.getCronExpression());
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("时间表达式不正确:" + e.getMessage());
            }
            scheduler.addJob(jobDetail, true);
            if (cronTrigger == null) {
                scheduler.scheduleJob(cronTriggerImpl);
            }
            scheduler.rescheduleJob(triggerKey, cronTriggerImpl);
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return schedulerJob;
    }

    private void validateJob(SchedulerJob schedulerJob) {
        String scheName = schedulerJob.getSchedName(),triggerName = schedulerJob.getTriggerName(),triggerGroup = schedulerJob.getTriggerGroup();
        SchedulerJob oldSchedulerJob = schedulerJobService.getById(new SchedulerJobKey(scheName, triggerName, triggerGroup));
        if(oldSchedulerJob!=null){//update
            if(!oldSchedulerJob.getJobDetails().getJobName().equals(schedulerJob.getJobDetails().getJobName())){
                throw new RuntimeException("不能修改任务名！");
            }
        }
    }

    public void pause(SchedulerJobKey schedulerJobKey) {
        pauseTrigger(schedulerJobKey.getTriggerName(), schedulerJobKey.getTriggerGroup());
    }

    public void resume(SchedulerJobKey schedulerJobKey) {
        resumeTrigger(schedulerJobKey.getTriggerName(), schedulerJobKey.getTriggerGroup());
    }

    public void remove(SchedulerJobKey schedulerJobKey) {
        removeTrigger(schedulerJobKey);
    }

    private void pauseTrigger(String triggerName, String group) {
        try {
            scheduler.pauseTrigger(new TriggerKey(triggerName, group));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private void resumeTrigger(String triggerName, String group) {
        try {
            scheduler.resumeTrigger(new TriggerKey(triggerName, group));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean removeTrigger(SchedulerJobKey schedulerJobKey) {
        try {
            TriggerKey triggerKey = new TriggerKey(schedulerJobKey.getTriggerName(), schedulerJobKey.getTriggerGroup());
            scheduler.pauseTrigger(triggerKey);
            removeJobDetails(schedulerJobKey);
            return scheduler.unscheduleJob(triggerKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeJobDetails(SchedulerJobKey schedulerJobKey) {
        SchedulerJob schedulerJob = schedulerJobService.getById(schedulerJobKey);
        JobKey jobKey = new JobKey(schedulerJob.getJobDetails().getJobName(),schedulerJob.getJobDetails().getJobGroup());
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SchedulerJob> getSchedulerJobsByProperty(String property, String value) {
        return schedulerJobService.getAll(property,value);
    }

    public void destroy() throws Exception {
        if(scheduler!=null)
            scheduler.shutdown(true);
    }
}
