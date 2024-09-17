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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private double rating;

    private double runtime;

    private String language;

    private double budget;
    // TODO - fix remove og add metoder
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Director> directors = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Actor> actors = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Genre> genres = new HashSet<>();

    public Movie(MovieDTO movieDTO) {
        this.id = movieDTO.getId();
        this.title = movieDTO.getTitle();
        this.description = movieDTO.getDescription();
        this.rating = movieDTO.getRating();
        this.language = movieDTO.getLanguage();
        this.budget = movieDTO.getBudget();

    }


    public void addActor(Actor actor) {
       actor = new Actor(actor.getFirstName(), actor.getLastName(), actor.getMovie());
        if (actor != null) {
            this.actors.add(actor);
        }
    }

    public void addDirector(Director director) {
        director = new Director(director.getFirstName(), director.getLastName(), director.getMovie());
        if (director != null) {
            this.directors.add(director);
        }
    }

    public void removeActor(Actor actor) {
        if (actor == null) {
            this.actors.remove(actor);
        }
    }

    public void removeDirector(Director director) {
        if (director != null) {
            this.directors.remove(director);
        }
    }








}
