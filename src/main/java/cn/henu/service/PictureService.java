package cn.henu.service;

import cn.henu.pojo.Photo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PictureService {

    public String addPhoto(Photo photo);

    public PageInfo<Photo> findAllPhoto(int pn, int pageSize);

    public PageInfo<Photo> findYsAllPhoto(int pn, int pageSize);

    public List<Photo> findPhotoByIf(Photo photo);

    public int countPhoto();

    public int countYsPhoto();

    public int delPhoto(int id);

    public int updatePhoto(Photo photo);

    public Photo findPhotoById(int id);

    public List<Photo> adminFindAllPhoto();

    public List<Photo> findTopFour();

}
