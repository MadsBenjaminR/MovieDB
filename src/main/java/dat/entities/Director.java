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
    @Column(name = "id", nullable = false)
    private Long id;

    private String firstName;
    private String lastName;
    private String fullName;
    private String job;

    @ManyToMany(mappedBy = "directors")
    private Set<Movie> movies = new HashSet<>();



    public Director(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        fullName = firstName + " "+ lastName;


    }
}
