package net.risesoft.model.subscription;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 文章推荐
 *
 * @author shidaobang
 * @date 2022/12/29
 */
@Data
public class RecommendedModel implements Serializable {

    @AllArgsConstructor
    @Getter
    public enum CheckedStatus {
        /** 未审核 */
        NOT_CHECKED(0, "未审核"),
        /** 审核通过 */
        PASSED(1, "审核通过"),
        /** 审核不通过 */
        NOT_PASSED(-1, "审核不通过");

        public static String getNameByValue(Integer value) {
            for (CheckedStatus checkedStatus : CheckedStatus.values()) {
                if (checkedStatus.getValue().equals(value)) {
                    return checkedStatus.getName();
                }
            }
            return null;
        }

        public static CheckedStatus getTypeByValue(Integer value) {
            for (CheckedStatus type : CheckedStatus.values()) {
                if (type.getValue().equals(value)) {
                    return type;
                }
            }
            return null;
        }

        private final Integer value;
        private final String name;
    }

    private static final long serialVersionUID = 7115615555569390931L;

    private String id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 审核状态
     */
    private Integer checkedStatus;

    /**
     * 推荐人id
     */
    private String recommendPersonId;

    /**
     * 文章作者或主体
     */
    private String author;

    /**
     * 文章发布时间
     */
    private Date publishDate;

    /**
     * 消息来源的系统名称
     */
    private String systemName;

    /**
     * 消息来源的系统中文名称
     */
    private String systemCnName;

    /**
     * 文章id
     */
    private Integer articleId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 文章缩略图URL
     */
    private String imgUrl;

    /**
     * 打开文章查看详情的URL
     */
    private String articleUrl;
}
