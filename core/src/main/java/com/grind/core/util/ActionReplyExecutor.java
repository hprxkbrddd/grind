package com.grind.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grind.core.dto.wrap.Reply;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingSupplier;

@Component
public class ActionReplyExecutor {
    public <T> Reply<T> withErrorMapping(ThrowingSupplier<Reply<T>> action) {
        try {
            return action.getWithException();
        } catch (EntityNotFoundException ex) {
            return Reply.error(ex, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException | JsonProcessingException | ConstraintViolationException ex) {
            return Reply.error(ex, HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException ex) {
            return Reply.error(ex, HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            return Reply.error(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
