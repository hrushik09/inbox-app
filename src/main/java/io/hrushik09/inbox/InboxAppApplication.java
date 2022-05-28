package io.hrushik09.inbox;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.hrushik09.inbox.emaillist.EmailListItem;
import io.hrushik09.inbox.emaillist.EmailListItemKey;
import io.hrushik09.inbox.emaillist.EmailListItemRepository;
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
    EmailListItemRepository emailListItemRepository;

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
        folderRepository.save(new Folder("hrushik09", "Inbox", "blue"));
        folderRepository.save(new Folder("hrushik09", "Sent", "green"));
        folderRepository.save(new Folder("hrushik09", "Important", "yellow"));

        for (int i = 0; i < 10; i++) {
            EmailListItemKey key = new EmailListItemKey();
            key.setId("hrushik09");
            key.setLabel("Inbox");
            key.setTimeUUID(Uuids.timeBased());

            EmailListItem item = new EmailListItem();
            item.setKey(key);
            item.setTo(List.of("hrushik09"));
            item.setSubject("This is a message " + i);
            item.setUnread(true);

            emailListItemRepository.save(item);
        }
    }
}
