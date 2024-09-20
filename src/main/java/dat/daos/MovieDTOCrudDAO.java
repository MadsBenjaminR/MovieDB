package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.MovieDTO;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import java.util.List;

/**
 * @author laith kaseb
 **/


public class MovieDTOCrudDAO {
    EntityManagerFactory emf= HibernateConfig.getEntityManagerFactory("tmdb");

    public void delete(MovieDTO movieDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Movie movieEntity = em.find(Movie.class, movieDTO.getId());

            if (movieEntity != null) {

                em.remove(movieEntity);
            } else {
                throw new IllegalArgumentException("Movie with ID " + movieDTO.getId() + " not found.");
            }

            em.getTransaction().commit();
        }
    }

   public MovieDTO create(MovieDTO movieDTO) {
    EntityManager em = emf.createEntityManager();
    try {
        em.getTransaction().begin();

        // Create a new Movie entity from the DTO data
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getDescription());
        movie.setRating(movieDTO.getRating());
        movie.setLanguage(movieDTO.getLanguage());
        movie.setBudget(movieDTO.getBudget());

        // Persist the Movie entity into the database
        em.persist(movie);
        em.getTransaction().commit();

        // Set the generated ID back in the DTO
        movieDTO.setId(movie.getId());
        return new MovieDTO(movie);
    } catch (Exception e) {
        em.getTransaction().rollback(); // Rollback the transaction if there's an error
        throw new RuntimeException("Error creating movie", e); // Optionally rethrow the exception
    } finally {
        em.close();
    }
    }



    public MovieDTO findById(Long movieId) {
        EntityManager em = emf.createEntityManager();
        try {
            Movie movie = em.find(Movie.class, movieId);
            if (movie != null) {
                return new MovieDTO(movie);  // Convert entity to DTO
            } else {
                throw new IllegalArgumentException("Movie with ID " + movieId + " not found.");
            }
        } finally {
            em.close();
        }
    }

    public List<MovieDTO> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT new dat.dtos.MovieDTO(m) FROM movie m", MovieDTO.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(Movie movie) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE movie m set m.budget=:budget where m.id=:id");
            query.setParameter("budget", movie.getBudget());
            query.setParameter("id", movie.getId());
            query.executeUpdate();
            em.getTransaction().commit();

        }
    }
}
