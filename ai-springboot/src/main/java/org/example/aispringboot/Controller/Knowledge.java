package org.example.aispringboot.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aispringboot.entity.Article;
import org.example.aispringboot.Service.ArticleService;
import org.example.aispringboot.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/knowledge")
public class Knowledge {

    @Resource
    private ArticleService articleService;

    @GetMapping("/article/page")
    public Result<IPage<Article>> getArticlePage(
            @RequestParam("sortField") String sortField,
            @RequestParam("sortDirection") String sortDirection,
            @RequestParam("currentPage") Integer currentPage,
            @RequestParam("size") Integer size,
            @RequestParam(value = "total", required = false) Integer total
    ){
        Page<Article> page = new Page<>(currentPage, size);
        IPage<Article> pageResult = articleService.getArticlePage(page, sortField, sortDirection);
        return Result.ok(pageResult);
    }
}
