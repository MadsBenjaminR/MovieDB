package dat.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
*Purpose: 
* @author: Jeppe Koch
*/
public class HttpClientExample {
    public static void main(String[] args){
        try {
            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            String apiKey = System.getenv("API_KEY");
            String imdbId = "The Dark Knight"; // Example movie: The Godfather
            String encodeMoveTitle= URLEncoder.encode(imdbId, StandardCharsets.UTF_8);

            String url = "https://api.themoviedb.org/3/search/movie?query=" + encodeMoveTitle + "&include_adult=false&language=en-US&page=1&api_key=" + apiKey;
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
                ObjectMapper mapper = new ObjectMapper();


                // Deserialize the JSON response into MovieApiResponse object
              //  MovieApiResponse apiResponse = mapper.readValue(jsonResponse, MovieApiResponse.class);

               // List<MovieDTO> movieDTO1=apiResponse.getMovie_results();

               // List<MovieDTO> popularMovies = movieDTO1.stream().filter(movieDTO -> movieDTO.getPopularity()> 20).collect(Collectors.toList());


                if ("" != null) {

                    // Write the popular movies to a JSON file
                    mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print the JSON
                    File jsonFile = new File("//Users/mingo/Desktop/sem3/ProjectSture/movieapi/src/main/resources/popular_movies.json");
                    mapper.writeValue(jsonFile, ""); // Save to file

                    System.out.println(jsonFile.getAbsolutePath());


                } else {
                    System.out.println("No movies found in the response.");
                }
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
