package dat.entities;


import dat.dtos.DirectorDTO;
import dat.dtos.MovieDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**@author laith kaseb**/
@Getter
@ToString
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "movie")

public class Movie {
    @Id
    private Long id;

    @Column(length = 1000)
    private String title;

    @Column(length = 1000)
    private String description;

    private double rating;
    private String language;
    private double budget;

    @ManyToMany
    @JoinTable(
            name = "movie_director",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private Set<Director> directors = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    public Movie(MovieDTO movieDTO) {
        this.id = movieDTO.getId();
        this.title = movieDTO.getTitle();
        this.description = movieDTO.getDescription();
        this.rating = movieDTO.getRating();
        this.language = movieDTO.getLanguage();
        this.budget = movieDTO.getBudget();

    }
}
