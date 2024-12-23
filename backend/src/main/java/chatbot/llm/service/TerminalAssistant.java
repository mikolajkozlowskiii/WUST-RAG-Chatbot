package chatbot.llm.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!web")  // Active when the 'web' profile is not enabled
public class TerminalAssistant implements Assistant {

    @Override
    public String answer(String prompt) {
        return "Terminal Response: " + prompt;
    }
}
