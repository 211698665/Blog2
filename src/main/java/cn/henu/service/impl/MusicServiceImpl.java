package cn.henu.service.impl;

import cn.henu.dao.MusicMapper;
import cn.henu.pojo.Music;
import cn.henu.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {
    @Autowired
    private MusicMapper musicMapper;

    @Override
    public int addMusic(Music music) {
        return musicMapper.insert(music);
    }

    @Override
    public List<Music> findAllMusic() {
        return musicMapper.selectAllMusic();
    }

    @Override
    public int delById(int id) {
        return musicMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateMusic(Music music) {
        return musicMapper.updateByPrimaryKey(music);
    }

    @Override
    public List<Music> frontFindAllMusic() {
        return musicMapper.frontSelectAllMusic();
    }

    @Override
    public Music findMusicById(int id) {
        return musicMapper.selectByPrimaryKey(id);
    }
}
