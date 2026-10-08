package org.example.aispringboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_article")
public class Article {
    @TableId
    private String id;
    private Long categoryId;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String tags;
    private Long authorId;
    private Integer readCount;
    private Integer status;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
