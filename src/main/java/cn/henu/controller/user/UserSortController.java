package cn.henu.controller.user;

import cn.henu.dao.SayingMapper;
import cn.henu.pojo.*;
import cn.henu.service.*;
import cn.henu.vo.CategoryVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserSortController {
    @Autowired
    private SortService sortService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private TimelineService timelineService;
    @Autowired
    private ArticleAndCotegoryService articleAndCotegoryService;
    @Autowired
    private MeService meService;

    @RequestMapping("/user/sort")
    public ModelAndView frontFindAllSort(@RequestParam(required = false, defaultValue = "1", value = "pn") Integer pn, @RequestParam(defaultValue = "15") Integer pageSize, HttpServletRequest request) throws Exception {
        User me = meService.findUser(1);//默认为1
        request.setAttribute("me", me);
        String[] arr = {"#337ab7", "#f0ad4e", "#5bc0de", "#5cb85c", "#d9534f", "#02aac4", "#afde5c", "#5cafde", "#deb25c", "#de815c", "#39b2a9", "#6dbb4a", "#53bee6", "#e4ba00", "#ec6459"};
        List<CategoryVo> listCate = new LinkedList<>();
        Page<CategoryVo> page = PageHelper.startPage(pn, pageSize);
        List<Category> list = new LinkedList<Category>();
        list = sortService.findAll();
        for (int i = 0; i < list.size(); i++) {
            //借用categoryPid字段存储每一类的博客数目
            list.get(i).setCategoryPid(articleAndCotegoryService.selectByCategoryId(list.get(i).getCategoryId()).size());
        }
        Random random = new Random();
        for (int i = 0; i < list.size(); i++) {
            CategoryVo category = new CategoryVo();//CategoryVo在category基础上添加了颜色属性
            int index = random.nextInt(arr.length); //随机设置一个前台展示的颜色
            category.setCategoryDesc(list.get(i).getCategoryDesc());
            category.setCategoryId(list.get(i).getCategoryId());
            category.setCategoryImg(list.get(i).getCategoryImg());
            category.setCategoryName(list.get(i).getCategoryName());
            category.setCategoryPid(list.get(i).getCategoryPid());
            category.setCategoryStatus(list.get(i).getCategoryStatus());
            category.setCategorycolor(arr[index]);
            listCate.add(category);
        }
        PageInfo<CategoryVo> plist = new PageInfo<>(listCate);
        ModelAndView view = new ModelAndView();
        List<Saying> says = timelineService.findAllSaying();
        int index = random.nextInt(says.size());
        view.addObject("categorylist", plist.getList());
        view.addObject("todaySaying", says.get(index).getSayingContent());
        view.setViewName("user/user_sort");
        int categorysPageNum;
        if (sortService.countCategorys() % pageSize == 0) {
            categorysPageNum = sortService.countCategorys() / pageSize;
        } else {
            categorysPageNum = sortService.countCategorys() / pageSize + 1;
        }
        request.setAttribute("pageSize", categorysPageNum);
        request.setAttribute("currPage", pn);
        view.addObject("topListHot", findTopHotArticle());
        return view;
    }

    @RequestMapping("/user/sortList")
    public ModelAndView sortList(@RequestParam("id") String id, @RequestParam(required = false, defaultValue = "1", value = "pn") Integer pn, @RequestParam(defaultValue = "7") Integer pageSize, HttpServletRequest request) throws Exception {
        User me = meService.findUser(1);//默认为1
        request.setAttribute("me", me);
        Page<Article> page = PageHelper.startPage(pn, pageSize);
        int id2 = Integer.parseInt(request.getParameter("id"));
        List<Article> listCategorys = articleService.findArticleByCategoryId(id2);
        for (int i = 0; i < listCategorys.size(); i++) {
            //先从文章中把所属类ID取出来,ArticleLike属性中
            String categoryName = sortService.findByCategoryId(listCategorys.get(i).getArticleLike()).getCategoryName();
            //把查询到的名称放到articleKeyword属性中
            listCategorys.get(i).setArticleKeyword(categoryName);
        }
        Collections.sort(listCategorys, new Comparator<Article>() {
            public int compare(Article a1, Article a2) {
                return a2.getArticleId() - a1.getArticleId();
            }
        });
        PageInfo<Article> plist = new PageInfo<>(listCategorys);
        request.setAttribute("articlelist", plist.getList());
        int articlePageNum;
        if (articleAndCotegoryService.selectByCategoryId(id2).size() % pageSize == 0) {
            articlePageNum = articleAndCotegoryService.selectByCategoryId(id2).size() / pageSize;
        } else {
            articlePageNum = articleAndCotegoryService.selectByCategoryId(id2).size() / pageSize + 1;
        }
        request.setAttribute("sortId", id2);
        request.setAttribute("pageSize", articlePageNum);
        request.setAttribute("currPage", pn);
        request.setAttribute("topListHot", findTopHotArticle());
        request.setAttribute("categorysList", allCategory());
        ModelAndView model = new ModelAndView();
        model.setViewName("user/sort_list");
        return model;
    }

    public List<Article> findTopHotArticle() throws Exception {
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
