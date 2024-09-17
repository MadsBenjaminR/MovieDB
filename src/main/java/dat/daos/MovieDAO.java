package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.ActorDTO;
import dat.dtos.DirectorDTO;
import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Purpose:
 *
 * @author: Jeppe Koch
 */
public class MovieDAO {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("tmdb");



    public MovieDTO create(MovieDTO movieDTO) {
        Movie movie = new Movie(movieDTO);
        try (EntityManager em = emf.createEntityManager()) {
       em.getTransaction().begin();
            Set<Director> directors = new HashSet<>();
            for (DirectorDTO directorDTO : movieDTO.getDirectors()) {
                Director director = em.find(Director.class, directorDTO.getId());
                if (director != null) {
                    directors.add(director);
                }else {
                    em.persist(movie);
                }
            }
            movie.setDirectors(directors);

            // Handle Actors (Set<Actor>)
            Set<Actor> actors = new HashSet<>();
            for (ActorDTO actorDTO : movieDTO.getActors()) {
                Actor actor = em.find(Actor.class, actorDTO.getId());
                if (actor != null) {
                    actors.add(actor);
                }else {
                    em.persist(actor);
                }
            }
            movie.setActors(actors);

            // Handle Genres (Set<Genre>)
            Set<Genre> genres = new HashSet<>();
            for (GenreDTO genreDTO : movieDTO.getGenres()) {
                Genre genre = em.find(Genre.class, genreDTO.getId());
                if (genre != null) {
                    genres.add(genre);
                } else {
                    em.persist(genre);
                }
            }
            movie.setGenres(genres);

            em.persist(movie);
            em.getTransaction().commit();
    }
        return new MovieDTO(movie);
    }

    // TODO: ret til når dto er lavet
    /*
    @Override
    public MovieDTO update(MovieDTO movieDTO) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // Find existing movie by ID
            Movie movie = em.find(Movie.class, movieDTO.getId());
            if (movie == null) {
                throw new IllegalArgumentException("Movie with ID " + movieDTO.getId() + " not found.");
            }

            // Update the movie fields with new DTO values
            movie.setTitle(movieDTO.getTitle()); // Assuming there's a title field, adjust as per your entity

            // Update Director
            Director director = em.find(Director.class, movieDTO.getDirectors().getId());
            if (director != null) {
                movie.setDirectors(director);
            } else {
                throw new IllegalArgumentException("Director with ID " + movieDTO.getDirectors().getId() + " not found.");
            }

            // Update Actor
            Actor actor = em.find(Actor.class, movieDTO.getActors().getId());
            if (actor != null) {
                movie.setActors(actor);
            } else {
                throw new IllegalArgumentException("Actor with ID " + movieDTO.getActors().getId() + " not found.");
            }

            // Update Genre
            Genre genre = em.find(Genre.class, movieDTO.getGenres().getId());
            if (genre != null) {
                movie.setGenres(genre);
            } else {
                throw new IllegalArgumentException("Genre with ID " + movieDTO.getGenres().getId() + " not found.");
            }

            // Merge the updated movie and commit the transaction
            em.merge(movie);
            em.getTransaction().commit();

            // Return the updated MovieDTO
            return new MovieDTO(movie);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Optionally handle the exception better
        } finally {
            em.close();
        }
    }


    @Override
    public void delete(MovieDTO movie) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.remove(movie);
            em.getTransaction().commit();
        }
    }

    @Override
    public MovieDTO findById(MovieDTO movieDTO) {
        return null;
    }

    @Override
    public Movie findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return new MovieDTO(em.find(Movie.class, id));
        }
    }

    @Override
    public List<MovieDTO> findAll() {
        try(EntityManager em = emf.createEntityManager()){
            //vi bruger jpl, hvor man bruger objekter i stedet for sql
          return em.createQuery("select new dtos.MovieDTO(m) FROM movie m", MovieDTO.class).getResultList();
        }
    }

     */
}
