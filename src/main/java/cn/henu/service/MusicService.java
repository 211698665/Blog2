package cn.henu.service;

import cn.henu.pojo.Music;

import java.util.List;

public interface MusicService {
    int addMusic(Music music);

    List<Music> findAllMusic();

    int delById(int id);

    int updateMusic(Music music);

    List<Music> frontFindAllMusic();

    Music findMusicById(int id);
}
