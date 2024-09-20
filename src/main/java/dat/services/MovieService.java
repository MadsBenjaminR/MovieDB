package dat.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dat.dtos.*;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MovieService {

    private static final  String apiKey = System.getenv("API_KEY");
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static Map<Integer, GenreDTO> genreDTOs; // Declare genreDTOs here


    public static Set<MovieDTO> getMoviesByName(String movieName) {
        Set<MovieDTO> movies = null;
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set.");
        }

        try {
            String encodedMovieTitle = URLEncoder.encode(movieName, StandardCharsets.UTF_8);
            String url = "https://api.themoviedb.org/3/search/movie?query=" + encodedMovieTitle
                    + "&include_adult=false&language=en-US&page=1&api_key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonResponse = response.body();
                MovieApiResponseDTO apiResponse = mapper.readValue(jsonResponse, MovieApiResponseDTO.class);
                movies = apiResponse.getMovieResults();

                if (movies != null && !movies.isEmpty()) {
                    movies.forEach(movie -> System.out.println("Movie: " + movie));
                    return movies;
                } else {
                    System.out.println("No movies found.");
                }
            } else {
                System.err.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.err.println("Error during API request: " + e.getMessage());
            e.printStackTrace();
        }
        return movies;
    }

    public MovieDTO getMovieByID(int movieID) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set man.");
        }

        try {

            String url = "https://api.themoviedb.org/3/movie/" + movieID + "?language=en-US&api_key=" + apiKey;

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() == 200) {
                String jsonResponse = response.body();
                MovieDTO movie = mapper.readValue(jsonResponse, MovieDTO.class);
                System.out.println(movie);
                return movie;
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<MovieDTO> getMovies(int totalNumberOfPages) {
        Set<MovieDTO> movieDTOS = new HashSet<>();

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set.");
            return movieDTOS;
        }

        // Call getGenres to populate genreDTOs
        getGenres();
        if (genreDTOs == null || genreDTOs.isEmpty()) {
            System.err.println("Failed to fetch or parse genre data.");
            return movieDTOS;
        }

        int currentPage = 1;
        boolean morePagesAvailable = true;

        while (morePagesAvailable) {
            try {
                String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                        "&include_adult=false&include_video=false&language=en-US&page=" + currentPage +
                        "&primary_release_date.gte=2000-01-01&sort_by=popularity.desc&with_original_language=en";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .header("accept", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String jsonResponse = response.body();
                    MovieApiResponseDTO apiResponse = mapper.readValue(jsonResponse, MovieApiResponseDTO.class);
                    Set<MovieDTO> moviesFromPage = apiResponse.getMovieResults();

                    for (MovieDTO movieDTO : moviesFromPage) {
                        MovieApiResponseDTO credits = getCredits(movieDTO.getId());
                        Set<ActorDTO> actors = credits.getListOfActorsDTO();
                        Set<DirectorDTO> directors = credits.getListOfDirectorsDTO().stream()
                                .filter(crew -> "Director".equalsIgnoreCase(crew.getJob()))
                                .collect(Collectors.toSet());

                        Set<GenreDTO> genreDTOSet;
                        genreDTOSet=movieDTO.getGenre_ids().stream().map(id->genreDTOs.get(id)).collect(Collectors.toSet());


                        movieDTO.setActors(actors);
                        movieDTO.setDirectors(directors);
                        movieDTO.setGenres(genreDTOSet);


                        movieDTOS.add(movieDTO);
                    }

                    // Write results to file
                    /*
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    File jsonFile = new File("movies-genres.json");
                    mapper.writeValue(jsonFile, movieDTOS);

                     */

                    if (currentPage >= totalNumberOfPages) {
                        morePagesAvailable = false;
                    } else {
                        currentPage++;
                    }
                } else {
                    System.err.println("GET request failed. Status code: " + response.statusCode());
                    morePagesAvailable = false;
                }
            } catch (URISyntaxException | IOException | InterruptedException e) {
                System.err.println("Error during API request: " + e.getMessage());
                e.printStackTrace();
                morePagesAvailable = false;
            }
        }

        if (movieDTOS.isEmpty()) {
            System.out.println("No movies found in the specified rating range.");
        } else {
            movieDTOS.forEach(movie -> System.out.println("Movie: " + movie));
        }

        return movieDTOS;
    }

    public void getGenres() {
        try {
            String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey + "&language=en-US";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .header("accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonResponse = response.body();
                GenreListDTO genreResponse = mapper.readValue(jsonResponse, GenreListDTO.class);
                genreDTOs = genreResponse.getGenres().stream()
                        .collect(Collectors.toMap(GenreDTO::getId,genreDTO -> genreDTO));
            } else {
                System.err.println("GET request failed. Status code: " + response.statusCode());
                System.err.println("Response body: " + response.body());
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI Syntax: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Request Interrupted: " + e.getMessage());
        }
    }


    private MovieApiResponseDTO getCredits(Long id) {
        MovieApiResponseDTO credits = new MovieApiResponseDTO();
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set.");
            return credits;
        }

        try {
            String url = "https://api.themoviedb.org/3/movie/" + id + "/credits?api_key=" + apiKey + "&language=en-US";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .header("accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonResponse = response.body();

                // Map the response to MovieCreditsApiResponseDTO
                credits = mapper.readValue(jsonResponse, MovieApiResponseDTO.class);

            } else {
                System.err.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.err.println("Error during API request: " + e.getMessage());
            e.printStackTrace();
        }

        return credits;
    }


    public Set<ActorDTO> getActors(Long id){
        Set<ActorDTO> listOfActorsDTO = new HashSet<>();
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set.");
            return listOfActorsDTO;
        }
        int currentPage = 1;
        boolean morePagesAvailable = true;

        while (morePagesAvailable) {
            try {

                String url = "https://api.themoviedb.org/3/movie/"+ id +"/credits?language=en-US";


                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .header("accept", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String jsonResponse = response.body();
                    MovieApiResponseDTO apiResponse = mapper.readValue(jsonResponse, MovieApiResponseDTO.class);
                    listOfActorsDTO = apiResponse.getListOfActorsDTO();


                } else {
                    System.err.println("GET request failed. Status code: " + response.statusCode());
                    morePagesAvailable = false;
                }
            } catch (URISyntaxException | IOException | InterruptedException e) {
                System.err.println("Error during API request: " + e.getMessage());
                e.printStackTrace();
                morePagesAvailable = false;
            }
        }

        if (listOfActorsDTO.isEmpty()) {
            System.out.println("No movies found in the specified rating range.");
        } else {
            listOfActorsDTO.forEach(movie -> System.out.println("Movie: " + movie));
        }

        return listOfActorsDTO;


    }


}

