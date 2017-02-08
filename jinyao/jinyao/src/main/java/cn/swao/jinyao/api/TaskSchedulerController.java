package cn.swao.jinyao.api;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.google.common.base.*;

import cn.swao.baselib.util.ArrayUtils;
import cn.swao.framework.api.ApiOutput;
import cn.swao.framework.util.WebUtils;
import cn.swao.jinyao.sys.TaskScheduler;

@RestController
@RequestMapping("/taskScheduler")
public class TaskSchedulerController {
    @Autowired
    private TaskScheduler taskScheduler;

    @RequestMapping(value = "/doScheduler", method = RequestMethod.POST, consumes = "application/json")
    public ApiOutput doScheduler(@RequestBody String json) throws Exception {
        Map map = WebUtils.getJsonParams(json);
        String method = ArrayUtils.getMapString(map, "method");
        Preconditions.checkNotNull(method, "method不为空");
        Iterator<String> it = Splitter.on(",").split(method).iterator();
        while (it.hasNext()) {
            taskScheduler.doScheduleManual(it.next());
        }
        return new ApiOutput();
    }

}
