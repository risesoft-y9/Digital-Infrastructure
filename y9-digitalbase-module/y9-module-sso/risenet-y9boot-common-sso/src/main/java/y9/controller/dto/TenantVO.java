package y9.controller.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TenantVO implements Serializable {

    private static final long serialVersionUID = -94957634199776609L;

    public static TenantVO operationTenantVO = new TenantVO("运维", "operation", null, null);

    private final String tenantName;

    private final String tenantShortName;

    private final String logoIcon;

    private final String description;

}
