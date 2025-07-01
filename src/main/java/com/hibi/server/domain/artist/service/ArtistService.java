package com.hibi.server.domain.artist.service;


import com.hibi.server.domain.artist.dto.request.ArtistCreateRequest;
import com.hibi.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.hibi.server.domain.artist.dto.response.ArtistResponse;
import com.hibi.server.domain.artist.entity.Artist;
import com.hibi.server.domain.artist.repository.ArtistRepository;
import com.hibi.server.global.exception.CustomException;
import com.hibi.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Transactional
    public void create(ArtistCreateRequest request) {
        Artist artist = Artist.builder()
                .nameKor(request.nameKor())
                .nameEng(request.nameEng())
                .nameJp(request.nameJp())
                .profileUrl("default.png")
                .build();
        artistRepository.save(artist);
    }

    public Artist getById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Transactional
    public ArtistResponse update(Long id, ArtistUpdateRequest request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        artist.update(request);
        return ArtistResponse.from(artist);
    }

    @Transactional
    public void delete(Long id) {
        artistRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ArtistResponse> getAll() {
        return artistRepository.findAll().stream()
                .map(ArtistResponse::from)
                .toList();
    }

}
