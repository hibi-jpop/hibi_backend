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

    @Query("SELECT p.song FROM Post p WHERE p.postedAt = :postedAt")
    Optional<Song> findByPostedAt(@Param("postedAt") LocalDate postedAt);

    @Query("SELECT p.song FROM Post p WHERE YEAR(p.postedAt) = :year AND MONTH(p.postedAt) = :month")
    List<Song> findByPostedAtYearAndMonth(@Param("year") int year, @Param("month") int month);
}
