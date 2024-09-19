package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Actor {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String firstName;
    private String lastName;
    private String fullName;


    @ManyToMany
    @JoinTable(
    name = "movie_actor", // The name of the join table
    joinColumns = @JoinColumn(name = "movie_id"), // Foreign key for Movie
    inverseJoinColumns = @JoinColumn(name = "actor_id") // Foreign key for Actor
    )
    private Set<Movie> movie = new HashSet<>();


    public Actor(String firstName, String lastName, Set<Movie> movie) {
        this.firstName = firstName;
        this.lastName = lastName;

        fullName = firstName + " "+ lastName;
        movies.add(movie);
    }
}



