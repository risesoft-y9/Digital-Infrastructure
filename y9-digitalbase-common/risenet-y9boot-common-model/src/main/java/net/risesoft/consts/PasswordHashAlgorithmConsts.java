package net.risesoft.consts;

/** 密码哈希算法标识，与数据库 PASSWORD_HASH_ALGORITHM 列中的值对应。 */
public final class PasswordHashAlgorithmConsts {

    public static final String BCRYPT = "bcrypt";

    private PasswordHashAlgorithmConsts() {
        throw new IllegalStateException("PasswordHashAlgorithmConsts class");
    }
}
