package org.apereo.cas.web.y9.event.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class Y9MessageCommon implements Serializable {
    private static final long serialVersionUID = -1107265840539410276L;

    public static final String RefreshRemoteApplicationEvent = "RefreshRemoteApplicationEvent";

    private Serializable eventObject;
    private String eventType;
}