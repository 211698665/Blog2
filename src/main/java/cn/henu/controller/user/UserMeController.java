package cn.henu.controller.user;

import cn.henu.pojo.*;
import cn.henu.service.ArticleService;
import cn.henu.service.MeService;
import cn.henu.service.MusicService;
import cn.henu.service.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserMeController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private SortService sortService;
    @Autowired
    private MusicService musicService;
    @Autowired
    private MeService meService;

    @RequestMapping("/user/me")
    public ModelAndView userMe(HttpServletRequest request) throws ParseException {
        //获取音乐资源
        User me = meService.findUser(1);//默认为1
        request.setAttribute("me", me);
        List<Link> links = meService.findAllLink();
        List<Ability> all = meService.findAll();
        request.setAttribute("links", links);
        request.setAttribute("ability", all);
        request.setAttribute("topListHot", findTopHotArticle());
        //这里设置展示的分类
        request.setAttribute("categorysList", allCategory());
        ModelAndView model = new ModelAndView();
        model.setViewName("user/user_me");
        return model;
    }


    public List<Article> findTopHotArticle() throws ParseException {
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = "";
        List<Article> list = new LinkedList<Article>();
        list = articleService.findAllArticle();
        Collections.sort(list);
        for (int i = 0; i < 5; i++) {
            date = formatdate.format(list.get(i).getArticleUpdatetime());
            Date newDate = formatdate.parse(date);
            java.sql.Date formaldate = new java.sql.Date(newDate.getTime());
            list.get(i).setArticleUpdatetime(formaldate);
        }
        List<Article> listTop = new LinkedList<Article>();
        for (int i = 0; i < 5; i++) {
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
