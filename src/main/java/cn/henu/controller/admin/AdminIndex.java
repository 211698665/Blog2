package cn.henu.controller.admin;

import cn.henu.service.ArticleService;
import cn.henu.service.CommentService;
import cn.henu.service.FlinkService;
import cn.henu.service.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminIndex {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FlinkService flinkService;
    @Autowired
    private SortService sortService;

    @RequestMapping("/admin/index")
    public ModelAndView adminIndex() {
        ModelAndView model = new ModelAndView();
        model.addObject("articles", articleService.countArticles(1) + articleService.countArticles(2));
        model.addObject("comments", commentService.countComm());
        model.addObject("flinks", flinkService.findAll().size());
        model.addObject("categorys", sortService.countCategorys());
        model.setViewName("admin/index");
        return model;
    }
}
