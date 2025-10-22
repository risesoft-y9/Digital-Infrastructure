package net.risesoft.y9public.repository.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.y9public.entity.resource.Y9AppIcon;

public interface Y9AppIconRepository extends JpaRepository<Y9AppIcon, String>, JpaSpecificationExecutor<Y9AppIcon> {

    List<Y9AppIcon> findByName(String name);

    List<Y9AppIcon> findByNameContaining(String name);

    Page<Y9AppIcon> findByNameContaining(String name, Pageable pageable);

    Optional<Y9AppIcon> findByNameAndColorType(String name, String colorType);

}
