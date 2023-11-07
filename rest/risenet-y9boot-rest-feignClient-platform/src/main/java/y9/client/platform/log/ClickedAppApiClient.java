package y9.client.platform.log;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

import net.risesoft.api.log.ClickedAppApi;
import net.risesoft.model.ClickedApp;
import net.risesoft.pojo.Y9Result;

/**
 * 应用点击组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@FeignClient(contextId = "ClickedAppApiClient", name = "clickedApp", url = "${y9.common.logBaseUrl}",
    path = "/services/rest/v1/clickedApp")
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
    Y9Result<Object> saveClickedAppLog(@SpringQueryMap ClickedApp clickedApp);
}
