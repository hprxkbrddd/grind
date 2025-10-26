package com.grind.security.entity;

import com.grind.security.dto.UserEntityDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "user_entity")
public class UserEntity {
    @Id
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;

    public UserEntityDTO toDTO() {
        return new UserEntityDTO(
                id,
                username
        );
    }
}
