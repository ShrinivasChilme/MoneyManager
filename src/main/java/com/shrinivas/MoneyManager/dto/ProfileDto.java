package com.shrinivas.MoneyManager.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDto {
    private Long id;
    private String fullName;

    private String email;
    private String password;
    private String profileImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
