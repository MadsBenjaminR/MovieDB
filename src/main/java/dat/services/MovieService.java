package dat.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dat.dtos.ActorDTO;
import dat.dtos.DirectorDTO;
import dat.dtos.MovieApiResponseDTO;
import dat.dtos.MovieDTO;


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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieService {

    private final String apiKey = System.getenv("API_KEY");
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Set<MovieDTO> getMoviesByName(String movieName) {
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

    public Set<MovieDTO> getMovies(int totalNumberOfPages){
        Set<MovieDTO> movieDTOS = new HashSet<>();

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set.");
            return movieDTOS;
        }
        int currentPage = 1;
        boolean morePagesAvailable = true;

        while (morePagesAvailable) {
            try {

                String url = "https://api.themoviedb.org/"+totalNumberOfPages+"/discover/movie?api_key=" + apiKey +
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
                    movieDTOS = apiResponse.getMovieResults();

                    //for(int i =0;i<movieDTOS.size();i++){
                        for (MovieDTO movieDTO: movieDTOS){



                        MovieApiResponseDTO credits=getCredits(movieDTO.getId());
                        Set<ActorDTO> actors = credits.getListOfActorsDTO();
                        Set<DirectorDTO> directors = credits.getListOfDirectorsDTO().stream()
                                .filter(crew -> "Director".equalsIgnoreCase(crew.getJob()))
                                .collect(Collectors.toSet());

                        movieDTO.setActors(actors);
                        movieDTO.setDirectors(directors);


                        // Add the movie with actors and di
                        // Process the lists of actors and directors as needed
                        //actors.forEach(actor -> System.out.println("Actor: " + actor.getName() + " as " + actor.getCharacter()));
                        //directors.forEach(director -> System.out.println("Director: " + director.getName()));



                    }

                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    File jsonfile=new File("movies.json");
                    mapper.writeValue(jsonfile,movieDTOS);

                    //TODO: metodekald efter vi har fået en liste med film, lav en metode til at få
                    //fat i alle actor/director, cast via api-kald. derefter tilføjes hver enkelt actor/director til hver deres
                    //list

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
/*
    private List<DirectorDTO> getDirectors (Long id) {
        List<DirectorDTO> listOfDirectors = new ArrayList<>();
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set.");
            return listOfDirectors;
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

                    //  mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    //  File jsonfile=new File("movies.json");
                    //  mapper.writeValue(jsonfile,movieDTOS);

                    //TODO: metodekald efter vi har fået en liste med film, lav en metode til at få
                    //fat i alle actor/director, cast via api-kald. derefter tilføjes hver enkelt actor/director til hver deres
                    //list


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

 */

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

                    //  mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    //  File jsonfile=new File("movies.json");
                    //  mapper.writeValue(jsonfile,movieDTOS);

                    //TODO: metodekald efter vi har fået en liste med film, lav en metode til at få
                    //fat i alle actor/director, cast via api-kald. derefter tilføjes hver enkelt actor/director til hver deres
                    //list


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


    /*


    // Method to fetch movies between a specified rating range (e.g., 8.5 to 9.0)
    public List<MovieDTO> getMoviesByRatingRange(double minRating, double maxRating) {
        List<MovieDTO> movies = new ArrayList<>();
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set.");
            return movies;
        }

        int currentPage = 1;
        boolean morePagesAvailable = true;
        int totalNumberOfPages = 3;

        while (morePagesAvailable) {
            try {
                String url = "https://api.themoviedb.org/" + totalNumberOfPages + "/discover/movie?api_key=" + apiKey +
                        "&sort_by=vote_average.desc&include_adult=false&include_video=false" +
                        "&vote_average.gte=" + minRating +
                        "&vote_average.lte=" + maxRating +
                        "&page=" + currentPage +
                        "&language=en-US";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String jsonResponse = response.body();
                    MovieApiResponseDTO apiResponse = mapper.readValue(jsonResponse, MovieApiResponseDTO.class);
                    movies = apiResponse.getMovies();
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

        if (movies.isEmpty()) {
            System.out.println("No movies found in the specified rating range.");
        } else {
            movies.forEach(movie -> System.out.println("Movie: " + movie));
        }

        return movies;
    }

    public List<MovieDTO> getSortedByReleaseDate(String query) {
        try {
            // Construct the search query URL
            String url = "https://api.themoviedb.org/3/search/movie?query=" + query +
                    "&include_adult=false&language=en-US&page=1&api_key=" + apiKey;

            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            // Create an HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the request was successful
            if (response.statusCode() == 200) {
                // Deserialize the response into MovieResponseDTO
                MovieApiResponseDTO apiResponse = mapper.readValue(response.body(), MovieApiResponseDTO.class);

                // Get the list of movies
                List<MovieDTO> movies = apiResponse.getMovies();

                // Sort movies by release date in descending order using Java Streams
                return movies.stream()
                        .filter(movie -> movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty())
                        .sorted((movie1, movie2) -> {
                            LocalDate date1 = LocalDate.parse(movie1.getReleaseDate());
                            LocalDate date2 = LocalDate.parse(movie2.getReleaseDate());
                            return date2.compareTo(date1); // Sort in descending order
                        })
                        .collect(Collectors.toList());
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("The method had returned an empty list");
        return List.of(); // Return an empty list if there's an error
    }

     */

}

