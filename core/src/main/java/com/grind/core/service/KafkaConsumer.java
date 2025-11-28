package com.grind.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.CoreMessageDTO;
import com.grind.core.dto.CoreMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaProducer kafkaProducer;

    @KafkaListener(id = "core-server", topics = "core.request")
    public void listen(
            @Payload String payload,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("X-Trace-Id") String traceId
    ) {
        CoreMessageDTO msg;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            msg = objectMapper.readValue(payload, CoreMessageDTO.class);
        } catch (JsonParseException e){
            msg = new CoreMessageDTO(CoreMessageType.UNDEFINED, payload);
            System.out.println("could not parse. processing as string");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String response;
        switch (msg.type()){
            case CREATE_TRACK -> {
                System.out.println("mocking create track action");
                response = "create track response. corrId:"+correlationId;
            }
            case CREATE_TASK -> {
                System.out.println("mocking create task action");
                response = "create task response. corrId:"+correlationId;
            }
            case DELETE_TRACK -> {
                System.out.println("mocking delete track action");
                response = "delete track response. corrId:"+correlationId;
            }
            case DELETE_TASK -> {
                System.out.println("mocking delete task action");
                response = "delete task response. corrId:"+correlationId;
            }
            case UNDEFINED -> {
                System.out.println("undefined message type");
                response = "payload: "+msg.payload()+". corrId: "+correlationId;
            }
            default -> {
                System.out.println("unknown type");
                response = "unknown type in core.request. corrId:"+correlationId;
            }
        }
        kafkaProducer.reply(response, correlationId, traceId);
        kafkaProducer.publish(response, traceId);
    }
}
