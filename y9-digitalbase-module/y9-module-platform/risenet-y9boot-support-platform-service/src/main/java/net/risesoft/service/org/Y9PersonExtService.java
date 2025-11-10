package net.risesoft.service.org;

import java.util.Optional;

import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonExt;

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
     * @return {@code Optional<PersonExt>}
     */
    Optional<PersonExt> findByPersonId(String personId);

    /**
     * 根据主键id获取人员扩展实例
     *
     * @param id 唯一标识
     * @return {@link PersonExt}
     */
    PersonExt getById(String id);

    /**
     * 根据人员主键id获取人员扩展实例
     *
     * @param personId 人员唯一标识
     * @return {@link PersonExt}
     */
    PersonExt getByPersonId(String personId);

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
     * 保存个人头像信息
     *
     * @param person 人员对象
     * @param photo 照片字节数组
     * @return {@link PersonExt}
     */
    PersonExt savePersonPhoto(Person person, byte[] photo);

    /**
     * 保存个人头像信息
     *
     * @param person 人员对象
     * @param photo 照片的base64
     * @return {@link PersonExt}
     */
    PersonExt savePersonPhoto(Person person, String photo);

    /**
     * 保存个人签名信息
     *
     * @param person 人员对象
     * @param sign 签名字节数组
     * @return {@link PersonExt}
     */
    PersonExt savePersonSign(Person person, byte[] sign);

    /**
     * 保存个人签名信息
     *
     * @param person 人员对象
     * @param sign 签名的base64
     * @return {@link PersonExt}
     */
    PersonExt savePersonSign(Person person, String sign);
}
