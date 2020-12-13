package cn.henu.service.impl;

import cn.henu.dao.ArticleMapper;
import cn.henu.dao.ArticlecategoryrefMapper;
import cn.henu.pojo.Article;
import cn.henu.pojo.Articlecategoryref;
import cn.henu.pojo.Category;
import cn.henu.service.ArticleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticlecategoryrefMapper articlecategoryrefMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageInfo<Article> findAllArticleByPage(int pn, int pageSize) {
        PageHelper.startPage(pn, pageSize);
        List<Article> list = new LinkedList<Article>();
        List<Article> listRight = new LinkedList<Article>();
        //判断redis是否有名为articles的缓存
        if (redisTemplate.hasKey("articles" + pn)) {
            listRight = redisTemplate.opsForList().range("articles" + pn, 0, -1);
        } else {
            //没有缓存需要加入缓存
            list = articleMapper.selectAll();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getArticleStatus() == 1) {
                    listRight.add(list.get(i));
                }
            }
            redisTemplate.opsForList().rightPushAll("articles" + pn, listRight);
        }
        PageInfo<Article> pageInfoList = new PageInfo<Article>(listRight);
        return pageInfoList;
    }

    @Override
    public List<Article> findAllArticle() {
        return articleMapper.findByStatus(1);
    }

    @Override
    public List<Article> adminFindAllArticle() {
        return articleMapper.selectAll();
    }

    @Override
    public int countArticles(int articleStatus) {
        return articleMapper.countArticles(articleStatus);
    }

    @Override
    public int addArticle(Article article) {

        return articleMapper.insert(article);
    }

    @Override
    public Article findById(int id) {
        return articleMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateViewCount(int id, int count) {
        return articleMapper.updateViewCount(id, count);
    }

    @Override
    public int delByArticleId(int articleId) {
        return articleMapper.deleteByPrimaryKey(articleId);
    }

    @Override
    public int updateByPrimaryKey(Article article) {
        return articleMapper.updateByPrimaryKey(article);
    }

    @Override
    public int findArticleByKeyWordSize(String keyword) {
        return articleMapper.findArticleByKeyWord(keyword).size();
    }

    @Override
    public List<Article> findArticleByKeyWord(String keyword) {
        return articleMapper.findArticleByKeyWord(keyword);
    }

    @Override
    public List<Article> findArticleByCategoryId(int categoryId) {
        List<Articlecategoryref> articlecategoryrefs = articlecategoryrefMapper.selectByCategoryId(categoryId);
        List<Article> listCategorys = new LinkedList<Article>();
        for (int i = 0; i < articlecategoryrefs.size(); i++) {
            listCategorys.add(articleMapper.selectByPrimaryKey(articlecategoryrefs.get(i).getArticleId()));
        }
        return listCategorys;
    }

    @Override
    public List<Article> findByIf(Article article) {
        return articleMapper.selectByIf(article);
    }
}
