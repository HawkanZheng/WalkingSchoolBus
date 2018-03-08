package project.cmpt276.server.walkingschoolbus;

import java.util.List;

import project.cmpt276.model.walkingschoolbus.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The ProxyBuilder class will handle the apiKey and token being injected as a header to all calls
 * This is a Retrofit interface.
 */
public interface WGServerProxy {
    @GET("getApiKey")
    Call<String> getApiKey(@Query("groupName") String groupName, @Query("sfuUserId") String sfuId);

    @POST("/users/signup")
    Call<User> createNewUser(@Body User user);

    @POST("/login")
    Call<Void> login(@Body User userWithEmailAndPassword);

    @GET("/users")
    Call<List<User>> getUsers();

    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") Long userId);

    @GET("/users/byEmail")
    Call<User> getUserByEmail(@Query("email") String email);

    /**
     * MORE GOES HERE:
     * - Monitoring
     * - Groups
     */

    //Monitoring
    @GET("/users/{id}/monitorsUsers")
    Call<List<User>> getUsersMonitered();

    @GET("/users/{id}/monitoredByUsers")
    Call<List<User>> getUsersMoniteredBy();

    @POST("/users/{id}/monitorsUsers")
    Call<List<User>> addUserToMonitor(@Body User userWithID);

    @DELETE("/users/{idA}/monitorsUsers/{idB}")
    Call<Void> stopMonitoringUser();

    //Groups







}
