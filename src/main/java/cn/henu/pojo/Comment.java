package cn.henu.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment implements Comparable<Comment> {
    private Integer commentId;

    private Integer articleId;

    private String commentName;

    private String commentQq;

    private String commentContent;

    private String commentHead;

    private String commentIp;

    private Date commentCreateTime;

    private Integer parentId;
    //回复评论
    private List<Comment> replyComments = new ArrayList<>();
    private Comment parentComment;
    private String parentCommentName;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public String getCommentName() {
        return commentName;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName == null ? null : commentName.trim();
    }

    public String getCommentQq() {
        return commentQq;
    }

    public void setCommentQq(String commentQq) {
        this.commentQq = commentQq == null ? null : commentQq.trim();
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent == null ? null : commentContent.trim();
    }

    public String getCommentHead() {
        return commentHead;
    }

    public void setCommentHead(String commentHead) {
        this.commentHead = commentHead == null ? null : commentHead.trim();
    }

    public String getCommentIp() {
        return commentIp;
    }

    public void setCommentIp(String commentIp) {
        this.commentIp = commentIp == null ? null : commentIp.trim();
    }

    public Date getCommentCreateTime() {
        return commentCreateTime;
    }

    public void setCommentCreateTime(Date commentCreateTime) {
        this.commentCreateTime = commentCreateTime;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<Comment> getReplyComments() {
        return replyComments;
    }

    public void setReplyComments(List<Comment> replyComments) {
        this.replyComments = replyComments;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public String getParentCommentName() {
        return parentCommentName;
    }

    public void setParentCommentName(String parentCommentName) {
        this.parentCommentName = parentCommentName;
    }

    @Override
    public int compareTo(Comment comment) {
        return comment.getCommentId() - this.getCommentId();
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", articleId=" + articleId +
                ", commentName='" + commentName + '\'' +
                ", commentQq='" + commentQq + '\'' +
                ", commentContent='" + commentContent + '\'' +
                ", commentHead='" + commentHead + '\'' +
                ", commentIp='" + commentIp + '\'' +
                ", commentCreateTime=" + commentCreateTime +
                ", parentId=" + parentId +
                ", replyComments=" + replyComments +
                ", parentComment=" + parentComment +
                ", parentCommentName='" + parentCommentName + '\'' +
                '}';
    }
}