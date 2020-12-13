package cn.henu.controller.admin;

import cn.henu.pojo.Article;
import cn.henu.pojo.Articlecategoryref;
import cn.henu.pojo.Category;
import cn.henu.pojo.User;
import cn.henu.service.ArticleAndCotegoryService;
import cn.henu.service.ArticleService;
import cn.henu.service.CommentService;
import cn.henu.service.SortService;
import cn.henu.utils.FileUploadUtils;
import cn.henu.vo.ArticleVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ArticleController {
    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private SortService sortService;
    @Autowired
    private ArticleAndCotegoryService articleAndCotegoryService;
    @Autowired
    private CommentService commentService;

    @RequestMapping("/admin/article")
    public ModelAndView findAllArticle(Article article, @RequestParam(required = false, defaultValue = "1", value = "pn") Integer pn, @RequestParam(defaultValue = "10") Integer pageSize, HttpServletRequest request) throws ParseException {
        List<Category> categories = sortService.findAll();
        boolean b = request.getParameter("Sea") != null ? true : false;//这里是为了判断是否为搜索状态
        PageHelper.startPage(pn, pageSize, "article_Id desc");
        List<Article> list = new LinkedList<Article>();
        Article article2 = new Article();
        if (request.getSession().getAttribute("Article2") != null) {
            article2 = (Article) request.getSession().getAttribute("Article2");//把搜索条件从session中拿出来
        }
        boolean b1 = article != null && (article.getArticleTitle() != null || article.getArticleLike() != null || article.getArticleStatus() != null);
        boolean b2 = article2 != null && (article2.getArticleTitle() != null || article2.getArticleLike() != null || article2.getArticleStatus() != null);
        if ((b1 || b2) && b) {//article和session中有一个不为空，并且sea不能为空,此时还是在搜索状态
            if (article != null && article.getArticleTitle() != null) {
                article2.setArticleTitle(article.getArticleTitle());
            }
            if (article != null && article.getArticleLike() != null) {
                article2.setArticleLike(article.getArticleLike());
            }
            if (article != null && article.getArticleStatus() != null) {
                article2.setArticleStatus(article.getArticleStatus());
            }
            request.getSession().setAttribute("Article2", article2);
            request.setAttribute("Article", article2);

            list = articleService.findByIf(article2);
            if (article2.getArticleLike() != null) {
                request.setAttribute("categoryChosed", sortService.findByCategoryId(article2.getArticleLike()));
                List<Category> list3 = new LinkedList<Category>();
                //这里是为了避免重复显示已经选择的那个分类
                for (int i = 0; i < categories.size(); i++) {
                    if (!categories.get(i).getCategoryName().equals(sortService.findByCategoryId(article2.getArticleLike()).getCategoryName())) {
                        list3.add(categories.get(i));
                    }
                }
                request.setAttribute("allcategorys", list3);
            } else {

                request.setAttribute("allcategorys", categories);
            }
        } else {
            list = articleService.adminFindAllArticle();
            request.getSession().setAttribute("Article2", null);
            request.setAttribute("allcategorys", categories); //设置所有的分类用于查询
        }
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = "";
        //这里借用article_like存储所属分类的id，处理一下时间合适
        for (int i = 0; i < list.size(); i++) {
            date = formatdate.format(list.get(i).getArticleUpdatetime());
            Date newDate = formatdate.parse(date);
            java.sql.Date formaldate = new java.sql.Date(newDate.getTime());
            list.get(i).setArticleUpdatetime(formaldate);
            Articlecategoryref articlecategoryref = articleAndCotegoryService.selectByArticleId(list.get(i).getArticleId());
            list.get(i).setArticleLike(articlecategoryref.getCategoryId());
        }
        Collections.sort(list, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getArticleId() - o1.getArticleId();
            }
        });
        PageInfo<Article> plist = new PageInfo<>(list);
        ModelAndView view = new ModelAndView();
        view.addObject("articlelist", plist.getList());
        view.setViewName("admin/article");
        int articlePageNum;
        int tempcount = articleService.countArticles(1) + articleService.countArticles(2);
        if (tempcount % 10 == 0) {
            articlePageNum = tempcount / pageSize;
        } else {
            articlePageNum = tempcount / pageSize + 1;
        }
        if (b) {
            request.setAttribute("pageSize", plist.getPages());
        } else {
            request.setAttribute("pageSize", articlePageNum);
        }
        request.setAttribute("currPage", pn);
        return view;
    }

    @RequestMapping("/admin/addArticlePage")
    public ModelAndView addArticlePage(HttpServletRequest request) {
        request.setAttribute("allcategorys", sortService.findAll());
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/add_article");
        return model;
    }

    @RequestMapping("/admin/addArticle")
    public void addArticle(ArticleVo articleVo, @RequestParam("photoFile") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Article article = new Article();
        String articleContent1 = articleVo.getArticleContent();
        //这里是对传输的数据进行加密
        article.setArticleContent(HtmlUtils.htmlEscapeHex(articleContent1));
        article.setArticleKeyword(articleVo.getArticleKeyword());
        article.setArticleStatus(articleVo.getArticleStatus());
        article.setArticleTitle(articleVo.getArticleTitle());
        article.setArticleSummary(articleVo.getArticleSummary());
        article.setArticleComment(0);
        article.setArticleClick(100);
        Object obj = request.getSession().getAttribute("user");
        //获取session中的userID如果不存在或者不是Integer说明出现异常
        if (obj != null && obj instanceof User) {
            article.setUserId((int) ((User) obj).getUserId());
        } else {
            throw new RuntimeException("session过期了");
        }
        Date date = new Date();
        article.setArticleUpdatetime(date);
        article.setArticleCreatetime(date);
        //调用service进行添加操作
        FileUploadUtils fileUploadUtils = new FileUploadUtils();
        Map fileurl = fileUploadUtils.uploadFile(file);
        if (Integer.parseInt(fileurl.get("error").toString()) == 0) {
            //获取error存的值
            article.setArticleImage(fileurl.get("url").toString());
        } else {
            article.setArticleImage("");
        }
        article.setArticleLike(Integer.parseInt(articleVo.getArticleCategory()));

        int res = articleService.addArticle(article);
        Articlecategoryref articlecategoryref = new Articlecategoryref();
        articlecategoryref.setArticleId(article.getArticleId());
        //这里所属类的id存储在了VO的ArticleCategory字段中
        articlecategoryref.setCategoryId(Integer.parseInt(articleVo.getArticleCategory()));

        articlecategoryref.setUserId((int) ((User) obj).getUserId()); //这里注意设置UserId
        articleAndCotegoryService.insert(articlecategoryref);
        request.setAttribute("addArticleStatus", res);
        response.sendRedirect("/admin/article");
    }

    @ResponseBody
    @RequestMapping("/admin/multiUpload")
    public String uploadImgFiles(MultipartFile file) {
        FileUploadUtils fileUploadUtils = new FileUploadUtils();
        Map map = fileUploadUtils.uploadFile(file);
        if (Integer.parseInt(map.get("error").toString()) == 0) {
            return map.get("url").toString();
        } else {
            return "fail";
        }
    }

    @RequestMapping("/admin/articleDel")
    @ResponseBody
    public List<Article> delArticle(@RequestParam("id") String id, String pn, HttpServletRequest request) throws ParseException {
        int articled = Integer.parseInt(id);
        //删除文章的同时，需要先删除文章在ArticleandCategory表中的关系，需要删除评论表中的相关评论
        commentService.delCommentByArticleId(articled);
        articleAndCotegoryService.deleteByArticleId(articled);
        int b = Integer.parseInt(request.getParameter("Sea")); //判断是否为查询的过程中执行了删除
        //最后删除文章
        if (articleService.delByArticleId((articled)) > 0) {
            //删除成功后重新查询
            PageHelper.startPage(Integer.parseInt(pn), 10, "article_Id desc");
            List<Article> list = new LinkedList<Article>();
            if (b == 1) {
                //注意如果是在查询中执行的删除，此时session肯定不空
                Article article = (Article) request.getSession().getAttribute("Article2");
                list = articleService.findByIf(article);
            } else {
                list = articleService.adminFindAllArticle();
            }
            SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = "";
            //这里借用article_like存储所属分类的id，处理一下时间合适
            for (int i = 0; i < list.size(); i++) {
                date = formatdate.format(list.get(i).getArticleUpdatetime());
                Date newDate = formatdate.parse(date);
                java.sql.Date formaldate = new java.sql.Date(newDate.getTime());
                list.get(i).setArticleUpdatetime(formaldate);
                Articlecategoryref articlecategoryref = articleAndCotegoryService.selectByArticleId(list.get(i).getArticleId());
                list.get(i).setArticleLike(articlecategoryref.getCategoryId());
            }
            Collections.sort(list, new Comparator<Article>() {
                @Override
                public int compare(Article o1, Article o2) {
                    return o2.getArticleId() - o1.getArticleId();
                }
            });
            PageInfo<Article> plist = new PageInfo<>(list);
            ModelAndView view = new ModelAndView();
            view.addObject("articlelist", plist.getList());
            view.setViewName("admin/article");
            int articlePageNum;
            int tempcount = articleService.countArticles(1) + articleService.countArticles(2);
            if (tempcount % 10 == 0) {
                articlePageNum = tempcount / 10;
            } else {
                articlePageNum = tempcount / 10 + 1;
            }
            request.setAttribute("pageSize", articlePageNum);
            request.setAttribute("allcategorys", sortService.findAll()); //设置所有的分类用于查询
            request.setAttribute("currPage", pn);
            return plist.getList();
        }
        //失败
        return null;
    }

    @RequestMapping("/admin/updateArticlePage")
    public ModelAndView updateArticlePage(@RequestParam("id") Integer id, HttpServletRequest request) {
        Article article = articleService.findById(id);
        String articleContent1 = article.getArticleContent();
        article.setArticleContent(articleContent1);
        //article.getArticleLike()这里借助articleLike存储categoryId,这里是为了把该文章的分类放在下拉选择框的第一行
        request.setAttribute("categoryChosed", sortService.findByCategoryId(article.getArticleLike()));
        List<Category> list = new LinkedList<Category>();
        List<Category> list2 = sortService.findAll();
        //这里是为了避免重复显示已经选择的那个分类
        for (int i = 0; i < list2.size(); i++) {
            if (!list2.get(i).getCategoryName().equals(sortService.findByCategoryId(article.getArticleLike()).getCategoryName())) {
                list.add(list2.get(i));
            }
        }
        request.setAttribute("allcategorys", list);
        request.setAttribute("updateArticle", article);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/update_article");
        return model;
    }

    //更新文章，，，
    @RequestMapping("/admin/updateArticle")
    public void updateArticle(Article article, HttpServletRequest request, HttpServletResponse response, @RequestParam("photoFile") MultipartFile file) throws Exception {
        Article article1 = articleService.findById(article.getArticleId());
        if (!article.getArticleTitle().equals(article1.getArticleTitle())) {
            article1.setArticleTitle(article.getArticleTitle());
        }
        if (!article.getArticleLike().equals(article1.getArticleLike())) {
            //这个字段存储的是文章的分类
            //修改文章分类
            Articlecategoryref articlecategoryref = new Articlecategoryref();
            articlecategoryref.setCategoryId(article.getArticleLike());
            articlecategoryref.setArticleId(article.getArticleId());
            articlecategoryref.setUserId(article1.getUserId());
            articleAndCotegoryService.updateCategoryAndArticleByArticleId(articlecategoryref);
            article1.setArticleLike(article.getArticleLike());
        }
        if (!article.getArticleImage().equals(article1.getArticleImage())) {
            FileUploadUtils fileUploadUtils = new FileUploadUtils();
            Map fileurl = fileUploadUtils.uploadFile(file);
            article1.setArticleImage(fileurl.get("url").toString());
        }
        String articleContent1 = article.getArticleContent();
        article1.setArticleContent(HtmlUtils.htmlEscapeHex(articleContent1));
        article1.setArticleSummary(article.getArticleSummary());
        article1.setArticleKeyword(article.getArticleKeyword());
        article1.setArticleStatus(article.getArticleStatus());
        //修改文章
        articleService.updateByPrimaryKey(article1);
        response.sendRedirect("/admin/article");
    }

}
