package net.risesoft.exception;

/**
 * 错误码接口<br>
 * 错误码的枚举类应实现这个接口，smart-doc 可以轻松将错误码导出到文档中<br>
 * <p>
 * 10000 以下 code 的保留，作为全局的系统错误码<br>
 * 其他系统可以按照 {两位系统码}-{两位模块码}-{两位错误序号} 进行编码<br>
 *
 * @author shidaobang
 * @date 2022/11/10
 * @since 9.6.0
 */
public interface ErrorCode {


    /**
     * 获取错误码 <br>
     * 由{两位系统码}-{两位模块码}-{两位错误序号}组成
     *
     * @return int
     */
    default int getCode() {
        String moduleCodeStr = String.format("%02d", moduleCode());
        String errorIndexStr = String.format("%02d", moduleErrorCode());
        return Integer.parseInt(systemCode() + moduleCodeStr + errorIndexStr);
    }


    /**
     * 系统代码<br>
     * 返回的值必须大于0
     *
     * @return int
     */
    int systemCode();

    int moduleCode();

    int moduleErrorCode();

    /**
     * 得到错误描述
     *
     * @return {@link String}
     */
    String getDescription();
    
}
