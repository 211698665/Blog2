package cn.henu.controller.user;

import cn.henu.pojo.*;
import cn.henu.service.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserArtDetailController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private SortService sortService;
    @Autowired
    private MeService meService;

    @RequestMapping("/user/artdetail")
    public ModelAndView artDetail(@RequestParam("id") String id, HttpServletRequest request) throws Exception {
        User me = meService.findUser(1);//默认为1
        request.setAttribute("me", me);
        Article article = articleService.findById(Integer.parseInt(id));
        article.setArticleClick(article.getArticleClick() + 1);
        articleService.updateViewCount(Integer.parseInt(id), article.getArticleClick());
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = "";
        date = formatdate.format(article.getArticleUpdatetime());
        Date newDate = formatdate.parse(date);
        java.sql.Date formaldate = new java.sql.Date(newDate.getTime());
        article.setArticleUpdatetime(formaldate);
        String str0 = HtmlUtils.htmlUnescape(article.getArticleContent());
        article.setArticleContent(str0);
        List<Comment> comentAll = commentService.selectByArticleId(Integer.parseInt(id));
        //排序的方式在pojo中已经定义过了
        Collections.sort(comentAll);
        int size = commentService.selectByArticleId(Integer.parseInt(id)).size();
        //查询评论数目
        request.setAttribute("commentNum", size);
        //这里是借用article_like字段来存储所属分类的ID
        Category artCategory = sortService.findByCategoryId(article.getArticleLike());
        request.setAttribute("artDetail", article);
        request.setAttribute("artCategory", artCategory.getCategoryName());
        request.setAttribute("topListHot", findTopHotArticle());
        request.setAttribute("comments", comentAll);
        request.setAttribute("categorysList", allCategory());
        request.setAttribute("advicesArticle", adviceArticle(article));
        ModelAndView model = new ModelAndView();
        model.setViewName("user/art_detail");
        return model;
    }

    @GetMapping("/user/comment/{articleId}")
    public String comments(Model model, @PathVariable int articleId) {
        List<Comment> comentAll = commentService.selectByArticleId(articleId);
        //排序的方式在pojo中已经定义过了
        Collections.sort(comentAll);
        model.addAttribute("comments", comentAll);
        return "user/art_detail :: commentList";
    }

    @PostMapping("/user/comment")
    public String post(Comment comment, HttpServletRequest request) {
        //设置头像
        String[] arr = new String[15];
        for (int i = 0; i < 14; i++) {
            arr[i] = "http://139.196.89.241/image/" + (i + 1) + ".jpg";
        }
        int index = new Random().nextInt(14);
        Date date = new Date();
        comment.setCommentHead(arr[index]);
        comment.setCommentCreateTime(date);
        comment.setCommentIp(request.getRemoteAddr());
        commentService.addComment(comment);
        //只把一级评论作为评论
        if (comment.getParentId() == -1) {
            Article article = articleService.findById(comment.getArticleId());
            article.setArticleComment(article.getArticleComment() + 1);
            //更新article中存储的评论数目
            articleService.updateByPrimaryKey(article);
        }
        return "redirect:/user/comment/" + comment.getArticleId();
    }

    public List<Article> adviceArticle(Article article) throws ParseException {
        //根据所选文章的Id查看文章的分类，然后从该类中根据关键词相似度进行推荐
        String articleKeyword = article.getArticleKeyword();
        String arr[] = articleKeyword.split(",");
        List<Article> list = new LinkedList<Article>();
        for (int i = 0; i < arr.length; i++) {
            list.addAll(articleService.findArticleByKeyWord(arr[i]));
        }
        Set set = new HashSet(list); //利用set去除重复,字符串可以直接去除，对象去重需要重写equals和hashcode方法
        list.clear();
        list.addAll(set);//这里list已经去除重复了
        Collections.sort(list, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getArticleClick() - o1.getArticleClick();
            }
        });
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = "";
        List<Article> list2 = new LinkedList<Article>();
        if (list.size() > 6) {//默认最多只推荐7个
            for (int i = 0; i < 6; i++) {
                if (!((int) list.get(i).getArticleId() == (int) article.getArticleId())) {
                    date = formatdate.format(list.get(i).getArticleUpdatetime());
                    Date newDate = formatdate.parse(date);
                    java.sql.Date formaldate = new java.sql.Date(newDate.getTime());
                    list.get(i).setArticleUpdatetime(formaldate);
                    list2.add(list.get(i));
                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (!((int) list.get(i).getArticleId() == (int) article.getArticleId())) {
                    date = formatdate.format(list.get(i).getArticleUpdatetime());
                    Date newDate = formatdate.parse(date);
                    java.sql.Date formaldate = new java.sql.Date(newDate.getTime());
                    list.get(i).setArticleUpdatetime(formaldate);
                    list2.add(list.get(i));
                }
            }
        }
        return list2;
    }

    public List<Article> findTopHotArticle() throws ParseException {
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = "";
        List<Article> list = new LinkedList<Article>();
        list = articleService.findAllArticle();
        Collections.sort(list);
        for (int i = 0; i < 6; i++) {
            date = formatdate.format(list.get(i).getArticleUpdatetime());
            Date newDate = formatdate.parse(date);
            java.sql.Date formaldate = new java.sql.Date(newDate.getTime());
            list.get(i).setArticleUpdatetime(formaldate);
        }
        List<Article> listTop = new LinkedList<Article>();
        for (int i = 0; i < 6; i++) {
            listTop.add(i, (Article) list.get(i));
        }
        return listTop;
    }

    public List<Category> allCategory() {
        //这里需要借助category的categoryDesc字段来存储前台的显示class
        List<Category> list = sortService.findAll();
        String[] arr = {"label label-success", "label label-info", "label label-warning", "label label-danger", "label label-primary "};
        for (int i = 0; i < list.size(); i++) {
            Random random = new Random();
            int index = random.nextInt(5);
            list.get(i).setCategoryDesc(arr[index]);
        }
        return list;
    }
}
