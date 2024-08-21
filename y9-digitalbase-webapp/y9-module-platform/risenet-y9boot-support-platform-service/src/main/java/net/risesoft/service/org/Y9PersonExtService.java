package net.risesoft.service.org;

import java.util.Optional;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonExtService {

    /**
     * 根据人员id，查找人员扩展信息
     *
     * @param personId 人员id
     * @return {@code Optional<Y9PersonExt>}
     */
    Optional<Y9PersonExt> findByPersonId(String personId);

    /**
     * 根据主键id获取人员扩展实例
     *
     * @param id 唯一标识
     * @return {@link Y9PersonExt}
     */
    Y9PersonExt getById(String id);

    /**
     * 根据人员主键id获取人员扩展实例
     *
     * @param personId 人员唯一标识
     * @return {@link Y9PersonExt}
     */
    Y9PersonExt getByPersonId(String personId);

    /**
     * 根据personId查询
     *
     * @param personId 人员id
     * @return {@link String}
     */
    String getEncodePhotoByPersonId(String personId);

    /**
     * 获取人员头像信息
     *
     * @param personId 人员id
     * @return {@link byte[]}
     */
    byte[] getPhotoByPersonId(String personId);

    /**
     * 保存/更新
     *
     * @param y9PersonExt 人员扩展信息对象
     * @param person 人员对象
     * @return {@link Y9PersonExt}
     */
    Y9PersonExt saveOrUpdate(Y9PersonExt y9PersonExt, Y9Person person);

    /**
     * 保存个人头像信息
     *
     * @param personId 人员id
     * @param photo 照片的base64
     * @return {@link Y9PersonExt}
     */
    Y9PersonExt savePersonPhoto(String personId, String photo);

    /**
     * 保存个人头像信息
     *
     * @param person 人员对象
     * @param photo 照片字节数组
     * @return {@link Y9PersonExt}
     */
    Y9PersonExt savePersonPhoto(Y9Person person, byte[] photo);

    /**
     * 保存个人头像信息
     *
     * @param person 人员对象
     * @param photo 照片的base64
     * @return {@link Y9PersonExt}
     */
    Y9PersonExt savePersonPhoto(Y9Person person, String photo);

    /**
     * 保存个人签名信息
     *
     * @param person 人员对象
     * @param sign 签名字节数组
     * @return {@link Y9PersonExt}
     */
    Y9PersonExt savePersonSign(Y9Person person, byte[] sign);

    /**
     * 保存个人签名信息
     *
     * @param person 人员对象
     * @param sign 签名的base64
     * @return {@link Y9PersonExt}
     */
    Y9PersonExt savePersonSign(Y9Person person, String sign);
}
