package com.hibi.server.domain.song.repository;

import com.hibi.server.domain.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    // 아티스트별 노래 리스트 조회 (필요하면)
    List<Song> findByArtistId(Long artistId);

    Optional<Song> findByPostedAt(LocalDate postedAt);
}
