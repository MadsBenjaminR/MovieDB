package dat;


import dat.daos.MovieDAO;
import dat.dtos.MovieDTO;
import dat.services.JsonService;
import dat.services.MovieService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        MovieService movieService = new MovieService();
        MovieDAO movieDAO = new MovieDAO();

        String releaseDate = "2019-01-01";
        

       // Set<MovieDTO> movieDTOS = movieService.getMovies(15);
        Set<MovieDTO> movieDTOS = movieService.getDanishMovieByAfterYear(releaseDate, 15);

        movieDAO.create(movieDTOS);
        movieDAO.establishRelationships(movieDTOS);


    }
}