package cn.henu.controller.admin;

import cn.henu.pojo.Saying;
import cn.henu.pojo.Timeline;
import cn.henu.pojo.User;
import cn.henu.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 这里包括每日一言和时光机器（个人日志）,因为比较类似，没必要弄两个了
 */
@Controller
public class TimeController {
    @Autowired
    private TimelineService timeLineService;

    @RequestMapping("/admin/saying")
    public ModelAndView saying(HttpServletRequest request) throws ParseException {
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Saying> list = timeLineService.findAllSaying();
        String date = "";
        for (int i = 0; i < list.size(); i++) {
            date = formatdate.format(list.get(i).getSayingUpdateTime());
            Date newDate = formatdate.parse(date);
            java.sql.Date formaldate = new java.sql.Date(newDate.getTime());
            list.get(i).setSayingUpdateTime(formaldate);
        }
        request.setAttribute("sayings", list);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/saying");
        return model;
    }

    @RequestMapping("/admin/sayingDel")
    @ResponseBody
    public String delSaying(@RequestParam("id") String id) {
        int sayingId = Integer.parseInt(id);
        return timeLineService.delSaying(sayingId) > 0 ? "success" : "fail";
    }

    @RequestMapping("/admin/addSaying")
    public void addSaying(Saying saying, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Date date = new Date();
        Object obj = request.getSession().getAttribute("user");
        //获取session中的userID如果不存在或者不是Integer说明出现异常
        if (obj != null && obj instanceof User) {
            saying.setUserId((int) ((User) obj).getUserId());
        } else {
            throw new RuntimeException("session过期了");
        }
        saying.setSayingCreateTime(date);
        saying.setSayingUpdateTime(date);
        int res = timeLineService.insertSaying(saying);
        if (res > 0) {
            request.setAttribute("result", "success");
        } else {
            request.setAttribute("result", "fail");
        }
        response.sendRedirect("/admin/saying");
    }

    @RequestMapping("/admin/updateSaying")
    @ResponseBody
    public String updateNotice(String id, HttpServletRequest request) {
        request.setAttribute("updateSaying", timeLineService.findSayingById(Integer.parseInt(id)));
        return "/admin/saying_Info?id=" + id;
    }

    @RequestMapping("/admin/saying_Info")
    public ModelAndView updateNoticeInfo(String id, HttpServletRequest request) {
        request.setAttribute("updateSaying", timeLineService.findSayingById(Integer.parseInt(id)));
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/saying_Info");
        return model;
    }

    @RequestMapping("/admin/update_saying")
    @ResponseBody
    public String update(Saying saying, HttpServletRequest request) {
        Saying saingOrigin = timeLineService.findSayingById(saying.getSayingId());
        Date date = new Date();
        saying.setSayingUpdateTime(date);
        saying.setSayingCreateTime(saingOrigin.getSayingCreateTime());
        saying.setUserId(saingOrigin.getUserId());
        int res = timeLineService.updateSaying(saying);
        request.setAttribute("updateSaying", saying);
        return res + "";
    }

    @RequestMapping("/admin/timeline")
    public ModelAndView timeLine(HttpServletRequest request) throws ParseException {
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Timeline> list = timeLineService.findAllTimeLine();
        String date = "";
        for (int i = 0; i < list.size(); i++) {
            date = formatdate.format(list.get(i).getTimelineUpdateTime());
            Date newDate = formatdate.parse(date);
            java.sql.Date formaldate = new java.sql.Date(newDate.getTime());
            list.get(i).setTimelineUpdateTime(formaldate);
        }
        Collections.sort(list, new Comparator<Timeline>() {
            @Override
            public int compare(Timeline o1, Timeline o2) {
                return o2.getTimelineId() - o1.getTimelineId(); //根据id倒序
            }
        });
        request.setAttribute("timelines", list);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/timeline");
        return model;
    }

    @RequestMapping("/admin/timelineDel")
    @ResponseBody
    public String delTimeline(@RequestParam("id") String id) {
        int timelineId = Integer.parseInt(id);
        return timeLineService.delTimeline(timelineId) > 0 ? "success" : "fail";
    }

    @RequestMapping("/admin/addTimeline")
    public void addTimeline(Timeline timeline, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Date date = new Date();
        timeline.setTimelineUpdateTime(date);
        Object obj = request.getSession().getAttribute("user");
        //获取session中的userID如果不存在或者不是Integer说明出现异常
        if (obj != null && obj instanceof User) {
            timeline.setUserId((int) ((User) obj).getUserId());
        } else {
            throw new RuntimeException("session过期了");
        }
        int res = timeLineService.insertTimeline(timeline);
        if (res > 0) {
            request.setAttribute("result", "success");
        } else {
            request.setAttribute("result", "fail");
        }
        response.sendRedirect("/admin/timeline");
    }

    @RequestMapping("/admin/updateTimeline")
    @ResponseBody
    public String updateTimeline(String id, HttpServletRequest request) {
        request.setAttribute("updateTimeline", timeLineService.findTimelineById(Integer.parseInt(id)));
        return "/admin/timeline_Info?id=" + id;
    }

    @RequestMapping("/admin/timeline_Info")
    public ModelAndView updateTimelineInfo(String id, HttpServletRequest request) {
        request.setAttribute("updateTimeline", timeLineService.findTimelineById(Integer.parseInt(id)));
        ModelAndView model = new ModelAndView();
        model.setViewName("admin/timeline_Info");
        return model;
    }

    @RequestMapping("/admin/update_timeline")
    @ResponseBody
    public String update(Timeline timeline, HttpServletRequest request) {
        Timeline timeOrigin = timeLineService.findTimelineById(timeline.getTimelineId());
        //还是原来的时间，不更新
        timeline.setTimelineUpdateTime(timeOrigin.getTimelineUpdateTime());
        //userId还是原来的
        timeline.setUserId(timeOrigin.getUserId());
        int res = timeLineService.updateTimeline(timeline);
        request.setAttribute("updateTimeline", timeline);
        return res + "";
    }
}
