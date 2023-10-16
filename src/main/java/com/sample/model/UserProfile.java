package com.sample.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record UserProfile(@Schema(accessMode = Schema.AccessMode.WRITE_ONLY)
                             String userHandle,
                          @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                             String firstName,
                          @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                             String lastName,

                          @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                             LocalDate dob) {
}
