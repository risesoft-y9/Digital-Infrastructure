package com.example;

import net.risesoft.model.Person;

/**
 * 人员同步
 *
 * @author shidaobang
 * @date 2022/12/29
 */
public class PersonUtil {

    /**
     * 判断数据库中是否存在对应的人员
     *
     * @param p
     * @return
     */
    public static boolean checkPersonExist(Person p) {
        boolean exist = false;
        // 写代码
        return exist;
    }

    /**
     * 同步人员
     *
     * @param p
     * @throws Exception
     */
    public static void syncPerson(Person p) throws Exception {
        boolean exist = checkPersonExist(p);
        if (exist) {
            // updatePerson(p);写自己的更新代码
        } else {
            // addPerson(p);写自己的新增代码
        }
    }

}
