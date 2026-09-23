package org.example.aispringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aispringboot.entity.Article;
import org.example.aispringboot.mapper.ArticleMapper;
import org.example.aispringboot.Service.ArticleService;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public IPage<Article> getArticlePage(Page<Article> page, String sortField, String sortDirection) {
        QueryWrapper<Article> qw = new QueryWrapper<>();

        // 白名单：数据库字段名（下划线），只允许下面这几个字段排序
        boolean isValidSortField = "id".equals(sortField)
                || "read_count".equals(sortField)
                || "published_at".equals(sortField)
                || "created_at".equals(sortField);

        if (isValidSortField) {
            boolean isAsc = "asc".equalsIgnoreCase(sortDirection);
            qw.orderBy(true, isAsc, sortField);
        }

        // 只查询已发布状态的文章 status=1
        qw.eq("status", 1);

        return articleMapper.selectPage(page, qw);
    }
}
