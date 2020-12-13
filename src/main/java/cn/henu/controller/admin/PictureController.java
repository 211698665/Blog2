package cn.henu.controller.admin;

import cn.henu.pojo.Photo;
import cn.henu.pojo.User;
import cn.henu.service.PictureService;
import cn.henu.utils.FileUploadUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class PictureController {
    @Autowired
    private PictureService pictureService;
    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    @RequestMapping("/admin/photo")
    public ModelAndView findAllPhoto(@RequestParam(required = false, defaultValue = "1", value = "pn") Integer pn, @RequestParam(defaultValue = "8") Integer pageSize, HttpServletRequest request, Photo photo) {
        PageHelper.startPage(pn, pageSize, "photo_Id desc");
        boolean b = (request.getParameter("Psea") != null);
        Photo photo2 = new Photo();
        if (request.getSession().getAttribute("Photo2") != null) {
            photo2 = (Photo) request.getSession().getAttribute("Photo2");
        }
        boolean b1 = photo != null && (photo.getPhotoStatus() != null || photo.getPhotoTitle() != null);
        boolean b2 = photo2 != null && (photo2.getPhotoStatus() != null || photo2.getPhotoTitle() != null);
        List list = new LinkedList<Photo>();
        if (b && (b1 || b2)) {
            if (photo != null && photo.getPhotoTitle() != null) {
                photo2.setPhotoTitle(photo.getPhotoTitle());
            }
            if (photo != null && photo.getPhotoStatus() != null) {
                photo2.setPhotoStatus(photo.getPhotoStatus());
            }
            request.getSession().setAttribute("Photo2", photo2);
            request.setAttribute("Photo", photo2);
            list = pictureService.findPhotoByIf(photo2);
        } else {
            list = pictureService.adminFindAllPhoto();
            request.getSession().setAttribute("Photo2", null);
        }
        PageInfo<Photo> plist = new PageInfo<>(list);
        ModelAndView view = new ModelAndView();
        view.addObject("photolist", plist.getList());
        view.setViewName("admin/photo");
        request.setAttribute("pageSize", plist.getPages());
        request.setAttribute("currPage", pn);
        return view;
    }

    @RequestMapping("/admin/updatePhotoPage")
    public ModelAndView updatePhotoPage(@RequestParam("id") Integer id, HttpServletRequest request) {
        Photo photo = pictureService.findPhotoById(id);
        request.setAttribute("updatePhoto", photo);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/update_photo");
        return model;
    }

    @RequestMapping("/admin/updatePhoto")
    public void updatePhoto(Photo photo, HttpServletRequest request, HttpServletResponse response, @RequestParam("photoFile") MultipartFile file) throws IOException {
        Photo photo2 = pictureService.findPhotoById(photo.getPhotoId());
        Date date = new Date();
        photo.setPhotoUpdateTime(date);
        photo.setPhotoCreateTime(photo2.getPhotoCreateTime());
        photo.setUserId(photo2.getUserId());
        if (!photo.getPhotoUrl().equals(photo2.getPhotoUrl())) {
            FileUploadUtils fileUitls = new FileUploadUtils();
            Map fileurl = fileUitls.uploadFile(file);
            if (Integer.parseInt(fileurl.get("error").toString()) == 0) {
                //获取error存的值
                photo.setPhotoUrl(fileurl.get("url").toString());
            } else {
                photo.setPhotoUrl("");
            }
        } else {
            photo.setPhotoUrl(photo2.getPhotoUrl());
        }
        //调用service进行添加操作
        int res = pictureService.updatePhoto(photo);
        response.sendRedirect("/admin/photo");
    }

    @RequestMapping(value = "/admin/photoAdd")
    public void addPhoto(Photo photo, HttpServletRequest request, HttpServletResponse response, @RequestParam("photoFile") MultipartFile file) throws IOException {
        Object obj = request.getSession().getAttribute("user");
        //获取session中的userID如果不存在或者不是Integer说明出现异常
        if (obj != null && obj instanceof User) {
            photo.setUserId((int) ((User) obj).getUserId());
        } else {
            throw new RuntimeException("session过期了");
        }
        Date date = new Date();
        photo.setPhotoUpdateTime(date);
        photo.setPhotoCreateTime(date);
        //调用service进行添加操作
        FileUploadUtils fileUitls = new FileUploadUtils();
        Map fileurl = fileUitls.uploadFile(file);
        if (Integer.parseInt(fileurl.get("error").toString()) == 0) {
            //获取error存的值
            photo.setPhotoUrl(fileurl.get("url").toString());
        } else {
            photo.setPhotoUrl("");
        }
        String res = pictureService.addPhoto(photo);
        request.setAttribute("addPhotoStatus", res);
        //返回Photo页面
        response.sendRedirect("/admin/photo");
    }

    @RequestMapping("/admin/photoDel")
    @ResponseBody
    public String delPhoto(@RequestParam("id") String id) {
        int photoId = Integer.parseInt(id);
        return pictureService.delPhoto(photoId) > 0 ? "success" : "fail";
    }

}
