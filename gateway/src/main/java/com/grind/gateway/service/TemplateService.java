package com.grind.gateway.service;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.IdDTO;
import com.grind.gateway.dto.template.CreateTrackTemplateDTO;
import com.grind.gateway.dto.template.EditTrackTemplateDTO;
import com.grind.gateway.enums.TemplateMessageType;
import com.grind.gateway.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateService {
    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.template.request}")
    private String tempReqTopic;

    public Body<?> callGetTemplate(String trackTemplateId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackTemplateId),
                TemplateMessageType.GET_TEMPLATE.name(),
                tempReqTopic
        );
    }

    public Body<?> callGetPublicTemplates(){
        return kafkaProducer.requestReply(
                null,
                TemplateMessageType.GET_PUBLIC_TEMPLATES.name(),
                tempReqTopic
        );
    }

    public Body<?> callGetTemplatesOfAuthor(String authorId){
        return kafkaProducer.requestReply(
                IdDTO.of(authorId),
                TemplateMessageType.GET_TEMPLATES_OF_AUTHOR.name(),
                tempReqTopic
        );
    }

    public Body<?> callGetTemplatesOfCategory(String categoryId){
        return kafkaProducer.requestReply(
                IdDTO.of(categoryId),
                TemplateMessageType.GET_TEMPLATES_OF_CATEGORY.name(),
                tempReqTopic
        );
    }

    public Body<?> callArchiveTemplate(String trackTemplateId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackTemplateId),
                TemplateMessageType.ARCHIVE_TEMPLATE.name(),
                tempReqTopic
        );
    }

    public Body<?> callPublishTemplate(String trackTemplateId){
        return kafkaProducer.requestReply(
                IdDTO.of(trackTemplateId),
                TemplateMessageType.PUBLISH_TEMPLATE.name(),
                tempReqTopic
        );
    }

    public Body<?> callCreateTemplate(CreateTrackTemplateDTO payload){
        return kafkaProducer.requestReply(
                payload,
                TemplateMessageType.CREATE_TEMPLATE.name(),
                tempReqTopic
        );
    }

    public Body<?> callEditTemplate(EditTrackTemplateDTO payload){
        return kafkaProducer.requestReply(
                payload,
                TemplateMessageType.EDIT_TEMPLATE.name(),
                tempReqTopic
        );
    }
}
