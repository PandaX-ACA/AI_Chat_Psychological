package org.example.aispringboot.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.aispringboot.entity.Article;

public interface ArticleService {
    IPage<Article> getArticlePage(Page<Article> page, String sortField, String sortDirection);
}
