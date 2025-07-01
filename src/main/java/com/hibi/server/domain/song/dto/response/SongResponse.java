package com.hibi.server.domain.song.dto.response;

import com.hibi.server.domain.song.entity.Song;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SongResponse(
        Long id,
        String titleKor,
        String titleEng,
        String titleJp,
        Long artistId,
        LocalDate postedAt
) {
    public static SongResponse from(Song song) {
        return new SongResponse(
                song.getId(),
                song.getTitleKor(),
                song.getTitleEng(),
                song.getTitleJp(),
                song.getArtist().getId(),
                song.getPostedAt()
        );
    }
}
