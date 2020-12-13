package cn.henu.controller.admin;

import cn.henu.pojo.Music;
import cn.henu.pojo.Photo;
import cn.henu.pojo.User;
import cn.henu.service.MusicService;
import cn.henu.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class MusicController {
    @Autowired
    private MusicService musicService;

    @RequestMapping("/admin/music")
    public ModelAndView adminMusic() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/music");
        List<Music> list = musicService.findAllMusic();
        modelAndView.addObject("musicList", list);
        return modelAndView;
    }

    @RequestMapping("admin/addMusic")
    //注意这里的musicFiles要和html里面的name一样，或者用@RequsetParam()
    public void addMusic(Music music, MultipartFile[] musicFiles, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object obj = request.getSession().getAttribute("user");
        //获取session中的userID如果不存在或者不是Integer说明出现异常
        if (obj != null && obj instanceof User) {
            music.setUserId((int) ((User) obj).getUserId());
        } else {
            throw new RuntimeException("session过期了");
        }
        FileUploadUtils fileUploadUtils = new FileUploadUtils();
        for (int i = 0; i < musicFiles.length; i++) {
            Map fileurl = fileUploadUtils.uploadFile(musicFiles[i]);
            String error = fileurl.get("error").toString();
            String temp = fileurl.get("url").toString();
            String fileType = temp.substring(temp.lastIndexOf("."));
            if (Integer.parseInt(error) == 0 && fileType.contains("mp3")) {
                music.setMusicSrc(temp);
            } else {
                music.setMusicImg(temp);
            }
        }
        int res = musicService.addMusic(music);
        response.sendRedirect("/admin/music");
    }

    @RequestMapping("/admin/updateMusicPage")
    public ModelAndView updateMusicPage(@RequestParam("id") Integer id, HttpServletRequest request) {
        Music music = musicService.findMusicById(id);
        request.setAttribute("updateMusic", music);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/update_music");
        return model;
    }

    @RequestMapping("/admin/updateMusic")
    public void updateMusic(Music music, HttpServletRequest request, HttpServletResponse response, MultipartFile[] musicFiles) throws IOException {
        Music music2 = musicService.findMusicById(music.getMusicId());
        Date date = new Date();
        music.setUserId(music2.getUserId());
        FileUploadUtils fileUitls = new FileUploadUtils();
        if (!music.getMusicImg().equals(music2.getMusicImg())) {
            Map fileurl = fileUitls.uploadFile(musicFiles[0]);
            String error = fileurl.get("error").toString();
            String temp = fileurl.get("url").toString();
            String fileType = temp.substring(temp.lastIndexOf("."));
            if (Integer.parseInt(error) == 0 && fileType.contains("mp3")) {
                music.setMusicSrc(temp);
            } else {
                music.setMusicImg(temp);
            }
        } else {
            music.setMusicImg(music2.getMusicImg());
        }
        if (!music.getMusicSrc().equals(music2.getMusicSrc())) {
            Map fileurl = fileUitls.uploadFile(musicFiles[1]);
            String error = fileurl.get("error").toString();
            String temp = fileurl.get("url").toString();
            String fileType = temp.substring(temp.lastIndexOf("."));
            if (Integer.parseInt(error) == 0 && fileType.contains("mp3")) {
                music.setMusicSrc(temp);
            } else {
                music.setMusicImg(temp);
            }
        } else {
            music.setMusicSrc(music2.getMusicSrc());
        }
        //调用service进行添加操作
        int res = musicService.updateMusic(music);
        response.sendRedirect("/admin/music");
    }

    @RequestMapping("/admin/musicDel")
    @ResponseBody
    public String delMusic(@RequestParam("id") String id) {
        int musicId = Integer.parseInt(id);
        return musicService.delById(musicId) > 0 ? "success" : "fail";
    }

}
