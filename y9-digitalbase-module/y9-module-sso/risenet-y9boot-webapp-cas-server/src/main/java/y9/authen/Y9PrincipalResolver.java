package y9.authen;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.attribute.SimplePersonAttributes;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.*;
import org.apereo.cas.authentication.principal.attribute.PersonAttributes;

import org.apereo.cas.util.CollectionUtils;
import y9.entity.Y9User;
import y9.service.Y9UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Y9PrincipalResolver implements PrincipalResolver {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean isReturnNullIfNoAttributes = false;

    private PrincipalFactory principalFactory = new DefaultPrincipalFactory();

    private final Y9UserService y9UserService;

    public Y9PrincipalResolver(Y9UserService y9UserService) {
        this.y9UserService = y9UserService;
    }

    @Override
    public Principal resolve(Credential credential,
                             Optional<Principal> principal,
                             Optional<AuthenticationHandler> handler,
                             Optional<Service> service) throws Throwable {
        UsernamePasswordCredential riseCredential = (UsernamePasswordCredential)credential;
        String username = riseCredential.getUsername();
        Map<String, Object> customFields = riseCredential.getCustomFields();
        String tenantShortName = (String) customFields.get("tenantShortName");
        String deptId = (String) customFields.get("deptId");
        String positionId = (String) customFields.get("positionId");
        String loginType = (String) customFields.get("loginType");

        if (username == null) {
            logger.debug("Got null for extracted principal ID; returning null.");
            return null;
        }

        PersonAttributes personAttributes = null;

        List<Y9User> users = null;
        if ("mobile".equals(loginType)) {
            if (StringUtils.hasText(deptId)) {
                users = y9UserService.findByTenantShortNameAndMobileAndParentId(tenantShortName, username, deptId);
            } else {
                users = y9UserService.findByTenantShortNameAndMobileAndOriginal(tenantShortName, username, Boolean.TRUE);
            }
        } else {
            if (StringUtils.hasText(deptId)) {
                users = y9UserService.findByTenantShortNameAndLoginNameAndParentId(tenantShortName, username, deptId);
            } else {
                users = y9UserService.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, username, Boolean.TRUE);
            }
        }
        if (users != null && !users.isEmpty()) {
            Y9User y9User = users.getFirst();
            val attr = new HashMap<String, List<Object>>();
            attr.put("tenantId", Lists.newArrayList((Object)y9User.getTenantId()));
            attr.put("tenantShortName", Lists.newArrayList((Object)tenantShortName));
            attr.put("tenantName", Lists.newArrayList((Object)y9User.getTenantName()));
            attr.put("personId", Lists.newArrayList((Object)y9User.getPersonId()));
            attr.put("loginName", Lists.newArrayList((Object)username));
            attr.put("sex", Lists.newArrayList((Object)y9User.getSex() == null ? 0 : y9User.getSex()));
            attr.put("caid", Lists.newArrayList(y9User.getCaid() == null ? "" : y9User.getCaid()));
            attr.put("email", Lists.newArrayList(y9User.getEmail() == null ? "" : y9User.getEmail()));
            attr.put("mobile", Lists.newArrayList(y9User.getMobile() == null ? "" : y9User.getMobile()));
            attr.put("guidPath", Lists.newArrayList(y9User.getGuidPath() == null ? "" : y9User.getGuidPath()));
            attr.put("dn", Lists.newArrayList(y9User.getDn() == null ? "" : y9User.getDn()));
            attr.put("loginType", Lists.newArrayList((Object)loginType));

            attr.put("name", Lists.newArrayList((Object)y9User.getName()));
            attr.put("parentId", Lists.newArrayList(y9User.getParentId() == null ? "" : y9User.getParentId()));
            attr.put("idNum", Lists.newArrayList(y9User.getIdNum() == null ? "" : y9User.getIdNum()));
            attr.put("avator", Lists.newArrayList(y9User.getAvator() == null ? "" : y9User.getAvator()));
            attr.put("personType", Lists.newArrayList(y9User.getPersonType() == null ? "" : y9User.getPersonType()));

            attr.put("password", Lists.newArrayList(y9User.getPassword() == null ? "" : y9User.getPassword()));
            attr.put("original", Lists.newArrayList(y9User.getOriginal() == null ? true : y9User.getOriginal()));
            attr.put("originalId", Lists.newArrayList(y9User.getOriginalId() == null ? "" : y9User.getOriginalId()));
            attr.put("globalManager", Lists.newArrayList(y9User.getGlobalManager() == null ? false : y9User.getGlobalManager()));
            attr.put("managerLevel", Lists.newArrayList(y9User.getManagerLevel() == null ? 0 : y9User.getManagerLevel()));
            attr.put("positions", Lists.newArrayList(y9User.getPositions() == null ? "" : y9User.getPositions()));

            if (StringUtils.hasText(positionId)) {
                attr.put("positionId", Lists.newArrayList(positionId));
            }
            if (StringUtils.hasText(deptId)) {
                attr.put("deptId", Lists.newArrayList(deptId));
            }

            personAttributes = new SimplePersonAttributes(username, attr);
        }

        final Map<String, List<Object>> attributes;

        if (personAttributes == null) {
            attributes = null;
        } else {
            attributes = personAttributes.getAttributes();
        }

        if (attributes == null & !isReturnNullIfNoAttributes) {
            return principalFactory.createPrincipal(username);
        }

        if (attributes == null) {
            return null;
        }

        val pair = convertPersonAttributesToPrincipal(username, attributes);
        return principalFactory.createPrincipal(pair.getKey(), pair.getValue());
    }

    /**
     * Convert person attributes to principal pair.
     *
     * @param extractedPrincipalId the extracted principal id
     * @param attributes the attributes
     * @return the pair
     */
    @SuppressWarnings("unchecked")
    protected Pair<String, Map<String, List<Object>>> convertPersonAttributesToPrincipal(
        final String extractedPrincipalId, final Map<String, List<Object>> attributes) {
        val convertedAttributes = new LinkedHashMap<String, List<Object>>();
        attributes.forEach((key, attrValue) -> {
            val values = ((List<Object>)CollectionUtils.toCollection(attrValue, ArrayList.class)).stream()
                .filter(Objects::nonNull).collect(toList());
            LOGGER.debug("Found attribute [{}] with value(s) [{}]", key, values);
            convertedAttributes.put(key, values);
        });

        var principalId = extractedPrincipalId;

        val attrNames = StringUtils.commaDelimitedListToSet("username");
        val result =
            attrNames.stream().map(String::trim).filter(attributes::containsKey).map(attributes::get).findFirst();

        if (result.isPresent()) {
            val values = result.get();
            if (!values.isEmpty()) {
                principalId = CollectionUtils.firstElement(values).get().toString();
                LOGGER.debug("Found principal id attribute value [{}] and removed it from the collection of attributes",
                    principalId);
            }
        } else {
            LOGGER.warn(
                "Principal resolution is set to resolve the authenticated principal via attribute(s) [username], and yet "
                    + "the collection of attributes retrieved [{}] do not contain any of those attributes. This is likely due to misconfiguration "
                    + "and CAS will switch to use [{}] as the final principal id",
                attributes.keySet(), principalId);
        }

        return Pair.of(principalId, convertedAttributes);
    }

    @Override
    public boolean supports(final Credential credential) {
        return credential instanceof UsernamePasswordCredential;
    }

}
