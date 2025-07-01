package com.hibi.server.domain.song.entity;

import com.hibi.server.domain.artist.entity.Artist;
import com.hibi.server.domain.song.dto.request.SongCreateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "songs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title_kor", nullable = false)
    private String titleKor;

    @Column(name = "title_eng", nullable = false)
    private String titleEng;

    @Column(name = "title_jp", nullable = false)
    private String titleJp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(name = "posted_at", nullable = false)
    private LocalDate postedAt;

    public static Song of(SongCreateRequest request, Artist artist) {
        return Song.builder()
                .titleKor(request.titleKor())
                .titleEng(request.titleEng())
                .titleJp(request.titleJp())
                .postedAt(request.postedAt())
                .artist(artist)
                .build();
    }

    public void updateSong(String titleKor, String titleEng, String titleJp, LocalDate postedAt) {
        this.titleKor = titleKor;
        this.titleEng = titleEng;
        this.titleJp = titleJp;
        this.postedAt = postedAt;
    }
}
