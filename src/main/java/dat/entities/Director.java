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
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String firstName;
    private String lastName;
    private String fullName;

    @ManyToMany
    @JoinTable(
            name = "movie_director", // The name of the join table
            joinColumns = @JoinColumn(name = "movie_id"), // Foreign key for Movie
            inverseJoinColumns = @JoinColumn(name = "director_id") // Foreign key for Actor
    )
    private Set<Movie> movie = new HashSet<>();



    public Director(String firstName, String lastName, Set<Movie> movie) {
        this.firstName = firstName;
        this.lastName = lastName;
        fullName = firstName + " "+ lastName;
        this.movie = movie;

    }

}
