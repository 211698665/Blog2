package cn.henu.service;

import cn.henu.pojo.Article;
import cn.henu.pojo.Category;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ArticleService {
    PageInfo<Article> findAllArticleByPage(int pn, int pageSize);

    List<Article> findAllArticle();

    int countArticles(int articleStatus);

    int addArticle(Article article);

    Article findById(int id);

    int updateViewCount(int id, int count);

    int delByArticleId(int articleId);

    int updateByPrimaryKey(Article article);

    int findArticleByKeyWordSize(String keyword);

    List<Article> findArticleByKeyWord(String keyword);

    List<Article> findArticleByCategoryId(int categoryId);

    List<Article> findByIf(Article article);

    List<Article> adminFindAllArticle();
}
