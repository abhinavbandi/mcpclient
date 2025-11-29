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

        Arrays.stream(tools.getToolCallbacks()).forEach(t -> {
            log.info("Tool Callback found: {}", t.getToolDefinition());
        });

        this.chatClient = builder
                .defaultToolCallbacks(tools)
                .build();
    }

    @GetMapping("/chat")
    public String chat() {
        return chatClient.prompt()
                .user("find the meeting email from manager")
                .call()
                .content();
    }

    @GetMapping("/folders")
    public String listFolders() {
        return chatClient.prompt()
                .user("Call outlook_list_folders tool.")
                .call()
                .content();
    }

    @GetMapping("/summary")
    public String summary() {
        return chatClient.prompt()
                .user("Call outlook_mailbox_summary tool.")
                .call()
                .content();
    }

    @GetMapping("/emails/{id}")
    public String getEmail(@PathVariable String id) {
        return chatClient.prompt()
                .user("Fetch email details for id: " + id + " using outlook_get_email.")
                .call()
                .content();
    }

    @GetMapping("/emails/search")
    public String searchEmails(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String folder,
            @RequestParam(required = false) Boolean unread,
            @RequestParam(required = false) Integer max
    ) {
        String prompt = "Search emails using these filters:\n" +
                "- query: " + query + "\n" +
                "- folder: " + folder + "\n" +
                "- unread: " + unread + "\n" +
                "- max: " + max + "\n" +
                "Use outlook_search_emails tool.";

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
