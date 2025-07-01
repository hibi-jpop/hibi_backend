package com.hibi.server.domain.artist.entity;

import com.hibi.server.domain.artist.dto.request.ArtistUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "artists")
@Builder
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_kor")
    private String nameKor;

    @Column(name = "name_eng")
    private String nameEng;

    @Column(name = "name_jp")
    private String nameJp;

    @Column(name = "profile_url")
    private String profileUrl;

    public void update(ArtistUpdateRequest request) {
        this.nameKor = request.nameKor();
        this.nameEng = request.nameEng();
        this.nameJp = request.nameJp();
        this.profileUrl = request.profileUrl();
    }
}
