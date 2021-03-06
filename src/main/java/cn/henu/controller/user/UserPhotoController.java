package cn.henu.controller.user;

import cn.henu.pojo.Article;
import cn.henu.pojo.Category;
import cn.henu.pojo.Photo;
import cn.henu.pojo.User;
import cn.henu.service.ArticleService;
import cn.henu.service.MeService;
import cn.henu.service.PictureService;
import cn.henu.service.SortService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserPhotoController {
    @Autowired
    private PictureService pictureService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private SortService sortService;
    @Autowired
    private MeService meService;

    @RequestMapping("/user/photo")
    public ModelAndView frontFindAllPhoto(@RequestParam(required = false, defaultValue = "1", value = "pn") Integer pn, @RequestParam(defaultValue = "12") Integer pageSize, HttpServletRequest request) throws ParseException {
        List list = new LinkedList<Photo>();
        ModelAndView view = new ModelAndView();
        User me = meService.findUser(1);//默认为1
        request.setAttribute("me", me);
        List<Category> listcategory = allCategory();
        view.addObject("MyallKinds", listcategory);
        if (request.getParameter("yspassword") != null && request.getParameter("yspassword").equals(me.getUserPassword())) {
            list = pictureService.findYsAllPhoto(pn, pageSize).getList();
            Collections.sort(list, new Comparator<Photo>() {
                @Override
                public int compare(Photo o1, Photo o2) {
                    return o1.getPhotoId() - o2.getPhotoId();
                }
            });
            view.addObject("photolist", list);
            view.setViewName("user/user_ysphoto");
            int photoPageNum;
            int tempnum = pictureService.countYsPhoto();
            if (tempnum % pageSize > 0) {
                photoPageNum = pictureService.countYsPhoto() / pageSize + 1;
            } else {
                photoPageNum = pictureService.countYsPhoto() / pageSize;
            }
            request.setAttribute("pageSize", photoPageNum);
            request.setAttribute("currPage", pn);
            request.setAttribute("photos", list);
            view.addObject("topListHot", findTopHotArticle());
            return view;
        } else {
            //这里之所以在这里添加种类信息是为了怕之后的分页会导致查询到的数据只有分页数
            list = pictureService.findAllPhoto(pn, pageSize).getList();
            Collections.sort(list, new Comparator<Photo>() {
                @Override
                public int compare(Photo o1, Photo o2) {
                    return o1.getPhotoId() - o2.getPhotoId();
                }
            });

            view.addObject("photolist", list);
            view.setViewName("user/user_photo");
            int photoPageNum;
            int tempnum = pictureService.countPhoto();
            if (tempnum % pageSize > 0) {
                photoPageNum = pictureService.countPhoto() / pageSize + 1;
            } else {
                photoPageNum = pictureService.countPhoto() / pageSize;
            }
            request.setAttribute("pageSize", photoPageNum);
            request.setAttribute("currPage", pn);
            request.setAttribute("photos", list);

            view.addObject("topListHot", findTopHotArticle());
            return view;
        }

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
