package dat.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


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

    @ElementCollection
    private Set<String> language;

    private double budget;
    // TODO - fix remove og add metoder
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Director> directors = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Actor> actors = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Genre> genres = new HashSet<>();

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
