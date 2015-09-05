package gg.uhc.migration.messages;

@SuppressWarnings("unused")
public enum Senders {
    ACTIONBAR(new ActionbarMessageSender()),
    TITLE(new TitleMessageSender()),
    SUBTITLE(new SubtitleMessageSender()),
    CHAT(new ChatMessageSender())
    ;

    public final MessageSender sender;

    Senders(MessageSender sender) {
        this.sender = sender;
    }
}

