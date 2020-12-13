package cn.henu.controller.user;

import cn.henu.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

@Controller
public class ChatController {
    @Autowired
    private MusicService musicService;

    @GetMapping("/user/chatindex")
    public ModelAndView chatIndex(HttpServletRequest request) {
        //获取用户的ip作为用户名
        String username = request.getRemoteAddr();
        ModelAndView mav = new ModelAndView("user/chat");
        int size = musicService.findAllMusic().size();
        Random random = new Random();
        int index = random.nextInt(size);
        mav.addObject("myMusic", musicService.findAllMusic().get(index));
        mav.addObject("username", username);
        mav.addObject("webSocketUrl", "ws://139.196.89.241:80/chat");
        return mav;
    }
}
