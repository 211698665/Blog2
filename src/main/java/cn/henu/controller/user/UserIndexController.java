package cn.henu.controller.user;

import cn.henu.pojo.*;
import cn.henu.service.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserIndexController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private SortService sortService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private MeService meService;

    @RequestMapping("/")
    @ResponseBody
    public ModelAndView index(@RequestParam(required = false, defaultValue = "1", value = "pn") Integer pn, @RequestParam(defaultValue = "5") Integer pageSize, HttpServletRequest request) throws Exception {
        ModelAndView model = new ModelAndView();
        User me = meService.findUser(1);//默认为1
        request.setAttribute("me", me);
        PageHelper.startPage(pn, pageSize, "article_id desc");
        List<Article> articles = new LinkedList<Article>();
        articles = articleService.findAllArticle();
        //这里借用articleKeywords来保存文章类名
        for (int i = 0; i < articles.size(); i++) {
            //先从文章中把所属类ID取出来,ArticleLike属性中
            String categoryName = sortService.findByCategoryId(articles.get(i).getArticleLike()).getCategoryName();
            //把查询到的名称放到articleKeyword属性中
            articles.get(i).setArticleKeyword(categoryName);
        }
        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getArticleId() - o1.getArticleId();
            }
        });
        PageInfo<Article> plist = new PageInfo<>(articles);
        request.setAttribute("articlelist", plist.getList());
        int articlePageNum;
        if (articleService.countArticles(1) % pageSize == 0) {
            articlePageNum = articleService.countArticles(1) / pageSize;
        } else {
            articlePageNum = articleService.countArticles(1) / pageSize + 1;
        }
        //把最新加入的前四个图片放出来
        List<Photo> allPhoto = pictureService.findTopFour();
        //这里使用一个单独的photo来设置前台的active属性
        request.setAttribute("photoOne", allPhoto.get(0));
        request.setAttribute("topPhoto", allPhoto.subList(1, 4)); //截取1,2,3 共三个元素
        //加载通知
        List<Notice> allNotice = noticeService.findAllNotice();
        request.setAttribute("notices", allNotice);
        request.setAttribute("pageSize", articlePageNum);
        request.setAttribute("currPage", pn);
        request.setAttribute("topListHot", findTopHotArticle());
        //这里设置展示的分类
        request.setAttribute("categorysList", allCategory());
        model.setViewName("user/index");
        return model;
    }

    @RequestMapping("/user/exception")
    public ModelAndView ExceptionsRep(ModelAndView modelAndView) {
        modelAndView.setViewName("user/404");
        return modelAndView;
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
