package chatbot.llm.configuration;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;


@Configuration
@RequiredArgsConstructor
public class Langchain4jConfiguration {
    @Value("${ollama.model.name}")
    private String ollamaModelName;

    @Value("${ollama.host.url}")
    private String ollamaBaseUrl;

    @Value("${openaiApiKey}")
    private String openAiApiKey;

    @Bean
    @Qualifier("OllamaChatModel")
    @SessionScope
    public ChatLanguageModel chatLanguageModelOllamaChatModel() {
        return OllamaChatModel.builder()
                .modelName(ollamaModelName)
                .baseUrl(ollamaBaseUrl)
                .build();
    }

    @Bean
    @Qualifier("AzureOpenAiChatModel")
    @SessionScope
    public ChatLanguageModel chatLanguageModelAzureOpenAiChatModel() {
        return AzureOpenAiChatModel.builder()
                .apiKey("")
                .deploymentName("")
                .endpoint("")
                .build();
    }

    @Bean
    @Qualifier("OpenAiChatModel")
    public ChatLanguageModel chatLanguageModelOpenAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(GPT_4_O_MINI)
                .temperature(0.0)
                .build();
    }
}
