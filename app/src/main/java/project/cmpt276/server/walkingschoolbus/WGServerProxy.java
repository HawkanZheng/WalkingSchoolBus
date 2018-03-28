package project.cmpt276.server.walkingschoolbus;

import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.Message;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.model.walkingschoolbus.lastGpsLocation;
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

    @POST("/users/{id}")
    Call<User> editUser(@Body User user, @Path("id") long id);

    @DELETE("/users/{id}")
    Call<Void> deleteUser();

    //Users last gps location
    @GET("/users/{id}/lastGpsLocation")
    Call<lastGpsLocation> getLastGpsLocation(@Path("id") Long userId);

    @POST("/users/{id}/lastGpsLocation")
    Call<lastGpsLocation> setLastGpsLocation(@Path("id") Long userId, @Body lastGpsLocation location);

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

// Messaging

    //Return all messages
    @GET("/messages")
    Call<List<Message>> getAllMessages();

    //Only return messages with is-emergency flag set
    @GET("/messages?is-emergency=true")
    Call<List<Message>> getEmergencyMessages();

    //Only return messages sent to specific group
    @GET("/messages")
    Call<List<Message>> getMessagesToGroup(@Query("togroup") long groupId);

    @GET("/messages")
    Call<List<Message>> getMessagesToGroupEmergency(@Query("togroup") long groupId, @Query("is-emergency") Boolean emergency);

    //Only return messages for user
    @GET("/messages")
    Call<List<Message>> getMessagesToUser(@Query("foruser") Long userId);

    //Only return messages for user which are unread
    @GET("/messages")
    Call<List<Message>> getMessagesToUserUnread(@Query("foruser") Long userId,@Query("status") String unread);

    //Only return messages for user which are read
    @GET("/messages")
    Call<List<Message>> getMessagesToUserRead(@Query("foruser") Long userId, @Query("status") String read);

    //Only return messages for user which are unread and emergency
    @GET("/messages")
    Call<List<Message>> getMessagesToUserUnreadEmergency(@Query("foruser") Long userId,@Query("status") String unread, @Query("is-emergency") Boolean emergency);

    //New message to group
    @POST("/messages/togroup/{groupId}")
    Call<Message> groupMessage(@Path("groupId") long groupId, @Body Message message);

    //New message to parents of a user
    @POST("/messages/toparentsof/{userId}")
    Call<Message> parentMessage(@Path("userId") Long userId, @Body Message message);

    //Get one message
    @GET("/messages/{id}")
    Call<Message> getMessageById(@Path("id") long id);

    //Delete a message
    @DELETE("/messages/{id}")
    Call<Void> deleteMessage(@Path("id") long id);

    //Mark message as read/unread by user, true for unread and false for read
    @POST("/messages/{messageId}/readby/{userId}")
    Call<User> markMessage(@Path("messageId") long messageId, @Path("userId") Long userId, @Body boolean read);
}
