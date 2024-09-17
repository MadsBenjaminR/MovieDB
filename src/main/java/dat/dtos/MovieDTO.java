package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")  // Add this to match the Movie entity
    private String description;

    @JsonProperty("vote_average")
    private double rating;

    @JsonProperty("original_language")
    private String language;
    @JsonProperty("budget")
    private double budget;

    @JsonProperty("directors")
    private Set<DirectorDTO> directors = new HashSet<>();

    @JsonProperty("actors")
    private Set<ActorDTO> actors = new HashSet<>();

    @JsonProperty("genres")
    private Set<GenreDTO> genres = new HashSet<>();

    // Constructor to convert from Movie entity to MovieDTO
    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.rating = movie.getRating();
        this.language = movie.getLanguage().toString();
        this.budget = movie.getBudget();

    }
}
