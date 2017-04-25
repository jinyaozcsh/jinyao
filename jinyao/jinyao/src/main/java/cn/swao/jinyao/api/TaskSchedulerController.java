package cn.swao.jinyao.api;

import cn.swao.baselib.util.ArrayUtils;
import cn.swao.framework.aop.*;
import cn.swao.framework.api.ApiOutput;
import cn.swao.framework.model.TaskJob;
import cn.swao.framework.service.SchedulerService;
import cn.swao.framework.util.WebUtils;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/Scheduler")
// @ConditionalOnClass(name = "quartz.enabled")
public class TaskSchedulerController {

    @Autowired
    private SchedulerService schedulerService;

    @Log(value = LogEnum.info)
    @RequestMapping(value = "/getSchedulerList", method = RequestMethod.POST, consumes = "application/json")
    public ApiOutput getSchedulerList(@RequestBody String json) throws Exception {
        Map<String, Object> map = WebUtils.getJsonParams(json);
        String type = ArrayUtils.getMapString(map, "type");
        List<TaskJob> taskJobs = null;
        if (Strings.isNullOrEmpty(type))
            taskJobs = this.schedulerService.getAllTaskJob();
        else if (type.equals("excute"))
            taskJobs = this.schedulerService.getAllExecutingTaskJob();
        return new ApiOutput(taskJobs);
    }

    @Log(value = LogEnum.info)
    @RequestMapping(value = "/createJob", method = RequestMethod.POST, consumes = "application/json")
    public ApiOutput createJob(@RequestBody String json) throws Exception {
        Map<String, Object> map = WebUtils.getJsonParams(json);
        String jobName = ArrayUtils.getMapString(map, "jobName");
        String jobGroup = ArrayUtils.getMapString(map, "jobGroup");
        String jobClassName = ArrayUtils.getMapString(map, "jobClassName");
        String description = ArrayUtils.getMapString(map, "description");
        Map dataMap = (Map) map.get("dataMap");
        List<Map<String, String>> triggerList = (List<Map<String, String>>) map.get("triggerList");
        // String cronExpression = ArrayUtils.getMapString(map, "cronExpression");
        // String triggerGroup = ArrayUtils.getMapString(map, "triggerGroup");
        // String triggerName = ArrayUtils.getMapString(map, "triggerName");
        TaskJob taskJob = new TaskJob(jobName, jobGroup, jobClassName, dataMap, description, triggerList);
        boolean job = this.schedulerService.createJob(taskJob);
        return new ApiOutput(job);
    }

    @Log(value = LogEnum.info)
    @RequestMapping(value = "/modifyJobTime", method = RequestMethod.POST, consumes = "application/json")
    public ApiOutput modifyJobTime(@RequestBody String json) throws Exception {
        Map<String, Object> map = WebUtils.getJsonParams(json);
        String cronExpression = ArrayUtils.getMapString(map, "cronExpression");
        String triggerGroup = ArrayUtils.getMapString(map, "triggerGroup");
        String triggerName = ArrayUtils.getMapString(map, "triggerName");
        this.schedulerService.modifyJobTime(triggerName, triggerGroup, cronExpression);
        return new ApiOutput();
    }

    @Log(value = LogEnum.info)
    @RequestMapping(value = "/removeJob", method = RequestMethod.POST, consumes = "application/json")
    public ApiOutput removeJob(@RequestBody String json) throws Exception {
        Map<String, Object> map = WebUtils.getJsonParams(json);
        String jobName = ArrayUtils.getMapString(map, "jobName");
        String jobGroup = ArrayUtils.getMapString(map, "jobGroup");
        String triggerGroup = ArrayUtils.getMapString(map, "triggerGroup");
        String triggerName = ArrayUtils.getMapString(map, "triggerName");
        this.schedulerService.removeJob(jobName, jobGroup, triggerName, triggerGroup);
        return new ApiOutput();
    }

    @Log(value = LogEnum.info)
    @RequestMapping(value = "/pauseJob", method = RequestMethod.POST, consumes = "application/json")
    public ApiOutput pauseJob(@RequestBody String json) throws Exception {
        Map<String, Object> map = WebUtils.getJsonParams(json);
        String jobName = ArrayUtils.getMapString(map, "jobName");
        String jobGroup = ArrayUtils.getMapString(map, "jobGroup");
        this.schedulerService.pauseJob(jobName, jobGroup);
        return new ApiOutput();
    }

    @Log(value = LogEnum.info)
    @RequestMapping(value = "/resumeJob", method = RequestMethod.POST, consumes = "application/json")
    public ApiOutput resumeJob(@RequestBody String json) throws Exception {
        Map<String, Object> map = WebUtils.getJsonParams(json);
        String jobName = ArrayUtils.getMapString(map, "jobName");
        String jobGroup = ArrayUtils.getMapString(map, "jobGroup");
        this.schedulerService.resumeJob(jobName, jobGroup);
        return new ApiOutput();
    }

}
