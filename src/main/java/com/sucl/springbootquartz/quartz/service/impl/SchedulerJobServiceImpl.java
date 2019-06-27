package com.sucl.springbootquartz.quartz.service.impl;

import com.sucl.springbootquartz.quartz.dao.SchedulerJobDao;
import com.sucl.springbootquartz.quartz.entity.SchedulerJob;
import com.sucl.springbootquartz.quartz.entity.SchedulerJobKey;
import com.sucl.springbootquartz.quartz.service.SchedulerJobService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author sucl
 * @date 2019/6/17
 */
@Service
@Transactional
public class SchedulerJobServiceImpl implements SchedulerJobService {
    @Autowired
    private SchedulerJobDao schedulerJobDao;

    @Override
    public SchedulerJob getById(SchedulerJobKey schedulerJobKey) {
        return schedulerJobDao.findOne(schedulerJobKey);
    }

    @Override
    public List<SchedulerJob> getAll(String property, String value) {
        SchedulerJob schedulerJob = new SchedulerJob();
        try {
            PropertyUtils.setProperty(schedulerJob,property,value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return schedulerJobDao.findAll(Example.of(schedulerJob));
    }
}
