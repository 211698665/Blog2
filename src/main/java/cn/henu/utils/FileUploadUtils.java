package cn.henu.utils;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileUploadUtils {
    public Map uploadFile(@RequestParam("photoFile") MultipartFile file) {
        String rootPath = "C:/image/";
        String filename = file.getOriginalFilename();
        int pos = filename.lastIndexOf(".");//获取最后一个.
        String suffix = filename.substring(pos);
        String path = rootPath + UUID.randomUUID().toString().replaceAll("\\-", "") + suffix;
        try {
            file.transferTo(new File(path));
            Map map = new HashMap<>();
            map.put("error", 0);
            path = "http://139.196.89.241/" + path.substring(3);
            map.put("url", path);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            Map map = new HashMap<>();
            map.put("error", 1);
            map.put("message", "图片上传失败");
            return map;
        }
    }
}
