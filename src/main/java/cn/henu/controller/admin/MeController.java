package cn.henu.controller.admin;

import cn.henu.pojo.Ability;
import cn.henu.pojo.Link;
import cn.henu.pojo.User;
import cn.henu.service.FlinkService;
import cn.henu.service.MeService;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
public class MeController {
    @Autowired
    private MeService meService;
    @Autowired
    private FlinkService flinkService;
    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    @RequestMapping("/admin/ability")
    public ModelAndView ability(HttpServletRequest request) {
        List<Ability> ablities = meService.findAll();
        request.setAttribute("abilities", ablities);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/my_ability");
        return model;
    }

    @RequestMapping("/admin/detail")
    public ModelAndView detail(HttpServletRequest request, HttpSession session) {
        Object obj = request.getSession().getAttribute("user");
        //获取session中的userID如果不存在或者不是Integer说明出现异常
        if (obj != null && obj instanceof User) {
            request.setAttribute("detail", meService.findByKey(((User) obj).getUserId()));
        } else {
            throw new RuntimeException("session过期了");
        }
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/my_detail");
        return model;
    }

    @RequestMapping("/admin/updateMydetail")
    public ModelAndView updateDetail(User user, HttpServletRequest request, @RequestParam("photoFile") MultipartFile file) {
        Date date = new Date();
        User user2 = meService.findByKey(user.getUserId());
        user.setUserPassword(user2.getUserPassword());
        user.setUserUpdatetime(date);
        if (!user.getUserImage().equals(user2.getUserImage())) {
            FileUploadUtils fileUploadUtils = new FileUploadUtils();
            Map fileurl = fileUploadUtils.uploadFile(file);
            if (Integer.parseInt(fileurl.get("error").toString()) == 0) {
                user.setUserImage(fileurl.get("url").toString());
            } else {
                user.setUserImage("");
            }
        } else {
            user.setUserImage(user2.getUserImage());
        }
        user.setUserCreatetime(user2.getUserCreatetime());
        user.setUserId(user2.getUserId());
        request.setAttribute("detail", user);
        int res = meService.updateById(user);
        if (res > 0) {
            request.setAttribute("result", "更新成功");
        } else {
            request.setAttribute("result", "更新失败");
        }
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/my_detail");
        return model;
    }

    @RequestMapping("/admin/abilityAdd")
    public void addAbility(Ability ability, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Object obj = request.getSession().getAttribute("user");
        //获取session中的userID如果不存在或者不是Integer说明出现异常
        if (obj != null && obj instanceof User) {
            ability.setUserId((int) ((User) obj).getUserId());
        } else {
            throw new RuntimeException("session过期了");
        }
        Date date = new Date();
        ability.setAbilityUpdateTime(date);
        meService.addAbility(ability);
        response.sendRedirect("/admin/ability");
    }

    @RequestMapping("/admin/abilityDel")
    @ResponseBody
    public String delAbility(@RequestParam("id") String id) {
        int abId = Integer.parseInt(id);
        return meService.delAbilityById(abId) > 0 ? "success" : "fail";
    }

    @RequestMapping("/admin/flink")
    public ModelAndView flink(HttpServletRequest request) {
        List<Link> list = flinkService.findAll();
        request.setAttribute("flinks", list);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/my_flink");
        return model;
    }

    @RequestMapping("/admin/addFlink")
    public ModelAndView addFlinkPage(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/add_flink");
        return model;
    }

    @RequestMapping("/admin/flinkAdd")
    public void addFlink(Link link, HttpServletRequest request, @RequestParam("photoFile") MultipartFile file, HttpServletResponse response) throws IOException {
        Object obj = request.getSession().getAttribute("user");
        //获取session中的userID如果不存在或者不是Integer说明出现异常
        if (obj != null && obj instanceof User) {
            link.setUserId((int) ((User) obj).getUserId());
        } else {
            throw new RuntimeException("session过期了");
        }
        Date date = new Date();
        link.setLinkCreateTime(date);
        link.setLinkUpdateTime(date);
        FileUploadUtils fileUploadUtils = new FileUploadUtils();
        Map fileurl = fileUploadUtils.uploadFile(file);
        if (Integer.parseInt(fileurl.get("error").toString()) == 0) {
            //获取error存的值
            link.setLinkImg(fileurl.get("url").toString());
        } else {
            link.setLinkImg("");
        }
        flinkService.addFlink(link);
        response.sendRedirect("/admin/flink");
    }

    @RequestMapping("/admin/flinkDel")
    @ResponseBody
    public String delFlink(@RequestParam("id") String id) {
        int flinkId = Integer.parseInt(id);
        return flinkService.delFlink(flinkId) > 0 ? "success" : "fail";
    }

    @RequestMapping("/admin/updateFlinkPage")
    public ModelAndView updatePage(@RequestParam("id") Integer id, HttpServletRequest request) {
        Link link = flinkService.findById(id);
        request.setAttribute("updateLink", link);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/update_flink");
        return model;
    }

    @RequestMapping("/admin/updateFlink")
    public void updateFlink(Link link, HttpServletRequest request, HttpServletResponse response, @RequestParam("photoFile") MultipartFile file) throws Exception {

        Link link1 = flinkService.findById(link.getLinkId());

        if (!link.getLinkImg().equals(link1.getLinkImg())) {
            FileUploadUtils fileUploadUtils = new FileUploadUtils();
            Map fileurl = fileUploadUtils.uploadFile(file);
            link.setLinkImg(fileurl.get("url").toString());
        }
        link.setLinkCreateTime(link1.getLinkCreateTime());
        Date date = new Date();
        link.setLinkUpdateTime(date);
        link.setUserId(link1.getUserId());
        //修改文章
        flinkService.updateFlink(link);

        response.sendRedirect("/admin/flink");
    }
}
