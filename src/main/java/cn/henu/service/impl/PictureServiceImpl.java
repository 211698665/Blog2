package cn.henu.service.impl;

import cn.henu.pojo.Article;
import cn.henu.service.PictureService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.henu.dao.PhotoMapper;
import cn.henu.pojo.Photo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {

    @Autowired
    private PhotoMapper photoMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //上传图片
    public String addPhoto(Photo photo) {
        int res = photoMapper.insert(photo);
        if (res > 0) {
            //更新缓存
            if (photo.getPhotoStatus() == 1) {
                if (redisTemplate.hasKey("allPhoto")) {
                    redisTemplate.opsForList().leftPush("allPhoto", photo);
                }
            } else {
                if (redisTemplate.hasKey("allYsPhoto")) {
                    redisTemplate.opsForList().leftPush("allYsPhoto", photo);
                }
            }
            return "success";
        } else {
            return "fail";
        }
    }

    //查询图片列表
    public PageInfo<Photo> findAllPhoto(int pn, int pageSize) {
        List<Photo> list = new LinkedList<Photo>();
        List<Photo> listRight = new LinkedList<Photo>();

        //判断redis是否有名为allPhoto的缓存
        if (redisTemplate.hasKey("allPhoto")) {
            listRight = redisTemplate.opsForList().range("allPhoto", 0, -1);
        } else {
            //没有缓存需要加入缓存
            list = photoMapper.selectAll();
            PageHelper.startPage(pn, pageSize);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPhotoStatus() == 1) {
                    listRight.add(list.get(i));
                }
            }
            Collections.sort(listRight);
            redisTemplate.opsForList().rightPushAll("allPhoto", listRight);
        }
        //计算一下分页数据
        Long numall = redisTemplate.opsForList().size("allPhoto");
        //最大页数
        long pages = 0;
        if (numall % pageSize > 0) {
            pages = numall / pageSize + 1;
        } else {
            pages = numall / pageSize;
        }
        if (pn < pages) {
            listRight = listRight.subList((pn - 1) * pageSize, pn * pageSize);//截取list
        } else {
            listRight = listRight.subList((pn - 1) * pageSize, Integer.parseInt(numall + ""));
        }

        PageInfo<Photo> pageInfoList = new PageInfo<Photo>(listRight);

        return pageInfoList;
    }

    //查询图片列表
    public PageInfo<Photo> findYsAllPhoto(int pn, int pageSize) {
        List<Photo> list = new LinkedList<Photo>();
        List<Photo> listRight = new LinkedList<Photo>();

        if (redisTemplate.hasKey("allYsPhoto")) {
            listRight = redisTemplate.opsForList().range("allYsPhoto", 0, -1);
        } else {
            //没有缓存需要加入缓存
            list = photoMapper.selectAll();
            PageHelper.startPage(pn, pageSize);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPhotoStatus() == 2) {
                    listRight.add(list.get(i));
                }
            }
            Collections.sort(listRight);
            redisTemplate.opsForList().rightPushAll("allYsPhoto", listRight);
        }
        //计算一下分页数据
        Long numall = redisTemplate.opsForList().size("allYsPhoto");
        //最大页数
        long pages = 0;
        if (numall % pageSize > 0) {
            pages = numall / pageSize + 1;
        } else {
            pages = numall / pageSize;
        }
        if (pn < pages) {
            listRight = listRight.subList((pn - 1) * pageSize, pn * pageSize);//截取list
        } else {
            listRight = listRight.subList((pn - 1) * pageSize, Integer.parseInt(numall + ""));
        }

        PageInfo<Photo> pageInfoList = new PageInfo<Photo>(listRight);

        return pageInfoList;
    }

    @Override
    public List<Photo> findPhotoByIf(Photo photo) {
        return photoMapper.selectByPhotoIf(photo);
    }

    public List<Photo> adminFindAllPhoto() {
        List<Photo> list = new LinkedList<Photo>();
        list = photoMapper.selectAll();
        return list;
    }

    @Override
    public List<Photo> findTopFour() {
        List<Photo> list = new LinkedList<Photo>();
        List<Photo> listRight = new LinkedList<Photo>();
        //判断redis是否有名为allPhoto的缓存
        if (redisTemplate.hasKey("allPhoto")) {
            listRight = redisTemplate.opsForList().range("allPhoto", 0, -1);
        } else {
            //没有缓存需要加入缓存
            list = photoMapper.selectAll();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPhotoStatus() == 1) {
                    listRight.add(list.get(i));
                }
            }
            Collections.sort(listRight);
            redisTemplate.opsForList().rightPushAll("allPhoto", listRight);
        }

        listRight = listRight.subList(0, 4);//截取list0,1,2,3
        return listRight;
    }

    @Override
    public int countPhoto() {
        if (redisTemplate.hasKey("allPhoto")) {
            return Integer.parseInt(redisTemplate.opsForList().size("allPhoto") + "");
        } else {
            List<Photo> list = photoMapper.selectAll();
            int count = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPhotoStatus() == 1) {
                    count++;
                }
            }
            return count;
        }
    }

    @Override
    public int countYsPhoto() {
        if (redisTemplate.hasKey("allYsPhoto")) {
            return Integer.parseInt(redisTemplate.opsForList().size("allYsPhoto") + "");
        } else {
            List<Photo> list = photoMapper.selectAll();
            int count = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPhotoStatus() == 2) {
                    count++;
                }
            }
            return count;
        }
    }

    @Override
    public int delPhoto(int id) {
        Photo photo = photoMapper.selectByPrimaryKey(id);
        if (photo.getPhotoStatus() == 1) {
            if (redisTemplate.hasKey("allPhoto") && photoMapper.deleteByPrimaryKey(id) > 0) {
                //从缓存中删除
                redisTemplate.opsForList().trim("allPhoto", 1, 0);//删除全部
                List<Photo> list = new LinkedList<Photo>();
                List<Photo> listRight = new LinkedList<Photo>();
                list = photoMapper.selectAll();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPhotoStatus() == 1) {
                        listRight.add(list.get(i));
                    }
                }
                Collections.sort(listRight);
                //重新添加缓存
                redisTemplate.opsForList().rightPushAll("allPhoto", listRight);
                return 1;
            } else {
                return 0;
            }
        } else {
            if (redisTemplate.hasKey("allYsPhoto") && photoMapper.deleteByPrimaryKey(id) > 0) {
                //从缓存中删除
                redisTemplate.opsForList().trim("allYsPhoto", 1, 0);//删除全部
                List<Photo> list = new LinkedList<Photo>();
                List<Photo> listRight = new LinkedList<Photo>();
                list = photoMapper.selectAll();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPhotoStatus() == 2) {
                        listRight.add(list.get(i));
                    }
                }
                Collections.sort(listRight);
                //重新添加缓存
                redisTemplate.opsForList().rightPushAll("allYsPhoto", listRight);
                return 1;
            } else {
                return 0;
            }
        }

    }

    @Override
    public int updatePhoto(Photo photo) {
        int res = photoMapper.updateByPrimaryKey(photo);
        if (res > 0) {
            redisTemplate.opsForList().trim("allPhoto", 1, 0);//删除全部
            List<Photo> list = new LinkedList<Photo>();
            List<Photo> listRight = new LinkedList<Photo>();
            list = photoMapper.selectAll();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPhotoStatus() == 1) {
                    listRight.add(list.get(i));
                }
            }
            Collections.sort(listRight);
            //重新添加公开的缓存
            redisTemplate.opsForList().rightPushAll("allPhoto", listRight);

            redisTemplate.opsForList().trim("allYsPhoto", 1, 0);//删除全部
            listRight.clear();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPhotoStatus() == 2) {
                    listRight.add(list.get(i));
                }
            }
            Collections.sort(listRight);
            //重新添加隐私的缓存
            redisTemplate.opsForList().rightPushAll("allYsPhoto", listRight);
        }
        return res;
    }

    @Override
    public Photo findPhotoById(int id) {
        return photoMapper.selectByPrimaryKey(id);
    }

}
