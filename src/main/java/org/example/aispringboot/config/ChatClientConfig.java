//package org.example.aispringboot.config;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.ai.chat.memory.InMemoryChatMemory;
//import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.openai.OpenAiChatModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ChatClientConfig {
//    @Bean
//    public ChatMemory chatMemory() {
//        return MessageWindowChatMemory.builder()
//                .maxMessages(30)//保留最近三十条消息;
//                .build();
//    }
//
//    @Bean
//    public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
//        return ChatClient.builder(openAiChatModel)
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory()).build())
//                .defaultSystem("你是一个专业的心理疏导师，温和耐心，善于倾听，能够提供专业的心理支持与建议")
//                .build();
//    }
//}
//
//
package org.example.aispringboot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatMemory chatMemory() {
        // M5版本：内存存储全部对话，无自动消息窗口截断
        return new InMemoryChatMemory();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        return new MessageChatMemoryAdvisor(chatMemory);
    }

    @Bean("open-ai")
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, MessageChatMemoryAdvisor messageChatMemoryAdvisor) {
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(messageChatMemoryAdvisor)
                .defaultSystem("你是一个专业的心理疏导师，温和耐心，善于倾听，能够提供专业的心理支持与建议")
                .build();
    }

}
