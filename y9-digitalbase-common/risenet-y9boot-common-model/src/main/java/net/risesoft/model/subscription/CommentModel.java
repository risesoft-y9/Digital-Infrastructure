package net.risesoft.model.subscription;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 评论
 *
 * @author shidaobang
 * @date 2022/12/29
 */
@Data
public class CommentModel implements Serializable {

    private static final long serialVersionUID = -2999723433491979929L;

    /**
     * 评论id，主键
     *
     * @ignore
     */
    private Integer id;

    /**
     * 创建时间
     *
     * @ignore
     */
    private Date createTime;

    /**
     * 文章id
     */
    private Integer articleId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论人id
     *
     * @ignore
     */
    private String personId;

    /**
     * 回复目标用户id
     */
    private String targetPersonId;

    /**
     * 父评论id
     */
    private Integer parentCommentId;
}
