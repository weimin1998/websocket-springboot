package com.example.websocket.utils;

import com.example.websocket.pojo.ResultMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//封装发送的消息内容
public class MessageUtils {
    public static String getMessage(boolean isSystemMessage, String fromName, Object message) {
        try {
            ResultMessage resultMessage = new ResultMessage();
            resultMessage.setSystem(isSystemMessage);
            resultMessage.setMessage(message);
            if (fromName != null) {
                resultMessage.setFromName(fromName);
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(resultMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
