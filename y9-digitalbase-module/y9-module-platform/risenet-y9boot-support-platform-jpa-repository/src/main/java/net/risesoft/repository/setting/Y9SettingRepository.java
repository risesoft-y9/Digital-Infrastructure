package net.risesoft.repository.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.risesoft.entity.setting.Y9Setting;

/**
 * @author shidaobang
 * @date 2024/03/27
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9SettingRepository extends JpaRepository<Y9Setting, String> {

}
