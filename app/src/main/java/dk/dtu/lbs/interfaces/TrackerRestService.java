package dk.dtu.lbs.interfaces;

import java.util.List;

import dk.dtu.lbs.dto.GeoCoordinate;
import dk.dtu.lbs.dto.Profile;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by Bahram Moradi on 15-11-2016.
 */

public interface TrackerRestService {
    @POST("users")
    Call<Profile> create(@Body Profile user);

    @PUT("users")
    Call<Profile> update(@Body Profile user);

    @DELETE("users/{uid}")
    Call<Profile> deleteUser(@Path("uid") long uid);



        /*Method related to user location*/

    /**
     * This method retrieve users all location
     * @param uid : user id of user
     * @return
     */
    @GET("locations/{uid}")
    Call<List<GeoCoordinate>> getUserLocations(@Path("uid") long uid);


    @GET("locations/{uid}/{from}/{to}")
    Call<List<GeoCoordinate>> getUserLocationsInTimeInterval(@Path("uid") long uid,@Path("from") long from,@Path("to") long to);

    /**
     * This method is for posting a list of location of a user
     * @param uid : id of user
     * @param locations : a list of user location
     * @return
     */
    @POST("locations/{uid}")
    Call<List<GeoCoordinate>>postUserLocations(@Path("uid") long uid,@Body List<GeoCoordinate> locations);

    @DELETE("locations/{uid}")
    Call<GeoCoordinate> deleteUserLocations(@Path("uid") long uid);
    @DELETE("locations/{uid}/{from}/{to}")
    Call<GeoCoordinate> deleteUserLocationsInTimeInterval(@Path("uid") long uid,@Path("from")long from,@Path("to")long to);





    /** just fpr inspiration
     @POST("location/{uid}")
     Call<AppLocation> getUsersLastLocation(@Path("uid") long uid);

     @POST("location/update/{uid}")
     Call<AppLocation> updateUserLocation(@Path("uid") long uid, @Body AppLocation location);

     @POST("location/userslocation")
     Call<List<UserLocation>> getUsersLocation(@Body List<Profile> users);

     @POST("location/{uid}/{radius}")
     Call<UserLocation> getAllUserInRadius(@Path("uid") long uid, @Path("radius") float radius);

     @POST("location/{latitude}/{longitude}/{radius}")
     Call<UserLocation> getAllUserInRadiusFromLocation(@Path("latitude") double latitude, @Path("longitude") double longitude, @Path("radius") float radius);
     **/

}
