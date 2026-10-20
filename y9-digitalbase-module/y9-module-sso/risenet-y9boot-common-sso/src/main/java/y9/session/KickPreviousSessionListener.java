package y9.session;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.support.events.ticket.CasServiceTicketGrantedEvent;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.jooq.lambda.Unchecked;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.Y9Properties;

/**
 * 特定服务一个用户只允许单设备在线（互踢）
 *
 * @author shidaobang
 * @date 2026/10/20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KickPreviousSessionListener {

    private final TicketRegistry ticketRegistry;

    private final Y9Properties y9Properties;

    @EventListener
    public void onTicketGrantingTicketCreated(final CasServiceTicketGrantedEvent event) {
        String originalUrl = event.getServiceTicket().getService().getOriginalUrl();
        if (StringUtils.equals(y9Properties.getSingleSessionServiceUrl(), originalUrl)) {
            TicketGrantingTicket tgt = event.getTicketGrantingTicket();
            String principalId = tgt.getAuthentication().getPrincipal().getId();
            kickOthers(tgt.getId(), principalId);
        }
    }

    private void kickOthers(final String tgtId, final String principalId) {
        List<TicketGrantingTicket> others = ticketRegistry.getSessionsFor(principalId)
            .filter(TicketGrantingTicket.class::isInstance)
            .map(TicketGrantingTicket.class::cast)
            .filter(t -> !t.getId().equals(tgtId))
            .map(ticket -> ticketRegistry.getTicket(ticket.getId(), TicketGrantingTicket.class))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        others.forEach(tgt -> {
            try {
                tgt.getDescendantTickets().forEach(Unchecked.consumer(ticketId -> {
                    LOGGER.debug("Deleting ticket [{}] from the registry as a descendant of [{}]", ticketId, tgt.getId());
                    ticketRegistry.deleteTicket(ticketId);
                }));
                ticketRegistry.deleteTicket(tgt.getId());
            } catch (Exception e) {
                LOGGER.error("Failed to kick previous sessions for principal [{}]", principalId, e);
            }
        });
    }

}