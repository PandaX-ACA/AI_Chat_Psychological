package org.example.aispringboot.AiService;

import org.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import org.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispringboot.Service.ConsultationSessionService;
import org.example.aispringboot.Service.ConsultationMessageService;
import org.example.aispringboot.entity.ConsultationSession;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PsychologicalSupportService {
    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;

    @Autowired
    private ConsultationSessionService consultationSessionService;

    // ✅ 注入ConsultationMessageService Bean
    @Autowired
    private ConsultationMessageService consultationMessageService;

    public StructOutput.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        //创建数据库会话记录
        ConsultationSession consultationSession = consultationSessionService.createSession(userId, createDTO);

        // ✅ 使用实例对象调用，不要类名.方法
        consultationMessageService.saveUserMessage(consultationSession.getId(), createDTO.getInitialMessage(), null);

        //创建会话信息
        String sessionId = "session_" + consultationSession.getId();
        return new StructOutput.StreamChatSession(
                sessionId,
                userId,
                createDTO.getInitialMessage(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000L,//24hours
                1,
                "ACTIVE"
        );
    }

public Flux<String> streamPsychologicalChat(String sessionId, String userMessage) {
    // 创建响应流
    return Flux.create(sink -> {
        // sink.next("数据1") // 发布数据
        // sink.complete(); // 完成流
        // sink.error(exception); // 发布错误
        Long dbSessionId = extractSessionId(sessionId);
        if (dbSessionId == null) {
            sink.error(new RuntimeException("会话ID格式错误"));
            return;
        }
        // 是否为初始消息
        boolean isInitialMessage = false;
        // 检查是否为初始消息，避免重复保存
        Integer messageCount = consultationMessageService.getMessageCountBySessionId(dbSessionId);
        if (messageCount == 1) {
            ConsultationMessageResponseDTO lastMessage = consultationMessageService.getLastMessageBySessionId(dbSessionId);
            if (lastMessage != null && lastMessage.getSenderType() == 1 && userMessage.equals(lastMessage.getContent())) {
                isInitialMessage = true;
            }
        }
        if (!isInitialMessage) {
            // 保存用户消息到数据库
            consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
        }

        // 进行流式对话
        // 生成对话记忆管理
        String conversationId = "conversation_" + sessionId;
        // 构建系统提示词
        List<org.springframework.ai.chat.messages.Message> userMessages = new ArrayList<>();
        userMessages.add(new UserMessage(userMessage));
        chatMemory.add(conversationId, userMessages);
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
        ));

        //用于存储AI完成的响应
        StringBuilder fullResponse = new StringBuilder();

        // 使用chatClient发送消息到Open AI
        chatClient.prompt(prompt)
                .user(userMessage)
                .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                .stream()
                .content()
                .doOnNext(Fragment -> {
                    fullResponse.append(Fragment);
                    sink.next(Fragment);
                })
                .doOnComplete(() -> {
                    String completeRes = fullResponse.toString();
                    // 将AI返回的内容保存到数据库
                    consultationMessageService.saveAiMessage(dbSessionId, completeRes, "openai");
                    // 添加AI回复到chatMemory
                    List<Message> aiMessages = new ArrayList<>();
                    aiMessages.add(new AssistantMessage(completeRes));
                    chatMemory.add(conversationId, aiMessages);

                    sink.complete();
                })
                .doOnError(error -> {
                    sink.error(error);
                })
                .subscribe(); // 订阅并启动流
    });
}

    //获取参数中的sessionId
    public Long extractSessionId(String sessionId) {
        if(sessionId != null && sessionId.startsWith("session_")) {
            return Long.parseLong(sessionId.split("_")[1]);
        }
        return null;
    }

    public List<ConsultationSession> getSessionListByUserId(Long userId) {
        return consultationSessionService.listByUserId(userId);
    }

    public ConsultationSession getSessionById(Long id) {
        return consultationSessionService.getById(id);
    }


}


