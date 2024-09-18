package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Set;

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
    private Set<MovieDTO> movieResults;

    @JsonProperty("cast")
    private Set<ActorDTO> listOfActorsDTO;
    @JsonProperty("crew")
    private Set<DirectorDTO> listOfDirectorsDTO;


}

