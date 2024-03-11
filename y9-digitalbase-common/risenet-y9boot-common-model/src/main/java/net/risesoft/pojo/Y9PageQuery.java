package net.risesoft.pojo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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
    private static final int MIN_SIZE = 1;
    // 分页查询时如果没有限制可能会造成的系统资源消耗漏洞
    private static final int MAX_SIZE = 200;

    /**
     * 页数
     */
    @NotNull @Min(value = DEFAULT_PAGE)
    private Integer page = DEFAULT_PAGE;

    /**
     * 每页的条数
     */
    @NotNull @Min(value = MIN_SIZE)
    @Max(value = MAX_SIZE)
    private Integer size = DEFAULT_SIZE;

    public int getPage4Db() {
        return page < 1 ? 0 : page - 1;
    }
}
