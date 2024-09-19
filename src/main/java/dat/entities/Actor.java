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


    private String fullName;


    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies = new HashSet<>();


    public Actor(String fullName, Movie movie) {
        this.fullName = fullName;
        movies.add(movie);
    }
}



