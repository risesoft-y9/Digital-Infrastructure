package y9.controller.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Tenant implements Serializable {

    private static final long serialVersionUID = -94957634199776609L;

    public static Tenant OPERATION_TENANT = new Tenant("运维", "operation");

    private final String tenantName;

    private final String tenantShortName;

}
