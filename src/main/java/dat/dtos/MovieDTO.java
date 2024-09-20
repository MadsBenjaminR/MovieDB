package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;

import java.util.HashSet;
import java.util.List;
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

    @JsonProperty("overview")// Add this to match the Movie entity
    @Column(name = "description")
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
    @JsonProperty("genre_ids")
    private List<Integer> genre_ids; // This will store genre IDs from the API response
    private Set<GenreDTO> genres;


    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.rating = movie.getRating();
        this.language = movie.getLanguage();
        this.budget = movie.getBudget();

    }

}
