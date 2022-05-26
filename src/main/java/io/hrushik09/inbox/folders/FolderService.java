package io.hrushik09.inbox.folders;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderService {
    public List<Folder> fetchDefaultFolders(String userId) {
        return List.of(
                new Folder(userId, "Inbox", "white"),
                new Folder(userId, "Sent Items", "green"),
                new Folder(userId, "Important", "red")
        );
    }

}
