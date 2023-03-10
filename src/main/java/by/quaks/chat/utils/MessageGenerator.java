package by.quaks.chat.utils;

import by.quaks.files.ChatFormatting;
import by.quaks.files.MainConfig;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MessageGenerator {
    public static enum MessageType {
        Global, Local, Custom
    }
    public static TextComponent genPlayerChatMessage(MessageType type, Player sender, String message, String room){
        LocalTime time = LocalTime.now();
        int hoursOffset = ChatFormatting.get().getInt("Time.HoursOffset");
        int minutesOffset = ChatFormatting.get().getInt("Time.MinutesOffset");
        LocalTime utcTime = time.plusHours(hoursOffset).plusMinutes(minutesOffset);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = utcTime.format(formatter);

        String playerName = ChatFormatting.get().getString("PlayerName.String");
        assert playerName != null;
        playerName = playerName.replaceAll("%player-name%",sender.getName());
        TextComponent playerNameComponent = new TextComponent(playerName);
        playerNameComponent.setColor(ChatColor.of(ChatFormatting.get().getString("PlayerName.Color")));
        playerNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatFormatting.get().getString("PlayerName.HoverEvent.Value").replaceAll("%player-name%", sender.getName())).color(ChatColor.GRAY).create()));
        playerNameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,ChatFormatting.get().getString("PlayerName.ClickEvent.Value").replaceAll("%player-name%", sender.getName())));

        String chatMessage = ChatFormatting.get().getString("Message.String");
        assert chatMessage != null;
        if (type.equals(MessageType.Global)){message = message.substring(1);}
        chatMessage = chatMessage.replaceAll("%message%",message).replaceAll("%time%", formattedTime);
        TextComponent chatMessageComponent = new TextComponent(chatMessage);
        chatMessageComponent.setColor(ChatColor.of(ChatFormatting.get().getString("Message.Color")));
        chatMessageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatFormatting.get().getString("Message.HoverEvent.Value")).color(ChatColor.GRAY).create()));

        String form = ChatFormatting.get().getString("Formatting");
        TextComponent componentChatType = null;
        switch (type){
            case Custom:
                chatMessageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,ChatFormatting.get().getString("Message.ClickEvent.Value")
                        .replaceAll("%time%",formattedTime)
                        .replaceAll("%player-name%", sender.getName())
                        .replaceAll("%message%",message)
                        .replaceAll("%chat-form-prefix%",room)
                ));
                componentChatType = new TextComponent(room);
                componentChatType.setColor(ChatColor.of(ChatFormatting.get().getString("ChatType.Color.Custom")));
                componentChatType.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatFormatting.get().getString("ChatType.HoverEvent.Value").replaceAll("%chat-prefix%", room)).color(ChatColor.GRAY).create()));
                componentChatType.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,ChatFormatting.get().getString("ChatType.ClickEvent.Value").replaceAll("%chat-prefix%", room)));
                break;
            case Global:
                chatMessageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,ChatFormatting.get().getString("Message.ClickEvent.Value")
                        .replaceAll("%time%",formattedTime)
                        .replaceAll("%player-name%", sender.getName())
                        .replaceAll("%message%",message)
                        .replaceAll("%chat-form-prefix%",ChatFormatting.get().getString("ChatType.Type.Global"))
                ));
                componentChatType = new TextComponent(ChatFormatting.get().getString("ChatType.Type.Global"));
                componentChatType.setColor(ChatColor.of(ChatFormatting.get().getString("ChatType.Color.Global")));
                componentChatType.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatFormatting.get().getString("ChatType.HoverEvent.Value").replaceAll("%chat-prefix%", MainConfig.get().getString("GlobalChatPrefix"))).color(ChatColor.GRAY).create()));
                componentChatType.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,ChatFormatting.get().getString("ChatType.ClickEvent.Value").replaceAll("%chat-prefix%", MainConfig.get().getString("GlobalChatPrefix"))));
                break;
            case Local:
                chatMessageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,ChatFormatting.get().getString("Message.ClickEvent.Value")
                        .replaceAll("%time%",formattedTime)
                        .replaceAll("%player-name%", sender.getName())
                        .replaceAll("%message%",message)
                        .replaceAll("%chat-form-prefix%",ChatFormatting.get().getString("ChatType.Type.Local"))
                ));
                componentChatType = new TextComponent(ChatFormatting.get().getString("ChatType.Type.Local"));
                componentChatType.setColor(ChatColor.of(ChatFormatting.get().getString("ChatType.Color.Local")));
//                componentChatType.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatFormatting.get().getString("ChatType.HoverEvent.Value")
//                        .replaceAll("%time%",formattedTime)
//                        .replaceAll("%player-name%", sender.getName())
//                        .replaceAll("%message%",message)
//                        .replaceAll("%chat-form-prefix%",ChatFormatting.get().getString("ChatType.Type.Local"))).color(ChatColor.GRAY).create()));
//                componentChatType.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,ChatFormatting.get().getString("ChatType.ClickEvent.Value")
//                        .replaceAll("%time%",formattedTime)
//                        .replaceAll("%player-name%", sender.getName())
//                        .replaceAll("%message%",message)
//                        .replaceAll("%chat-form-prefix%",ChatFormatting.get().getString("ChatType.Type.Local"))));
                break;
        }
        return getTextComponent(playerNameComponent, chatMessageComponent, form, componentChatType);
    }

    @NotNull
    private static TextComponent getTextComponent(TextComponent playerNameComponent, TextComponent chatMessageComponent, String form, TextComponent componentChatType) {
        Map<String, TextComponent> placeholders = new HashMap<>();
        placeholders.put("chat-form-prefix",componentChatType);
        placeholders.put("player-name", playerNameComponent);
        placeholders.put("message", chatMessageComponent);
        TextComponent textComponent = ChatComponentUtils.replacePlaceholders(form, placeholders);
        textComponent.setColor(ChatColor.of(ChatFormatting.get().getString("Other.Color")));

        return textComponent;
    }

    public static TextComponent genPlayerChatMessage(MessageType type, Player sender, String message){
        LocalTime time = LocalTime.now();
        int hoursOffset = ChatFormatting.get().getInt("Time.HoursOffset");
        int minutesOffset = ChatFormatting.get().getInt("Time.MinutesOffset");
        LocalTime utcTime = time.plusHours(hoursOffset).plusMinutes(minutesOffset);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = utcTime.format(formatter);

        String playerName = ChatFormatting.get().getString("PlayerName.String");
        assert playerName != null;
        playerName = playerName.replaceAll("%player-name%",sender.getName());
        TextComponent playerNameComponent = new TextComponent(playerName);
        playerNameComponent.setColor(ChatColor.of(ChatFormatting.get().getString("PlayerName.Color")));
        playerNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatFormatting.get().getString("PlayerName.HoverEvent.Value").replaceAll("%player-name%", sender.getName())).color(ChatColor.GRAY).create()));
        playerNameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,ChatFormatting.get().getString("PlayerName.ClickEvent.Value").replaceAll("%player-name%", sender.getName())));

        String chatMessage = ChatFormatting.get().getString("Message.String");
        assert chatMessage != null;
        if (type.equals(MessageType.Global)){message = message.substring(1);}
        chatMessage = chatMessage.replaceAll("%message%",message).replaceAll("%time%", formattedTime);
        TextComponent chatMessageComponent = new TextComponent(chatMessage);
        chatMessageComponent.setColor(ChatColor.of(ChatFormatting.get().getString("Message.Color")));
        chatMessageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatFormatting.get().getString("Message.HoverEvent.Value")).color(ChatColor.GRAY).create()));

        String form = ChatFormatting.get().getString("Formatting");
        TextComponent componentChatType = null;
        switch (type){
            case Global:
                chatMessageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,ChatFormatting.get().getString("Message.ClickEvent.Value")
                        .replaceAll("%time%",formattedTime)
                        .replaceAll("%player-name%", sender.getName())
                        .replaceAll("%message%",message)
                        .replaceAll("%chat-form-prefix%",ChatFormatting.get().getString("ChatType.Type.Global"))
                ));
                componentChatType = new TextComponent(ChatFormatting.get().getString("ChatType.Type.Global"));
                componentChatType.setColor(ChatColor.of(ChatFormatting.get().getString("ChatType.Color.Global")));
                componentChatType.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatFormatting.get().getString("ChatType.HoverEvent.Value").replaceAll("%chat-prefix%", MainConfig.get().getString("GlobalChatPrefix"))).color(ChatColor.GRAY).create()));
                componentChatType.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,ChatFormatting.get().getString("ChatType.ClickEvent.Value").replaceAll("%chat-prefix%", MainConfig.get().getString("GlobalChatPrefix"))));
                break;
            case Local:
                chatMessageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,ChatFormatting.get().getString("Message.ClickEvent.Value")
                        .replaceAll("%time%",formattedTime)
                        .replaceAll("%player-name%", sender.getName())
                        .replaceAll("%message%",message)
                        .replaceAll("%chat-form-prefix%",ChatFormatting.get().getString("ChatType.Type.Local"))
                ));
                componentChatType = new TextComponent(ChatFormatting.get().getString("ChatType.Type.Local"));
                componentChatType.setColor(ChatColor.of(ChatFormatting.get().getString("ChatType.Color.Local")));
//                componentChatType.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatFormatting.get().getString("ChatType.HoverEvent.Value")
//                        .replaceAll("%time%",formattedTime)
//                        .replaceAll("%player-name%", sender.getName())
//                        .replaceAll("%message%",message)
//                        .replaceAll("%chat-form-prefix%",ChatFormatting.get().getString("ChatType.Type.Local"))).color(ChatColor.GRAY).create()));
//                componentChatType.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,ChatFormatting.get().getString("ChatType.ClickEvent.Value")
//                        .replaceAll("%time%",formattedTime)
//                        .replaceAll("%player-name%", sender.getName())
//                        .replaceAll("%message%",message)
//                        .replaceAll("%chat-form-prefix%",ChatFormatting.get().getString("ChatType.Type.Local"))));
                break;
        }
        return getTextComponent(playerNameComponent, chatMessageComponent, form, componentChatType);
    }
}
