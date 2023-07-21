package net.risesoft.pojo;

import java.io.Serializable;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.risesoft.exception.GlobalErrorCodeEnum;

/**
 * 分页请求通用返回
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9Page<T> implements Serializable {

    private static final long serialVersionUID = 6789650187574079674L;

    public static <T> Y9Page<T> success(int currPage, int totalPages, long total, List<T> rows) {
        return success(currPage, totalPages, total, rows, GlobalErrorCodeEnum.SUCCESS.getDescription());
    }

    public static <T> Y9Page<T> success(int currPage, int totalPages, long total, List<T> rows, String msg) {
        return new Y9Page<>(currPage, totalPages, total, rows, GlobalErrorCodeEnum.SUCCESS.getCode(), msg, Boolean.TRUE);
    }

    public static <T> Y9Page<T> failure(int currPage, int totalPages, long total, List<T> rows, String msg, long code) {
        return new Y9Page<>(currPage, totalPages, total, rows, code, msg, Boolean.FALSE);
    }

    /**
     * 当前页
     */
    private int currPage;

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 总条数
     */
    private long total;

    /**
     * 数据项
     */
    private List<T> rows;

    /**
     * 错误代码
     */
    private long code;

    /**
     * 调用信息
     */
    private String msg;

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     *
     *
     * @deprecated (对于大多数不需要关注 code 的情况下，推荐直接静态方法创建对象，而不是通过新建对象然后设值 直接静态方法创建对象可以统一 code ，方便 code 的维护)
     */
    @Deprecated
    public Y9Page() {

    }
}
