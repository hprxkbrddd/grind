package com.grind.statistics.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grind.statistics.dto.wrap.Reply;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingSupplier;

@Component
public class ActionReplyExecutor {
    public <T> Reply<T> withErrorMapping(ThrowingSupplier<Reply<T>> action) {
        try {
            return action.getWithException();
        } catch (IllegalArgumentException | JsonProcessingException ex) {
            return Reply.error(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return Reply.error(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}