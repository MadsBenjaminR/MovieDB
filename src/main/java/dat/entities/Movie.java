package dat.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

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
    private Language language;
    private Director director;
    private int rating;
    private int budget;
    private String discription;
    private int runtime;

}
