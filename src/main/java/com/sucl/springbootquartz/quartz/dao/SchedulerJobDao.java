package com.sucl.springbootquartz.quartz.dao;

import com.sucl.springbootquartz.quartz.entity.SchedulerJob;
import com.sucl.springbootquartz.quartz.entity.SchedulerJobKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author sucl
 * @date 2019/6/17
 */
@Repository
public interface SchedulerJobDao extends JpaRepository<SchedulerJob,SchedulerJobKey>,JpaSpecificationExecutor<SchedulerJob>,
        org.springframework.data.repository.Repository<SchedulerJob, SchedulerJobKey> {
}
