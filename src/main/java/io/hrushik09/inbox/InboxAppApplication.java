package io.hrushik09.inbox;

import io.hrushik09.inbox.email.EmailService;
import io.hrushik09.inbox.folders.Folder;
import io.hrushik09.inbox.folders.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
@RestController
public class InboxAppApplication {

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    EmailService emailService;

    public static void main(String[] args) {
        SpringApplication.run(InboxAppApplication.class, args);
    }

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

    @PostConstruct
    public void init() {
        folderRepository.save(new Folder("hrushik09", "Work", "blue"));
        folderRepository.save(new Folder("hrushik09", "Home", "green"));
        folderRepository.save(new Folder("hrushik09", "Family", "yellow"));

        for (int i = 0; i < 10; i++) {
            emailService.sendEmail("hrushik09", List.of("hrushik09", "abc"), "Hello " + i, "body " + i);
        }
    }
}
