package gg.uhc.migration.messages

enum class Senders(val sender: MessageSender) {
    ACTIONBAR(ActionbarMessageSender.createInstance()),
    TITLE(TitleMessageSender()),
    SUBTITLE(SubtitleMessageSender()),
    CHAT(ChatMessageSender())
}

