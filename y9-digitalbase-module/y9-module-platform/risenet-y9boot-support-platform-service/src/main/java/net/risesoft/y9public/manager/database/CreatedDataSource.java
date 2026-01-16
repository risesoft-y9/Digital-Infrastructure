package net.risesoft.y9public.manager.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据源信息
 *
 * @author shidaobang
 * @date 2026/01/16
 */
@Getter
@Setter
@AllArgsConstructor
public class CreatedDataSource {

    private String url;

    private String username;

    private String password;

}
