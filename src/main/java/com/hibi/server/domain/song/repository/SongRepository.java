package com.hibi.server.domain.song.repository;

import com.hibi.server.domain.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByArtistId(Long artistId);

    Optional<Song> findByPostedAt(LocalDate postedAt);

    @Query("SELECT s FROM Song s WHERE YEAR(s.postedAt) = :year AND MONTH(s.postedAt) = :month")
    List<Song> findByPostedAtYearAndMonth(@Param("year") int year, @Param("month") int month);
}
