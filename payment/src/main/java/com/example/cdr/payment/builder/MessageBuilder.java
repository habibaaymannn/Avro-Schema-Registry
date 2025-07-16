package com.example.cdr.payment.builder;

import com.example.avro.*;
import java.util.*;
import java.util.stream.Collectors;

public class MessageBuilder {

    @SuppressWarnings("unchecked")
    public static PaymentMessage build(Map<String, Object> input) {
        PaymentMessage.Builder builder = PaymentMessage.newBuilder();

        // === Top-level primitives ===
        builder.setEventId((String) input.getOrDefault("eventId", UUID.randomUUID().toString()));
        builder.setTimestamp((String) input.getOrDefault("timestamp", new Date().toString()));
        builder.setEventSource((String) input.getOrDefault("eventSource", "default-service"));
        builder.setPriority((String) input.getOrDefault("priority", "MEDIUM"));
        builder.setCategory((String) input.getOrDefault("category", "general"));
        builder.setScheduledSendTime((String) input.getOrDefault("scheduledSendTime", null));
        builder.setClientId((String) input.getOrDefault("clientId", null));
        List<String> channels = (List<String>) input.getOrDefault("channels", Collections.emptyList());
        builder.setChannels(channels.stream().map(s -> (CharSequence) s).collect(Collectors.toList()));
        builder.setHref((String) input.getOrDefault("href", ""));
        builder.setLanguage((String) input.getOrDefault("language", "en"));
        builder.setContent((String) input.getOrDefault("content", ""));
        builder.setUseCommonContent((Boolean) input.getOrDefault("useCommonContent", true));
        builder.setMessageType((String) input.getOrDefault("messageType", ""));
        builder.setExternalField((String) input.getOrDefault("externalField", ""));

        // === Sender ===
        builder.setSender(castToCharSeqMap((Map<String, Object>) input.getOrDefault("sender", new HashMap<>())));
        // assume simple/empty sender for now

        // === Receivers ===
        List<Map<String, Object>> receiversInput = (List<Map<String, Object>>) input.get("receiver");
        if (receiversInput != null) {
            builder.setReceiver(
                    receiversInput.stream().map(MessageBuilder::buildReceiver).collect(Collectors.toList())
            );
        }

        // === Attachments ===
        List<Map<String, Object>> attachmentsInput = (List<Map<String, Object>>) input.get("attachment");
        if (attachmentsInput != null) {
            builder.setAttachment(
                    attachmentsInput.stream().map(MessageBuilder::buildAttachment).collect(Collectors.toList())
            );
        }

        // === Notification Channels ===
        List<Map<String, Object>> channelsInput = (List<Map<String, Object>>) input.get("notificationChannels");
        if (channelsInput != null) {
            builder.setNotificationChannels(
                    channelsInput.stream().map(MessageBuilder::buildChannel).collect(Collectors.toList())
            );
        }

        return builder.build();
    }

    private static Receiver buildReceiver(Map<String, Object> input) {
        Receiver.Builder receiverBuilder = Receiver.newBuilder();

        if (input.containsKey("emailReceiver")) {
            Map<String, Object> email = (Map<String, Object>) input.get("emailReceiver");
            receiverBuilder.setEmailReceiver(EmailReceiver.newBuilder()
                    .setTo(castToCharSeqList((List<String>) email.getOrDefault("to", Collections.emptyList())))
                    .setCc(castToCharSeqList((List<String>) email.getOrDefault("cc", Collections.emptyList())))
                    .setBcc(castToCharSeqList((List<String>) email.getOrDefault("bcc", Collections.emptyList())))
                    .build());

        }

        if (input.containsKey("smsReceiver")) {
            Map<String, Object> sms = (Map<String, Object>) input.get("smsReceiver");
            receiverBuilder.setSmsReceiver(SmsReceiver.newBuilder()
                    .setPhoneNumber((String) sms.get("phoneNumber"))
                    .build());
        }

        if (input.containsKey("whatsappReceiver")) {
            Map<String, Object> wa = (Map<String, Object>) input.get("whatsappReceiver");
            receiverBuilder.setWhatsappReceiver(WhatsappReceiver.newBuilder()
                    .setPhoneNumber((String) wa.get("phoneNumber"))
                    .build());
        }

        if (input.containsKey("pushReceiver")) {
            Map<String, Object> push = (Map<String, Object>) input.get("pushReceiver");
            receiverBuilder.setPushReceiver(PushReceiver.newBuilder()
                    .setAppUserId((String) push.get("appUserId"))
                    .build());
        }

        return receiverBuilder.build();
    }

    private static NotificationChannel buildChannel(Map<String, Object> input) {
        return NotificationChannel.newBuilder()
                .setChannel((String) input.getOrDefault("channel", "EMAIL"))
                .setSender((String) input.getOrDefault("sender", ""))
                .setOverridingBody((String) input.getOrDefault("overridingBody", null))
                .setBody((String) input.getOrDefault("body", null))
                .setProperties(castToCharSeqMap((Map<String, Object>) input.getOrDefault("properties", new HashMap<>())))
                .build();
    }

    private static Attachment buildAttachment(Map<String, Object> input) {
        return Attachment.newBuilder()
                .setUrl((String) input.get("url"))
                .setName((String) input.get("name"))
                .build();
    }

    private static Map<CharSequence, CharSequence> castToCharSeqMap(Map<String, Object> map) {
        Map<CharSequence, CharSequence> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return result;
    }
    private static List<CharSequence> castToCharSeqList(List<String> list) {
        return list.stream().map(s -> (CharSequence) s).collect(Collectors.toList());
    }

}
