package net.risesoft.y9public.repository.resource;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.resource.Y9AppIcon;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9AppIconRepository extends JpaRepository<Y9AppIcon, String>, JpaSpecificationExecutor<Y9AppIcon> {

    public Y9AppIcon findByName(String name);

    public List<Y9AppIcon> findByNameContaining(String name);

    public Y9AppIcon findByPath(String path);

    public Page<Y9AppIcon> findByNameContaining(String name, Pageable pageable);

    public Y9AppIcon findTopByOrderByCreateTimeAsc();

}
