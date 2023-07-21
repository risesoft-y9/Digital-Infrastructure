package org.apereo.cas.web.y9.event.listener.cas;

import org.apereo.cas.support.events.audit.CasAuditActionContextRecordedEvent;
import org.apereo.inspektr.audit.AuditActionContext;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CasTicketEventListener {

    @EventListener
    public void casAuditActionContextRecordedEventEventListener(CasAuditActionContextRecordedEvent event) {
        try {
            AuditActionContext auditActionContext = event.getAuditActionContext();
            LOGGER.info("AuditActionContext:{}", auditActionContext);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
