package com.grind.template.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.template.dto.entity.TrackTemplateFullDTO;
import com.grind.template.dto.request.CreateTrackTemplateDTO;
import com.grind.template.dto.request.EditTrackTemplateDTO;
import com.grind.template.dto.wrap.Reply;
import com.grind.template.entity.TrackTemplate;
import com.grind.template.enums.TemplateMessageType;
import com.grind.template.service.application.TrackTemplateService;
import com.grind.template.util.ActionReplyExecutor;
import com.grind.template.util.IdParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TemplateReplyHandler {
    private final TrackTemplateService trackTemplateService;
    private final ObjectMapper objectMapper;
    private final ActionReplyExecutor exec;

    public Reply<?> routeReply(TemplateMessageType messageType, String payload) {
        return switch (messageType) {
            case GET_TEMPLATE -> handleGetTemplate(payload);
            case GET_PUBLIC_TEMPLATES -> handleGetPublicTemplates();
            case GET_TEMPLATES_OF_AUTHOR -> handleGetTemplatesOfAuthor(payload);
            case GET_TEMPLATES_OF_CATEGORY -> handleGetTemplatesOfCategory(payload);
            case ARCHIVE_TEMPLATE -> handleArchiveTemplate(payload);
            case PUBLISH_TEMPLATE -> handlePublishTemplate(payload);
            case CREATE_TEMPLATE -> handleCreateTemplate(payload);
            case EDIT_TEMPLATE -> handleEditTemplate(payload);
            default ->
                    throw new UnsupportedOperationException("Message type is not related to templates");
        };
    }

    private Reply<TrackTemplateFullDTO> handleGetTemplate(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        TemplateMessageType.TEMPLATE,
                        trackTemplateService.getById(IdParser.run(payload))
                                .mapFullDTO()
                )
        );
    }


    private Reply<Collection<TrackTemplateFullDTO>> handleGetPublicTemplates() {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        TemplateMessageType.PUBLIC_TEMPLATES,
                        trackTemplateService.getPublished()
                                .stream().map(TrackTemplate::mapFullDTO)
                                .collect(Collectors.toSet())
                )
        );
    }

    private Reply<Collection<TrackTemplateFullDTO>> handleGetTemplatesOfAuthor(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        TemplateMessageType.TEMPLATES_OF_AUTHOR,
                        trackTemplateService.getByAuthorId(IdParser.run(payload))
                                .stream().map(TrackTemplate::mapFullDTO)
                                .collect(Collectors.toSet())
                )
        );
    }

    private Reply<Collection<TrackTemplateFullDTO>> handleGetTemplatesOfCategory(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        TemplateMessageType.TEMPLATES_OF_CATEGORY,
                        trackTemplateService.getByCategoryId(IdParser.run(payload))
                                .stream().map(TrackTemplate::mapFullDTO)
                                .collect(Collectors.toSet())
                )
        );
    }

    private Reply<TrackTemplateFullDTO> handleCreateTemplate(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        TemplateMessageType.TEMPLATE_CREATED,
                        trackTemplateService.create(
                                        objectMapper.readValue(
                                                payload,
                                                CreateTrackTemplateDTO.class
                                        )
                                )
                                .mapFullDTO()
                )
        );
    }

    private Reply<TrackTemplateFullDTO> handleEditTemplate(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        TemplateMessageType.TEMPLATE_EDITED,
                        trackTemplateService.edit(
                                        objectMapper.readValue(
                                                payload,
                                                EditTrackTemplateDTO.class
                                        )
                                )
                                .mapFullDTO()
                )
        );
    }

    private Reply<TrackTemplateFullDTO> handleArchiveTemplate(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        TemplateMessageType.TEMPLATE_ARCHIVED,
                        trackTemplateService.archive(IdParser.run(payload))
                                .mapFullDTO()
                ));
    }

    private Reply<TrackTemplateFullDTO> handlePublishTemplate(String payload) {
        return exec.withErrorMapping(() ->
                Reply.ok(
                        TemplateMessageType.TEMPLATE_PUBLISHED,
                        trackTemplateService.publish(IdParser.run(payload))
                                .mapFullDTO()
                )
        );
    }
}
