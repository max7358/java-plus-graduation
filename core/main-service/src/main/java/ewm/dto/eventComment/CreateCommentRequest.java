package ewm.dto.eventComment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequest {
    @NotBlank
    @Size(min = 1, max = 7000)
    private String text;
    private Long eventId;
}
