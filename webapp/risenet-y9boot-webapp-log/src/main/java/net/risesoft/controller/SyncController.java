package net.risesoft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.service.Y9CommonAppForPersonService;

@RestController
@RequestMapping(value = "/sync")
@RequiredArgsConstructor
public class SyncController {

    private final Y9CommonAppForPersonService commonAppForPersonService;

    /**
     * 半年应用点击次数
     *
     * @return long 点击次数
     * @since 9.6.0
     */
    @GetMapping(value = "/getCount")
    public long getCount() {
        return commonAppForPersonService.getCount();
    }

    /**
     * 保存常用应用
     *
     * @return String 操作结果
     */
    @PostMapping(value = "/save")
    public String saveCommonApp() {
        return commonAppForPersonService.saveForQuery();
    }

    /**
     * 同步近半年的数据
     *
     * @return String 操作结果
     */
    @PostMapping(value = "/syncData")
    public String syncData() {
        return commonAppForPersonService.syncData();
    }
}
