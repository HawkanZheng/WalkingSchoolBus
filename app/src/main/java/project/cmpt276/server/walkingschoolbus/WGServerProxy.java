package project.cmpt276.server.walkingschoolbus;

import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
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

    @DELETE("/users/{id}")
    Call<Void> deleteUser();

    /**
     * MORE GOES HERE:
     * - Monitoring
     * - Groups
     */

    //Monitoring
    @GET("/users/{id}/monitorsUsers")
    Call<List<User>> getUsersMonitered(@Path("id") Long userId);

    @GET("/users/{id}/monitoredByUsers")
    Call<List<User>> getUsersMonitoredBy(@Path("id") Long userId);

    @POST("/users/{id}/monitorsUsers")
    Call<List<User>> addUserToMonitor(@Path("id") Long userId, @Body User user);

    @POST("/users/{id}/monitoredByUsers")
    Call<List<User>> addUserMonitoredBy(@Path("id") Long userId, @Body User user);

    @DELETE("/users/{idA}/monitorsUsers/{idB}")
    Call<Void> stopMonitoringUser(@Path("idA") Long userIdA, @Path("idB") Long userIdB);

    @DELETE("/users/{idA}/monitoredByUsers/{idB}")
    Call<Void> stopBeingMonitoredByUser(@Path("idA") Long userIdA, @Path("idB") Long userIdB);


    //Groups

    @GET("/groups")
    Call<List<Group>> getGroups();

    @POST("/groups")
    Call<Group> createGroup(@Body Group group);



    @GET("/groups/{id}")
    Call<Group> getGroupById(@Path("id") long groupId);

    @POST("/groups/{id}")
    Call<Group> updateGroup(@Path("id") long groupId, @Body Group group);

    @DELETE("/groups/{id}")
    Call<Void> deleteGroup(@Path("id") long groupId);

    //Group members

    @GET("/groups/{id}/memberUsers")
    Call<List<User>> getGroupMembers(@Path("id") long groupId);

    @POST("/groups/{id}/memberUsers")
    Call<List<User>> addNewMember(@Path("id") long groupId, @Body User user);

    @DELETE("/groups/{groupId}/memberUsers/{userId}")
    Call<Void> deleteGroupMember(@Path("groupId") long groupId, @Path("userId") Long userId);





}
