package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Purpose:
 *
 * @author: Jeppe Koch
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieApiResponseDTO {
    @JsonProperty("results")
    private List<MovieDTO> movie_results;

}

