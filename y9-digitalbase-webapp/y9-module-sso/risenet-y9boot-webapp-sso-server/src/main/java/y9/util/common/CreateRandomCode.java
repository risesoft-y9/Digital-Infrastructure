package y9.util.common;

public class CreateRandomCode {

    /**
     * 随机生成指定长度的验证码
     * 
     * @param codelength
     * @return
     */
    public static String create(int codelength) {
        String vcode = "";
        for (int i = 0; i < codelength; i++) {
            vcode = vcode + (int)(Math.random() * 9);
        }
        return vcode;
    }
}
