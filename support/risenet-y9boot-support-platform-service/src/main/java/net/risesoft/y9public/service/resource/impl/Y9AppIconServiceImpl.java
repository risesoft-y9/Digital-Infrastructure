package net.risesoft.y9public.service.resource.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.entity.resource.Y9AppIcon;
import net.risesoft.y9public.repository.resource.Y9AppIconRepository;
import net.risesoft.y9public.service.Y9FileStoreService;
import net.risesoft.y9public.service.resource.Y9AppIconService;

import jodd.util.Base64;

@Service(value = "appIconService")
@Slf4j
@RequiredArgsConstructor
public class Y9AppIconServiceImpl implements Y9AppIconService {

    private final Y9AppIconRepository appIconRepository;

    private final Y9FileStoreService y9FileStoreService;

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9AppIcon appIcon = this.findById(id);
        if (appIcon != null) {
            appIconRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Y9AppIcon appIcon) {
        appIconRepository.delete(appIcon);
    }

    @Override
    public Y9AppIcon findById(String id) {
        return appIconRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Y9AppIcon> findByName(String name) {
        return appIconRepository.findByName(name);
    }

    @Override
    public List<Y9AppIcon> listAll() {
        return appIconRepository.findAll();
    }

    @Override
    public List<Y9AppIcon> listByName(String name) {
        return appIconRepository.findByNameContaining(name);
    }

    @Override
    public Page<Y9AppIcon> pageAll(int page, int rows) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.DESC, "createTime"));
        return appIconRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void refreshAppIconData() {
        List<Y9AppIcon> apps = appIconRepository.findAll();
        for (Y9AppIcon appicon : apps) {
            try {
                byte[] b = y9FileStoreService.downloadFileToBytes(appicon.getPath());
                String iconData = Base64.encodeToString(b);
                appicon.setIconData(iconData);
                appIconRepository.save(appicon);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9AppIcon save(MultipartFile iconFile, String remark) throws Exception {
        byte[] iconData = null;
        try {
            iconData = iconFile.getBytes();
        } catch (IOException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        }
        // 文件名称
        String originalFilename = iconFile.getOriginalFilename();
        // 图片名称
        String imgName = FilenameUtils.getName(originalFilename);
        // 文件类型
        String imgType = FilenameUtils.getExtension(imgName);
        Optional<Y9AppIcon> y9AppIconOptional = appIconRepository.findByName(imgName);
        if (y9AppIconOptional.isEmpty()) {
            Y9AppIcon appIcon = new Y9AppIcon();
            appIcon.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            appIcon.setName(imgName);
        }
        Y9AppIcon appIcon = y9AppIconOptional.get();
        appIcon.setRemark(remark);
        appIcon.setType(imgType);
        String fullPath = Y9FileStore.buildFullPath("riseplatform", "public", "appIcon");
        appIcon.setPath(y9FileStoreService.uploadFile(iconFile, fullPath, imgName).getId());
        appIcon.setIconData(Base64.encodeToString(iconData));
        return appIconRepository.save(appIcon);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9AppIcon appIcon) {
        appIconRepository.save(appIcon);
    }

    @Override
    public Page<Y9AppIcon> searchByName(int page, int rows, String name) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.DESC, "createTime"));
        return appIconRepository.findByNameContaining(name, pageable);
    }
}
