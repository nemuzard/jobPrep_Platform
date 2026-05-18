package com.jobprep.jobprep_platform.model.dto.notification;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationDTO {
    @NotEmpty(message = "content cannot be empty")
    @NotNull(message = "content cannot be empty")
    private String content;
}
