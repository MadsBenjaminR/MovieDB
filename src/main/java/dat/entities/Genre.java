package dat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "movie_genre", // The name of the join table
            joinColumns = @JoinColumn(name = "movie_id"), // Foreign key for Movie
            inverseJoinColumns = @JoinColumn(name = "genre_id") // Foreign key for Actor
    )
    private Set<Movie> movie = new HashSet<>();



}
