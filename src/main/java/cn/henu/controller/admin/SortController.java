package cn.henu.controller.admin;

import cn.henu.pojo.Category;
import cn.henu.pojo.User;
import cn.henu.service.SortService;
import cn.henu.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class SortController {
    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;
    @Autowired
    private SortService sortService;

    @RequestMapping("/admin/sort")
    public String sort(HttpServletRequest request) {
        List<Category> categories = sortService.findAll();
        for (int i = 0; i < categories.size(); i++) {
            categories.get(i).setCategoryPid(sortService.countBlogInCategory(categories.get(i).getCategoryId()));
        }
        request.setAttribute("categorys", categories);
        return "admin/category";
    }

    @RequestMapping(value = "/admin/categoryAdd")
    public void addCategory(Category category, HttpServletRequest request, HttpServletResponse response, @RequestParam("photoFile") MultipartFile file) throws IOException {
        Object obj = request.getSession().getAttribute("user");
        //获取session中的userID如果不存在或者不是Integer说明出现异常
        if (obj != null && obj instanceof User) {
            category.setUserId((int) ((User) obj).getUserId());
        } else {
            request.setAttribute("addPhotoStatus", "error");
        }
        //调用service进行添加操作
        FileUploadUtils fileUitls = new FileUploadUtils();
        Map fileurl = fileUitls.uploadFile(file);
        if (Integer.parseInt(fileurl.get("error").toString()) == 0) {
            //获取error存的值
            category.setCategoryImg(fileurl.get("url").toString());
        } else {
            category.setCategoryImg("");
        }
        String res = sortService.addCategory(category);
        request.setAttribute("addCategoryStatus", res);
        //返回Photo页面
        response.sendRedirect("/admin/sort"); //这里执行的是controller方法进行跳转的，不是直接找的页面
    }


    @RequestMapping("/admin/categoryDel")
    @ResponseBody
    public String delCategory(@RequestParam("id") String id) {
        int cateId = Integer.parseInt(id);
        return sortService.delCategoryById(cateId) > 0 ? "success" : "fail";
    }

    ///admin/updateCategoryPage
    @RequestMapping("/admin/updateCategoryPage")
    public ModelAndView updateCategoryPage(@RequestParam("id") Integer id, HttpServletRequest request) {
        Category category = sortService.findByCategoryId(id);
        request.setAttribute("updateCategory", category);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/update_category");
        return model;
    }

    @RequestMapping("/admin/updateCategory")
    public void updatePhoto(Category category, HttpServletRequest request, HttpServletResponse response, @RequestParam("photoFile") MultipartFile file) throws IOException {
        Category category2 = sortService.findByCategoryId(category.getCategoryId());
        category.setUserId(category2.getUserId());
        if (!category.getCategoryImg().equals(category2.getCategoryImg())) {
            FileUploadUtils fileUitls = new FileUploadUtils();
            Map fileurl = fileUitls.uploadFile(file);
            if (Integer.parseInt(fileurl.get("error").toString()) == 0) {
                category.setCategoryImg(fileurl.get("url").toString());
            } else {
                category.setCategoryImg("");
            }
        } else {
            category.setCategoryImg(category2.getCategoryImg());
        }
        //调用service进行添加操作
        int res = sortService.updateByCategoryId(category);
        response.sendRedirect("/admin/sort");
    }
}
