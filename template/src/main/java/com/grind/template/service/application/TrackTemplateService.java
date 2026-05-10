package com.grind.template.service.application;

import com.grind.template.dto.request.CreateTrackTemplateDTO;
import com.grind.template.dto.request.EditTrackTemplateDTO;
import com.grind.template.entity.TaskTemplate;
import com.grind.template.entity.TrackTemplate;
import com.grind.template.enums.TrackTemplateStatus;
import com.grind.template.enums.TrackVisibility;
import com.grind.template.repository.TrackTemplateRepository;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Validated
@RequiredArgsConstructor
public class TrackTemplateService {
    private final TrackTemplateRepository repository;
    private final TagService tagService;
    private final TrackCategoryService categoryService;
    private final TaskTemplateService taskTemplateService;

    // TODO Future lifecycle methods:
    // - publish(String id): validate template completeness, set PUBLISHED, set publishedAt.
    // - archive(String id): prevent further editing/usage or apply explicit archived rules.
    // - unpublish(String id) / moveToDraft(String id): define allowed PUBLISHED -> DRAFT transition.
    // - restore(String id): define ARCHIVED -> DRAFT/PUBLISHED transition rules.
    // - deleteDraft(String id): allow deletion only for templates that were never published.

    @Transactional
    public TrackTemplate publish(String id) {
        TrackTemplate template = getById(id);

        Set<String> errors = validateTrackTemplate(template);

        if (!errors.isEmpty()) {
            String msg =
                    String.format("Track with id '%s' cannot be published, because it is invalid:\n", id) +
                            String.join("\n", errors);
            throw new IllegalStateException(msg);
        }

        for (TaskTemplate task : template.getTaskTemplates()) {
            taskTemplateService.validateTask(task);
        }

        template.setStatus(TrackTemplateStatus.PUBLISHED);
        if (template.getPublishedAt() == null) {
            template.setPublishedAt(LocalDateTime.now());
        }
        return repository.save(template);
    }

    @Nonnull
    private static Set<String> validateTrackTemplate(TrackTemplate template) {
        Set<String> errors = new HashSet<>();

        if (template.getTitle() == null || template.getTitle().isBlank()) {
            errors.add("Track template has to have title");
        }

        if (template.getDurationDays() == null || template.getDurationDays() < 1) {
            errors.add("Track duration is not present or negative");
        }

        if (template.getStatus().equals(TrackTemplateStatus.ARCHIVED)) {
            errors.add("Track cannot be published, because it is archived");
        }

        if (template.getSprintLength() == null || template.getSprintLength() < 1) {
            errors.add("Sprint length is not present or negative");
        }

        if (template.getTaskTemplates().isEmpty()) {
            errors.add("Task set is empty. Please fill track with tasks");
        }
        return errors;
    }

    // TODO Future validation before publish:
    // - validateBeforePublish(String id): required fields, category, tags, task presence.
    // - Ensure durationDays, sprintLength, estimatedTimePerDayMinutes are coherent.
    // - Ensure every task plannedDayOffset fits inside track duration.
    // - Ensure required task fields are present and all requested tags/category exist.
    //
    // TODO Future query methods:
    // - getById(String id) with explicit not-found behavior instead of getReferenceById.
    // - getPublished(), getByAuthor(String authorId), getByCategory(String slug), getByTag(String slug).
    // - search(criteria, pageable): query text, status, visibility, category, tags, difficulty, skillType.
    // - getVisibleForUser(String userId): apply PRIVATE/UNLISTED/PUBLIC visibility rules.

    public TrackTemplate getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No track template found with id: " + id));
    }

    public List<TrackTemplate> getPublished() {
        return repository.findByVisibilityAndStatus(TrackVisibility.PUBLIC, TrackTemplateStatus.PUBLISHED);
    }

    public List<TrackTemplate> getByAuthorId(String authorId) {
        return repository.findByAuthorId(authorId);
    }

    public List<TrackTemplate> getByCategoryId(String categoryId) {
        return repository.findByCategoryId(categoryId);
    }

    // TODO Future edit/versioning rules:
    // - Restrict editing PUBLISHED/ARCHIVED templates or create a new revision instead.
    // - duplicateTemplate(String id, String authorId), forkTemplate(String id, String authorId).
    // - createRevision(String id), getLatestRevision(String templateId).

    @Transactional
    public TrackTemplate archive(String id) {
        TrackTemplate template = getById(id);
        template.setStatus(TrackTemplateStatus.ARCHIVED);
        return repository.save(template);
    }

    public List<TrackTemplate> getAll() {
        return repository.findAll();
    }

    public TrackTemplate create(CreateTrackTemplateDTO dto) {
        TrackTemplate template = new TrackTemplate();
        template.setId(UUID.randomUUID().toString());
        template.setStatus(TrackTemplateStatus.DRAFT);
        template.setAuthorId(dto.authorId()); // TODO replace with value from Kafka headers
        template.setVisibility(dto.visibility());
        template.setTitle(dto.title());
        template.setDescription(dto.description());
        template.setExpectedResult(dto.expectedResult());
        template.setDurationDays(dto.durationDays());
        template.setSprintLength(dto.sprintLength());
        template.setEstimatedTimePerDayMinutes(dto.estimatedTimePerDayMinutes());
        template.setDifficulty(dto.difficulty());
        template.setSkillType(dto.skillType());

        template.setTags(tagService.getBySlug(dto.tags()));
        template.setCategory(categoryService.getBySlug(dto.category()));

        return repository.save(template);
    }

    public TrackTemplate edit(@Valid EditTrackTemplateDTO dto) {
        TrackTemplate template = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("No template with such id: " + dto.id()));
        if (dto.visibility() != null) {
            template.setVisibility(dto.visibility());
        }
        if (dto.title() != null) {
            template.setTitle(dto.title());
        }
        if (dto.description() != null) {
            template.setDescription(dto.description());
        }
        if (dto.expectedResult() != null) {
            template.setExpectedResult(dto.expectedResult());
        }
        if (dto.durationDays() != null) {
            template.setDurationDays(dto.durationDays());
        }
        if (dto.sprintLength() != null) {
            template.setSprintLength(dto.sprintLength());
        }
        if (dto.estimatedTimePerDayMinutes() != null) {
            template.setEstimatedTimePerDayMinutes(dto.estimatedTimePerDayMinutes());
        }
        if (dto.difficulty() != null) {
            template.setDifficulty(dto.difficulty());
        }
        if (dto.skillType() != null) {
            template.setSkillType(dto.skillType());
        }
        if (dto.tags() != null) {
            template.setTags(tagService.getBySlug(dto.tags()));
        }
        if (dto.category() != null) {
            template.setCategory(categoryService.getBySlug(dto.category()));
        }

        return repository.save(template);
    }
}
