package chatbot.llm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {
    private final Assistant assistant;

    public String getResponse(String prompt) {
        log.info("Session ID: {}, Assistant Bean HashCode: {}", getSessionId(), assistant.hashCode());

        return assistant.answer(prompt);
    }

    private String getSessionId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }
}
