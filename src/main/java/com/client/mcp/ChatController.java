package com.client.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, ToolCallbackProvider tools) {

        Arrays.stream(tools.getToolCallbacks()).forEach(t ->
                log.info("Tool Callback found: {}", t.getToolDefinition())
        );

        this.chatClient = builder
                .defaultToolCallbacks(tools)   
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()        
                .content();
    }
}
