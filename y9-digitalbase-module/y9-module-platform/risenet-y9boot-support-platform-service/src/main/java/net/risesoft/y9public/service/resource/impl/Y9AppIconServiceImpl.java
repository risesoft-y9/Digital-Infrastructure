package net.risesoft.y9public.service.resource.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.AppIcon;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9public.entity.Y9AppIcon;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.repository.Y9AppIconRepository;
import net.risesoft.y9public.service.Y9FileStoreService;
import net.risesoft.y9public.service.resource.Y9AppIconService;

@Service(value = "appIconService")
@Slf4j
@RequiredArgsConstructor
public class Y9AppIconServiceImpl implements Y9AppIconService {

    private final Y9AppIconRepository appIconRepository;

    private final Y9FileStoreService y9FileStoreService;

    private static AppIcon entityToModel(Y9AppIcon y9AppIcon) {
        return PlatformModelConvertUtil.convert(y9AppIcon, AppIcon.class);
    }

    private static List<AppIcon> entityToModel(List<Y9AppIcon> y9AppIconList) {
        return PlatformModelConvertUtil.convert(y9AppIconList, AppIcon.class);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(String id) {
        appIconRepository.deleteById(id);
    }

    @Override
    public Optional<AppIcon> findByNameAndColorType(String name, String colorType) {
        return appIconRepository.findByNameAndColorType(name, colorType).map(Y9AppIconServiceImpl::entityToModel);
    }

    @Override
    public AppIcon getById(String id) {
        return appIconRepository.findById(id)
            .map(Y9AppIconServiceImpl::entityToModel)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.APP_ICON_NOT_FOUND, id));
    }

    @Override
    public List<AppIcon> listAll() {
        List<Y9AppIcon> y9AppIconList = appIconRepository.findAll();
        return entityToModel(y9AppIconList);
    }

    @Override
    public List<AppIcon> listByName(String name) {
        return entityToModel(appIconRepository.findByName(name));
    }

    @Override
    public Y9Page<AppIcon> pageAll(Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Y9AppIcon> y9AppIconPage = appIconRepository.findAll(pageable);
        return Y9Page.success(pageQuery.getPage(), y9AppIconPage.getTotalPages(), y9AppIconPage.getTotalElements(),
            entityToModel(y9AppIconPage.getContent()));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void refreshAppIconData() {
        List<Y9AppIcon> apps = appIconRepository.findAll();
        for (Y9AppIcon y9AppIcon : apps) {
            try {
                byte[] b = y9FileStoreService.downloadFileToBytes(y9AppIcon.getPath());
                String iconData = Base64.getEncoder().encodeToString(b);
                y9AppIcon.setIconData(iconData);
                appIconRepository.save(y9AppIcon);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public AppIcon save(MultipartFile iconFile, String remark) throws Y9BusinessException {
        byte[] iconData = null;
        try {
            if (!iconFile.isEmpty()) {
                iconData = iconFile.getBytes();
            }
        } catch (IOException e1) {
            LOGGER.warn(e1.getMessage(), e1);
            throw new Y9BusinessException(500, "上传文件异常,错误信息为：" + e1.getMessage());
        }
        // 文件名称
        String originalFilename = iconFile.getOriginalFilename();
        // 图片名称
        String imgName = FilenameUtils.getName(originalFilename);
        // 文件类型
        String imgType = FilenameUtils.getExtension(imgName);
        List<Y9AppIcon> y9AppIconList = appIconRepository.findByName(imgName);
        Y9AppIcon appIcon = null;
        if (y9AppIconList.isEmpty()) {
            appIcon = new Y9AppIcon();
            appIcon.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            appIcon.setName(imgName);
        } else {
            appIcon = y9AppIconList.get(0);
        }
        appIcon.setRemark(remark);
        appIcon.setType(imgType);
        String fullPath = Y9FileStore.buildPath("riseplatform", "public", "appIcon");
        try {
            appIcon.setPath(y9FileStoreService.uploadFile(iconFile.getInputStream(), fullPath, imgName).getId());
            appIcon.setIconData(Base64.getEncoder().encodeToString(iconData));
            return entityToModel(appIconRepository.save(appIcon));
        } catch (Exception e) {
            LOGGER.warn("上传文件异常", e);
            throw new Y9BusinessException(500, "上传文件异常,错误信息为：" + e.getMessage());
        }

    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public AppIcon save(String name, String category, String colorType, String remark, MultipartFile iconFile)
        throws Y9BusinessException {
        byte[] iconData = null;
        try {
            if (!iconFile.isEmpty()) {
                iconData = iconFile.getBytes();
            }
        } catch (IOException e1) {
            LOGGER.warn(e1.getMessage(), e1);
            throw new Y9BusinessException(500, "上传文件异常,错误信息为：" + e1.getMessage());
        }
        if (StringUtils.isNotBlank(category)) {
            category = "default";
        }
        // 文件类型
        String imgType = FilenameUtils.getExtension(iconFile.getOriginalFilename());
        Optional<Y9AppIcon> y9AppIconOptional = appIconRepository.findByNameAndColorType(name, colorType);
        Y9AppIcon appIcon;
        if (y9AppIconOptional.isEmpty()) {
            appIcon = new Y9AppIcon();
            appIcon.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            appIcon.setName(name);
            appIcon.setColorType(colorType);
        } else {
            appIcon = y9AppIconOptional.get();
        }
        appIcon.setRemark(remark);
        appIcon.setType(imgType);
        appIcon.setCategory(category);
        String fullPath = Y9FileStore.buildPath("riseplatform", "public", "appIcon", category, colorType);
        try {
            appIcon.setPath(y9FileStoreService.uploadFile(iconFile.getInputStream(), fullPath, name).getId());
            appIcon.setIconData(Base64.getEncoder().encodeToString(iconData));
            return entityToModel(appIconRepository.save(appIcon));
        } catch (Exception e) {
            LOGGER.warn("上传文件异常", e);
            throw new Y9BusinessException(500, "上传文件异常,错误信息为：" + e.getMessage());
        }

    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void save(AppIcon appIcon) {
        Y9AppIcon y9AppIcon = PlatformModelConvertUtil.convert(appIcon, Y9AppIcon.class);
        appIconRepository.save(y9AppIcon);
    }

    @Override
    public Y9Page<AppIcon> pageByName(String name, Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Y9AppIcon> y9AppIconPage = appIconRepository.findByNameContaining(name, pageable);
        return Y9Page.success(pageQuery.getPage(), y9AppIconPage.getTotalPages(), y9AppIconPage.getTotalElements(),
            entityToModel(y9AppIconPage.getContent()));
    }
}
