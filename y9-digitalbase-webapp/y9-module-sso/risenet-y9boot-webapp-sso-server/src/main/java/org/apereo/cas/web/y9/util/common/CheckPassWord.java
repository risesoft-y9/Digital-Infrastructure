package org.apereo.cas.web.y9.util.common;

public class CheckPassWord {
    private static String[] simplePassWordArray = new String[] {"000000", "111111", "222222", "333333", "444444",
        "555555", "666666", "777777", "888888", "999999", "123456"};

    public static boolean isSimplePassWord(String password) {
        for (int i = 0; i < simplePassWordArray.length; i++) {
            if (simplePassWordArray[i].equals(password)) {
                return true;
            }
        }
        return false;
    }
}
