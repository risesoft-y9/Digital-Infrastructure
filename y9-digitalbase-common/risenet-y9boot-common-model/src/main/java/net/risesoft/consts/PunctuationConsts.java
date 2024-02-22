package net.risesoft.consts;

/**
 * 标点符号
 *
 * @author qinman
 * @date 2022/4/21
 */
public class PunctuationConsts {

    /**
     * 逗号
     */
    public static final String COMMA = ",";

    /**
     * 冒号
     */
    public static final String COLON = ":";
    /**
     * 分号
     */
    public static final String SEMICOLON = ";";
    /**
     * 等号
     */
    public static final String EQUAL_SIGN = "=";
    /**
     * 单引号
     */
    public static final String SINGLE_QUOTE_MARK = "'";
    /**
     * 六角括号-左
     */
    public static final String LEFTHEXBRACKETS = "〔";

    /**
     * 六角括号-右
     */
    public static final String RIGHTHEXBRACKETS = "〕";

    private PunctuationConsts() {
        throw new IllegalStateException("PunctuationConsts class");
    }
}
