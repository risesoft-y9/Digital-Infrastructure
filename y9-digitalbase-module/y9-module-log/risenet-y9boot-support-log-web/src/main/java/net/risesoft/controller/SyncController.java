package net.risesoft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9public.service.Y9CommonAppForPersonService;

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

}
