package y9.client.platform.log;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.log.ClickedAppApi;
import net.risesoft.model.ClickedApp;

/**
 * 应用点击组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@FeignClient(contextId = "ClickedAppApiClient", name = "clickedApp", url = "${y9.common.logBaseUrl}", path = "/services/rest/clickedApp")
public interface ClickedAppApiClient extends ClickedAppApi {

    /**
     * 保存点击的图标的人员Id和应用名称等信息
     *
     * @param clickedApp 应用点击详情
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveClickedAppLog")
    boolean saveClickedAppLog(@SpringQueryMap ClickedApp clickedApp);

    /**
     * 保存点击的图标的人员Id和应用名称等信息
     *
     * @param clickedAppJson 应用点击Json字符串
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveClickedAppLogByJson")
    boolean saveClickedAppLogByJson(@RequestParam("clickedAppJson") String clickedAppJson);
}
