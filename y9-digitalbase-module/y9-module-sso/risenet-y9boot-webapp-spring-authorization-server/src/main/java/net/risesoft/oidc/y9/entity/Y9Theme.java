package net.risesoft.oidc.y9.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = Y9Theme.ENTITY_NAME)
@Table(name = "Y9_THEME", indexes = {@Index(columnList = "client_id")})
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@Slf4j
@Accessors(chain = true)
public class Y9Theme implements Serializable {
    public static final String ENTITY_NAME = "Y9Theme";

    @Serial
    private static final long serialVersionUID = 8188041534553034382L;

    @Id
    @Column(name = "client_id", length = 100, nullable = false)
    private String clientId;

    @Column(name = "theme", length = 100, nullable = true)
    private String theme;

}
