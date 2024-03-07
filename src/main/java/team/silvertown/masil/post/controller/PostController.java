package team.silvertown.masil.post.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.post.dto.request.CreateRequest;
import team.silvertown.masil.post.dto.response.CreateResponse;
import team.silvertown.masil.post.dto.response.PostDetailResponse;
import team.silvertown.masil.post.service.PostService;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/api/v1/posts")
    public ResponseEntity<CreateResponse> create(
        @AuthenticationPrincipal
        Long userId,
        @RequestBody
        CreateRequest request
    ) {
        CreateResponse response = postService.create(userId, request);
        URI uri = URI.create("/api/v1/posts/" + response.id());

        return ResponseEntity.created(uri)
            .body(response);
    }

    @GetMapping("/api/v1/posts/{id}")
    public ResponseEntity<PostDetailResponse> getById(
        @PathVariable
        Long id
    ) {
        PostDetailResponse response = postService.getById(id);

        return ResponseEntity.ok(response);
    }

}
