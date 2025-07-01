package com.hibi.server.domain.song.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SongUpdateRequest(
        @NotNull String titleKor,
        @NotNull String titleEng,
        @NotNull String titleJp,
        @NotNull LocalDate postedAt
) {
}
