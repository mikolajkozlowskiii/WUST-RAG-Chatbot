package chatbot.llm.configuration;


import chatbot.llm.service.Assistant;
import chatbot.llm.service.ChatbotService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

@Configuration
@Slf4j
public class AssistantConfiguration {
    private static final String SYSTEM_MESSAGE = """
            Jesteś milym chatbotem dla studentow, odpowiadasz tak jakbys byl pracownikiem dziekanatu.
            Udzielaj odpowiedzi zawsze tylko w języku polskim.
            Możesz rozmawiać ze studentami i udzielać im odpowiedzi na podstawie dostarczonych danych.
            Jeśli pojawi się pytanie, którego nie znasz odpowiedzi lub nie masz odpowiedniego dokumentu w swojej bazie,
            to odpowiadasz, że nie masz takich informacji.
            Zawsze odpowiadaj w takiej formie tekstowej, aby byla zrozumiala dla modelu text-to-speech.
            """;
    private static final String RESOURCES_PATH = "documents/";


    private final ChatLanguageModel chatLanguageModel;

    public AssistantConfiguration(@Qualifier("OpenAiChatModel") ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    private static Path toPath() throws URISyntaxException {
        URL fileUrl = ChatbotService.class.getClassLoader().getResource(RESOURCES_PATH);
        assert fileUrl != null;
        return Paths.get(fileUrl.toURI());
    }

    private static PathMatcher glob() {
        return FileSystems.getDefault().getPathMatcher("glob:" + "*.txt");
    }

    @Bean
    @SessionScope
    public Assistant assistant() throws URISyntaxException {
        final List<Document> documents = loadDocuments(toPath(), glob());

        return AiServices.builder(Assistant.class)
                .systemMessageProvider(chatMemoryId -> SYSTEM_MESSAGE)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(createContentRetriever(documents))
                .build();
    }

    private ContentRetriever createContentRetriever(List<Document> documents) {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }
}

