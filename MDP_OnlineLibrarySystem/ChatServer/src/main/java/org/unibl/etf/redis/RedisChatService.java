package org.unibl.etf.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class RedisChatService {
    private RedisConnection redisConnection;

    public RedisChatService() {
        this.redisConnection = new RedisConnection();
    }

    public void saveChatMessage(String user1, String user2, String dataMessage){
        // THe key is chat:user1:user2
        String chatKey = "chat:" + user1 + ":" + user2;
        long timestamp = System.currentTimeMillis();
        Map<String, Object> messageData = new HashMap<>();      // {message=user1: msgOfChat, timestamp=1.723806921534E12} or {message=user2: msgOfChat, timestamp=1.723806921534E12}
        messageData.put("message", dataMessage);
        messageData.put("timestamp", timestamp);

        Gson gson = new Gson();
        String messageJson = gson.toJson(messageData);
        redisConnection.getJedis().rpush(chatKey, messageJson);
    }

    public List<Map<String, Object>> getChatHistory(String user1, String user2){
        String chatKey12 = "chat:" + user1 + ":" + user2;
        String chatKey21 = "chat:" + user2 + ":" + user1;
        Gson gson = new Gson();

        List<String> messagesJson1 = redisConnection.getJedis().lrange(chatKey12, 0, -1);
        List<String> messagesJson2 = redisConnection.getJedis().lrange(chatKey21, 0, -1);
        List<Map<String, Object>> messages = new ArrayList<>();

        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        for (String messageJson : messagesJson1) {
            Map<String, Object> messageData = gson.fromJson(messageJson, type);     // {message=user1: msgOfChat, timestamp=1.723806921534E12, sender=user1} or {message=user2: msgOfChat, timestamp=1.723806921534E12, sender=user2}
            messageData.put("sender", user1);
            messages.add(messageData);
        }
        for (String messageJson : messagesJson2) {
            Map<String, Object> messageData = gson.fromJson(messageJson, type);
            messageData.put("sender", user2);
            messages.add(messageData);
        }

        messages.sort((m1, m2) -> {
            long t1 = ((Number) m1.get("timestamp")).longValue();
            long t2 = ((Number) m2.get("timestamp")).longValue();
            return Long.compare(t1, t2);
        });

        return messages;
    }
}
