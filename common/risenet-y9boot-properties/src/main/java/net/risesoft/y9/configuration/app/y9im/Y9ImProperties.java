package net.risesoft.y9.configuration.app.y9im;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9ImProperties {

    private String systemName = "riseim";

    private String boshService;

    private Integer historyPageNo = 0;

    private Integer historyPageSize = 10;

    private String historyOrder = "desc";

    private String msgIp;

    private String groupIp;

    private String uploadUrl;

    private String downloadUrl;

}
