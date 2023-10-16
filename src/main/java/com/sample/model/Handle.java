package com.sample.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record Handle(String userHandle, String type,
                     @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                     LocalDateTime created_at) {
}
