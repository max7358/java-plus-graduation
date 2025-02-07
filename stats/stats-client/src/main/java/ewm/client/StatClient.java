package ewm.client;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import dto.HitDto;
import dto.StatDto;

import java.util.List;

@Service
public class StatClient {
    private final RestClient restClient;

    @Autowired
    StatClient(@Value("${stats.server.url}") String serverUrl) {
        restClient = RestClient.create(serverUrl);
    }

    public void hit(@Valid HitDto hitDto) {
        restClient.post().uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<StatDto> getStats(String start,
                                  String end,
                                  List<String> uris,
                                  Boolean unique) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .queryParam("uris", uris)
                            .queryParam("unique", unique)
                            .build())
                    .retrieve().body(new ParameterizedTypeReference<>() {
                    });
        } catch (ResourceAccessException ignored) {
        }
        return List.of();
    }
}