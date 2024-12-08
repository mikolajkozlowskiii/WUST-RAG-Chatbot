package chatbot.llm.controller;


import chatbot.llm.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat/")
public class PromptController {
    private ChatbotService chatbotService;

    public PromptController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<String> generateResponse(@RequestBody String prompt) {
        return ResponseEntity.ok(chatbotService.getResponse(prompt));
    }
}
