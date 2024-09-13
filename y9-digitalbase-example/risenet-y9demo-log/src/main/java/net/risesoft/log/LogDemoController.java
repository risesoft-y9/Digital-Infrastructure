package net.risesoft.log;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.log.annotation.RiseLog;

@RestController
public class LogDemoController {

    @GetMapping
    @RiseLog(operationName = "测试记录日志", moduleName = "日志记录演示系统")
    public String log() {
        return "log success";
    }
}
