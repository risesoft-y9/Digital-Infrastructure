package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.resource.Y9AppIcon;

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
     * 删除选定的图标
     *
     * @param appIcon 应用图标
     */
    void delete(Y9AppIcon appIcon);

    /**
     * 根据id，获取应用图标
     *
     * @param id 唯一标识
     * @return {@code Optional<Y9AppIcon>}应用图标对象 或 null
     */
    Optional<Y9AppIcon> findById(String id);

    /**
     * 根据名字获取应用图标
     *
     * @param name 图标名称
     * @return {@code Optional<Y9AppIcon>}
     */
    Optional<Y9AppIcon> findByNameAndColorType(String name, String colorType);

    /**
     * 根据 id 获取应用图标
     *
     * @param id id
     * @return {@link Y9AppIcon}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9AppIcon getById(String id);

    /**
     * 查询所有图标
     *
     * @return {@code List<Y9AppIcon>}
     */
    List<Y9AppIcon> listAll();

    /**
     * 根据名称查询应用图标列表
     *
     * @param name 图标名称
     * @return {@code List<Y9AppIcon>}
     */
    List<Y9AppIcon> listByName(String name);

    /**
     * 根据名称查询应用图标列表
     *
     * @param name 图标名称
     * @return {@code List<Y9AppIcon>}
     */
    List<Y9AppIcon> listByNameLike(String name);

    /**
     * 分页获取图标列表
     *
     * @param pageQuery 分页查询参数
     * @return {@code Page<Y9AppIcon>}
     */
    Page<Y9AppIcon> pageAll(Y9PageQuery pageQuery);

    /**
     * 刷新图标数据
     */
    void refreshAppIconData();

    /**
     * 保存应用程序图标
     *
     * @param iconFile 图标文件
     * @param remark 备注
     * @return {@link Y9AppIcon}
     * @throws Y9BusinessException 业务异常
     * @see Y9BusinessException
     */
    Y9AppIcon save(MultipartFile iconFile, String remark) throws Y9BusinessException;

    /**
     * 保存应用程序图标
     *
     * @param name 图标名称
     * @param category 类别
     * @param colorType 颜色类别
     * @param remark 备注
     * @param iconFile 图标文件
     * @return {@link Y9AppIcon}
     * @throws Y9BusinessException 业务异常
     */
    Y9AppIcon save(String name, String category, String colorType, String remark, MultipartFile iconFile)
        throws Y9BusinessException;

    /**
     * 保存图标
     *
     * @param appIcon 应用图标
     */
    void save(Y9AppIcon appIcon);

    /**
     * 根据名字分页查询图标
     *
     * @param name 图标名称
     * @param pageQuery 分页查询参数
     * @return {@code Page<Y9AppIcon>}
     */
    Page<Y9AppIcon> searchByName(String name, Y9PageQuery pageQuery);
}
