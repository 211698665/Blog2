package cn.henu.controller.admin;

import cn.henu.pojo.User;
import cn.henu.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller

public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping("/admin/login")
    public ModelAndView login() {
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/login");
        return model;
    }

    @RequestMapping("/login/check")
    public void loginCheck(User user, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        int res = loginService.checkUser(user);
        User user2 = loginService.findByEmail(user.getUserMail());
        if (res > 0) {
            request.getSession().setAttribute("user", user2);
            response.sendRedirect("/admin/index");
        } else {
            response.sendRedirect("/admin/login");
        }
    }
}
