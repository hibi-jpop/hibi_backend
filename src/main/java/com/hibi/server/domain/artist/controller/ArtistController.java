package com.hibi.server.domain.artist.controller;

import com.hibi.server.domain.artist.dto.request.ArtistCreateRequest;
import com.hibi.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.hibi.server.domain.artist.dto.response.ArtistResponse;
import com.hibi.server.domain.artist.entity.Artist;
import com.hibi.server.domain.artist.service.ArtistService;
import com.hibi.server.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @Operation(summary = "아티스트 생성", description = "새로운 아티스트를 생성합니다.")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> create(@RequestBody ArtistCreateRequest request) {
        artistService.create(request);
        return ResponseEntity.ok(SuccessResponse.success("아티스트가 생성되었습니다."));
    }

    @Operation(summary = "아티스트 단건 조회", description = "ID를 통해 아티스트를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ArtistResponse>> getById(@PathVariable Long id) {
        Artist artist = artistService.getById(id);
        ArtistResponse response = ArtistResponse.from(artist);
        return ResponseEntity.ok(SuccessResponse.success("아티스트 조회 성공", response));
    }

    @Operation(summary = "아티스트 수정(관리자)", description = "ID로 기존 아티스트 정보를 수정합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<ArtistResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid ArtistUpdateRequest request
    ) {
        ArtistResponse updatedArtist = artistService.update(id, request);
        return ResponseEntity.ok(SuccessResponse.success("아티스트가 수정되었습니다.", updatedArtist));
    }

    @Operation(summary = "아티스트 삭제", description = "ID를 통해 아티스트를 삭제합니다.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<?>> delete(@PathVariable Long id) {
        artistService.delete(id);
        return ResponseEntity.ok(SuccessResponse.success("아티스트가 삭제되었습니다."));
    }

    @Operation(summary = "아티스트 전체 조회", description = "등록된 모든 아티스트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<SuccessResponse<List<ArtistResponse>>> getAll() {
        List<ArtistResponse> artists = artistService.getAll();
        return ResponseEntity.ok(SuccessResponse.success("아티스트 목록 조회 성공", artists));
    }
}
