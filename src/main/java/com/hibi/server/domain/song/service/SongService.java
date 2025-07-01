package com.hibi.server.domain.song.service;

import com.hibi.server.domain.artist.entity.Artist;
import com.hibi.server.domain.artist.repository.ArtistRepository;
import com.hibi.server.domain.song.dto.request.SongCreateRequest;
import com.hibi.server.domain.song.dto.request.SongUpdateRequest;
import com.hibi.server.domain.song.dto.response.SongResponse;
import com.hibi.server.domain.song.entity.Song;
import com.hibi.server.domain.song.repository.SongRepository;
import com.hibi.server.global.exception.CustomException;
import com.hibi.server.global.exception.ErrorCode;
import com.hibi.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    @Transactional
    public SuccessResponse<?> create(SongCreateRequest request) {
        Artist artist = artistRepository.findById(request.artistId())
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Song song = Song.of(request, artist);
        songRepository.save(song);
        return SuccessResponse.success("노래가 성공적으로 생성되었습니다.");
    }

    public SuccessResponse<SongResponse> getById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        return SuccessResponse.success("노래 조회 성공", SongResponse.from(song));
    }

    public SuccessResponse<SongResponse> getByDate(LocalDate date) {
        Song song = songRepository.findByPostedAt(date)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
        return SuccessResponse.success("노래 조회 성공", SongResponse.from(song));
    }

    @Transactional
    public SuccessResponse<SongResponse> update(Long id, SongUpdateRequest request) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        song.updateSong(
                request.titleKor(),
                request.titleEng(),
                request.titleJp(),
                request.postedAt()
        );

        return SuccessResponse.success("노래가 성공적으로 수정되었습니다.", SongResponse.from(song));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SuccessResponse<?> delete(Long id) {
        if (!songRepository.existsById(id)) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        songRepository.deleteById(id);
        return SuccessResponse.success("노래가 성공적으로 삭제되었습니다.");
    }

    public SuccessResponse<List<SongResponse>> getAll() {
        List<SongResponse> songs = songRepository.findAll().stream()
                .map(SongResponse::from)
                .toList();
        return SuccessResponse.success("노래 전체 조회 성공", songs);
    }
}
