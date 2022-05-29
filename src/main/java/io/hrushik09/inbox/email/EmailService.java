package io.hrushik09.inbox.email;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.hrushik09.inbox.emaillist.EmailListItem;
import io.hrushik09.inbox.emaillist.EmailListItemKey;
import io.hrushik09.inbox.emaillist.EmailListItemRepository;
import io.hrushik09.inbox.folders.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    EmailListItemRepository emailListItemRepository;

    @Autowired
    UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String from, List<String> to, String subject, String body) {
        Email email = new Email();
        email.setFrom(from);
        email.setTo(to);
        email.setSubject(subject);
        email.setBody(body);
        email.setId(Uuids.timeBased());
        emailRepository.save(email);

        to.forEach(toId -> {
            EmailListItem item = createEmailListItem(to, subject, email, toId, "Inbox");
            emailListItemRepository.save(item);
            unreadEmailStatsRepository.incrementUnreadCounter(toId, "Inbox");
        });

        EmailListItem sentItemsEntry = createEmailListItem(to, subject, email, from, "Sent Items");
        sentItemsEntry.setUnread(false);
        emailListItemRepository.save(sentItemsEntry);
    }

    private EmailListItem createEmailListItem(List<String> to, String subject, Email email, String itemOwner, String folder) {
        EmailListItemKey key = new EmailListItemKey();
        key.setId(itemOwner);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

        EmailListItem item = new EmailListItem();
        item.setKey(key);
        item.setTo(to);
        item.setSubject(subject);
        item.setUnread(true);
        return item;
    }

    public boolean doesHaveAccess(Email email, String userId) {
        return userId.equals(email.getFrom()) || email.getTo().contains(userId);
    }

    public String getReplySubject(String subject) {
        return "Re: " + subject;
    }

    public String getReplyBody(Email email) {
        return "\n\n\n-----------------------------------------\n" +
                "From: " + email.getFrom() + "\n" +
                "To: " + email.getTo() + "\n\n" +
                email.getBody();
    }
}
