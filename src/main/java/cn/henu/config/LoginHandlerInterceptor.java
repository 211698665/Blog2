package cn.henu.config;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//自定义拦截器
public class LoginHandlerInterceptor extends HandlerInterceptorAdapter {
    private List<String> url = new ArrayList();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判斷是否出現錯誤頁面
        List<Integer> errorCodeList = Arrays.asList(404, 403, 500);
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (errorCodeList.contains(statusCode)) {
            request.getRequestDispatcher("/user/exception").forward(request, response);
            return false;
        }
        Object user = request.getSession().getAttribute("user");
        //获取请求的url，查看是否为指向后台的请求
        String reqUrl = request.getRequestURL().toString();
        if (reqUrl.contains("admin") && user == null) {
            //未登录
            request.setAttribute("msg", "没有权限请先登陆");
            request.getRequestDispatcher("/admin/login").forward(request, response);
            return false;
        } else {
            //登陆
            return true;
        }
        //return super.preHandle(request, response, handler); //因为还没有写登录，所以先都返回true
    }

    public List<String> getUrl() {
        url.add("/admin/login");      //登录页
        url.add("/login/check");    //验证方法
        url.add("/user/*");   //前台页面

        //网站静态资源,也就是static目录下的东西
        url.add("/static/**");
        url.add("/webjars/**");
        url.add("/image/**");
        return url;
    }
}

