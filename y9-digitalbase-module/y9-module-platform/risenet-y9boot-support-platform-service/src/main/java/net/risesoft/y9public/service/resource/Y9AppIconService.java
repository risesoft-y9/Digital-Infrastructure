package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.model.platform.AppIcon;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9AppIconService {

    /**
     * 删除图标
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 根据名字获取应用图标
     *
     * @param name 图标名称
     * @param colorType 颜色类型
     * @return {@code Optional<AppIcon>}
     */
    Optional<AppIcon> findByNameAndColorType(String name, String colorType);

    /**
     * 根据 id 获取应用图标
     *
     * @param id id
     * @return {@link AppIcon}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    AppIcon getById(String id);

    /**
     * 查询所有图标
     *
     * @return {@code List<AppIcon>}
     */
    List<AppIcon> listAll();

    /**
     * 根据名称查询应用图标列表
     *
     * @param name 图标名称
     * @return {@code List<AppIcon>}
     */
    List<AppIcon> listByName(String name);

    /**
     * 分页获取图标列表
     *
     * @param pageQuery 分页查询参数
     * @return {@code Page<AppIcon>}
     */
    Y9Page<AppIcon> pageAll(Y9PageQuery pageQuery);

    /**
     * 刷新图标数据
     */
    void refreshAppIconData();

    /**
     * 保存应用程序图标
     *
     * @param iconFile 图标文件
     * @param remark 备注
     * @return {@link AppIcon}
     * @throws Y9BusinessException 业务异常
     * @see Y9BusinessException
     */
    AppIcon save(MultipartFile iconFile, String remark) throws Y9BusinessException;

    /**
     * 保存应用程序图标
     *
     * @param name 图标名称
     * @param category 类别
     * @param colorType 颜色类别
     * @param remark 备注
     * @param iconFile 图标文件
     * @return {@link AppIcon}
     * @throws Y9BusinessException 业务异常
     */
    AppIcon save(String name, String category, String colorType, String remark, MultipartFile iconFile)
        throws Y9BusinessException;

    /**
     * 保存图标
     *
     * @param appIcon 应用图标
     */
    void save(AppIcon appIcon);

    /**
     * 根据名字分页查询图标
     *
     * @param name 图标名称
     * @param pageQuery 分页查询参数
     * @return {@code Page<AppIcon>}
     */
    Y9Page<AppIcon> pageByName(String name, Y9PageQuery pageQuery);
}
