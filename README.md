##File Organizer
这是一个有关记录心理健康的项目

##功能介绍
###用户端：
- 引入AI聊天功能,用户可以将自己的心情通过对话框发给AI，AI会根据所发送的内容做出回应
- 设立“情绪日志”，用户可以自行记录当天心情
- 设立知识库功能，用户可以阅读有关于心理健康的文章

###管理端：
- 数据分析：统计使用的用户的趋势分析、对话情况以及用户活跃度
- 知识文章管理：对知识文章进行更新与删除
- 咨询记录查询：显示每个用户的咨询记录
- 情绪日志查询：可以使用用户的id对用户的情绪日志进行检索

##技术栈
- 后端：Spring Boot / Java
- 前端：Vue3 + TypeScript + Vite
- AI能力：大模型API（流式SSE对话）
- 数据库：MySQL + Redis
- 其他：Maven、JWT、Websocket/SSE

##运行环境
- Java > 17.0
- MySQL 8.0+
- Node.js 18+
- API Key

##快速启动
###1.克隆项目
```bash
git clone https://github.com/PandaX-ACA/AI_Chat_Psychological.git
cd AI_Chat_Psychological
```
###2.后端部署
1. 修改 `application.yml` 配置文件，填写数据库、Redis、大模型 API 信息
2. 执行 sql 目录下数据库初始化脚本
3. 启动 SpringBoot 主程序
###3.前端部署
```bash
cd frontend
npm install
npm run dev
```

##项目结构
├── ai-springboot/          # SpringBoot后端
├── ai-vue/         				# Vue3前端页面
├── sql/              			# 数据库脚本
└── README.md         			# 项目说明文档

