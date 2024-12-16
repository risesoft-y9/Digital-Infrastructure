package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * CompositeResourceManager
 *
 * @author shidaobang
 * @date 2024/12/12
 * @since 9.6.8
 */
public interface CompositeResourceManager {

    Y9Menu findMenuById(String id);

    Y9Operation findOperationById(String id);

    Y9App findAppById(String id);

    Y9DataCatalog findDataCatalogById(String id);

    Y9ResourceBase getResourceAsParent(String resourceId);

    Optional<Y9ResourceBase> findResource(String id);
}
