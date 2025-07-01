package com.hibi.server.domain.song.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SongCreateRequest(
        @NotNull String titleKor,
        @NotNull String titleEng,
        @NotNull String titleJp,
        @NotNull Long artistId,
        @NotNull LocalDate postedAt
) {
}
