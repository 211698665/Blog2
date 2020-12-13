package cn.henu.service.impl;

import cn.henu.dao.CommentMapper;
import cn.henu.pojo.Comment;
import cn.henu.pojo.CommentExample;
import cn.henu.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    //存放迭代找出的所有子代的集合
    private List<Comment> tempReplys = new ArrayList<>();

    /**
     * @Description: 查询评论
     * @Auther: ONESTAR
     * @Date: 17:26 2020/4/14
     * @Param:
     * @Return: 评论消息
     */
    @Override
    public List<Comment> findAll() {
        //查询出父节点
        CommentExample commentExample = new CommentExample();
        commentExample.setOrderByClause("comment_id DESC");
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andParentIdEqualTo(-1);
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        for (Comment comment : comments) {
            int id = comment.getCommentId();
            String parentNickname1 = comment.getCommentName();
            CommentExample commentExample2 = new CommentExample();
            commentExample2.setOrderByClause("comment_id DESC");
            CommentExample.Criteria criteria2 = commentExample2.createCriteria();
            criteria2.andParentIdEqualTo(id);
            List<Comment> childComments = commentMapper.selectByExample(commentExample2);
            //查询出子评论
            combineChildren(childComments, parentNickname1);
            comment.setReplyComments(tempReplys);
            tempReplys = new ArrayList<>();
        }
        return comments;
    }

    /**
     * @Description: 查询出子评论
     * @Auther: ONESTAR
     * @Date: 17:31 2020/4/14
     * @Param: childComments：所有子评论
     * @Param: parentNickname1：父评论的姓名
     * @Return:
     */
    private void combineChildren(List<Comment> childComments, String parentNickname1) {
        //判断是否有一级子回复
        if (childComments.size() > 0) {
            //循环找出子评论的id
            for (Comment childComment : childComments) {
                String parentNickname = childComment.getCommentName();
                childComment.setParentCommentName(parentNickname1);

                tempReplys.add(childComment);
                int childId = childComment.getCommentId();
                //查询二级以及所有子集回复
                recursively(childId, parentNickname);
            }
        }
    }

    /**
     * @Description: 循环迭代找出子集回复
     * @Auther: ONESTAR
     * @Date: 17:33 2020/4/14
     * @Param: childId：子评论的id
     * @Param: parentNickname1：子评论的姓名
     * @Return:
     */
    private void recursively(int childId, String parentNickname1) {
        //根据子一级评论的id找到子二级评论
        CommentExample example = new CommentExample();
        example.setOrderByClause("comment_id DESC");
        CommentExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(childId);
        List<Comment> replayComments = commentMapper.selectByExample(example);
        if (replayComments.size() > 0) {
            for (Comment replayComment : replayComments) {
                String parentNickname = replayComment.getCommentName();
                replayComment.setParentCommentName(parentNickname1);
                int replayId = replayComment.getCommentId();
                tempReplys.add(replayComment);
                //循环迭代找出子集回复
                recursively(replayId, parentNickname);
            }
        }
    }


    @Override
    public List<Comment> selectByArticleId(int id) {
        //查询出父节点
        CommentExample commentExample = new CommentExample();
        commentExample.setOrderByClause("comment_id DESC");
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andArticleIdEqualTo(id);
        criteria.andParentIdEqualTo(-1);
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        for (Comment comment : comments) {
            int commentId = comment.getCommentId();
            String parentNickname1 = comment.getCommentName();
            //查询出父节点
            CommentExample commentExample2 = new CommentExample();
            commentExample2.setOrderByClause("comment_id DESC");
            CommentExample.Criteria criteria2 = commentExample2.createCriteria();
            criteria2.andArticleIdEqualTo(id);
            criteria2.andParentIdEqualTo(commentId);
            List<Comment> childComments = commentMapper.selectByExample(commentExample2);

            //查询出子评论
            combineChildren(childComments, parentNickname1);
            comment.setReplyComments(tempReplys);
            tempReplys = new ArrayList<>();
        }

        return comments;
    }


    @Override
    public int addComment(Comment comment) {
        return commentMapper.insert(comment);
    }


    @Override
    public int delComment(int id) {
        CommentExample commentExample = new CommentExample();
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andCommentIdEqualTo(id);
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        for (Comment comment : comments) {
            int commentId = comment.getCommentId();
            String parentNickname1 = comment.getCommentName();
            //查询出父节点
            CommentExample commentExample2 = new CommentExample();
            commentExample2.setOrderByClause("comment_id DESC");
            CommentExample.Criteria criteria2 = commentExample2.createCriteria();
            criteria2.andParentIdEqualTo(commentId);
            List<Comment> childComments = commentMapper.selectByExample(commentExample2);
            //查询出子评论
            combineChildren(childComments, parentNickname1);
            comment.setReplyComments(tempReplys);
            tempReplys = new ArrayList<>();
        }
        //删除父评论的同时，删除子评论
        for (int i = 0; i < comments.size(); i++) {
            List<Comment> lists = comments.get(i).getReplyComments();
            if (lists != null && lists.size() > 0) {
                for (int j = 0; j < lists.size(); j++) {
                    commentMapper.deleteByPrimaryKey(lists.get(j).getCommentId());
                }
            }
            commentMapper.deleteByPrimaryKey(comments.get(i).getCommentId());
        }
        return comments.size();
    }

    @Override
    public int countComm() {
        return commentMapper.countComm().size();
    }

    @Override
    public Comment findCommById(int commentId) {
        CommentExample example = new CommentExample();
        CommentExample.Criteria criteria = example.createCriteria();
        criteria.andCommentIdEqualTo(commentId);
        return commentMapper.selectByExample(example).get(0);
    }

/*    @Override
    public Comment findCommById(int commentId) {
        return commentMapper.findCommById(commentId);
    }*/

    @Override
    public int delCommentByArticleId(int articleId) {
        return commentMapper.delCommentByArticleId(articleId);
    }

    @Override
    public List<Comment> findAllComment() {
        return commentMapper.findAllComment();
    }
}
