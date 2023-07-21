package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9FileStore;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9FileStoreRepository extends JpaRepository<Y9FileStore, String> {

    public List<Y9FileStore> findByRealFileNameIsNull();

    public Y9FileStore getByFullPathAndFileName(String fullPath, String fileName);

}
