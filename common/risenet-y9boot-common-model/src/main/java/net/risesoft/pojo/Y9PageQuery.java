package net.risesoft.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 分页查询参数
 *
 * @author shidaobang
 * @date 2022/3/3
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Y9PageQuery {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    /**
     * 页数
     */
    private int page = DEFAULT_PAGE;
    /**
     * 每页的条数
     */
    private int size = DEFAULT_SIZE;

    public int getPage4Db() {
        return page < 1 ? 0 : page - 1;
    }
}
