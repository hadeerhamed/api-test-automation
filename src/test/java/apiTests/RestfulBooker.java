package apiTests;

import com.beust.ah.A;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RestfulBooker {
    String token;
    int bookingId;

    @BeforeMethod
    public void loginToApp(){
        String endpoint="https://restful-booker.herokuapp.com/auth";
        String body = """
                {
                    "username" : "admin",
                    "password" : "password123"
                }
                """;
ValidatableResponse validatableResponse = given().body(body)
                .header("content-Type","application/json")
        .log().all()
                .when().post(endpoint).
            then();
Response response=validatableResponse.extract().response();
        JsonPath jsonPath = response.jsonPath();
        token = jsonPath.getString("token");
        System.out.println(token);

    }
   @Test(priority = 0)
public void testCreateBooking(){
        String endPoint= "https://restful-booker.herokuapp.com/booking";
        String body = """
                {
                    "firstname" : "Jim",
                    "lastname" : "Brown",
                    "totalprice" : 111,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2018-01-01",
                "checkout" : "2019-01-01"
                    },
                    "additionalneeds" : "Breakfast"
                }
                """;
        ValidatableResponse validatableResponse= given().body(body)
                .header("Content-Type","application/json")
                .log().all().when().post(endPoint).then();
        validatableResponse.body("booking.firstname",equalTo("Jim"));
              validatableResponse.body("booking.lastname",equalTo("Brown"));
        validatableResponse.statusCode(200);
Response response=validatableResponse.extract().response();
JsonPath jsonPath=response.jsonPath();
bookingId=jsonPath.getInt("bookingid");

}
    @Test(priority = 1)
    public void testEditBooking(){
        String endpoint = "https://restful-booker.herokuapp.com/booking/" +bookingId;

        String body= """
                {
                    "firstname" : "James",
                    "lastname" : "Brown",
                    "totalprice" : 111,
                    "depositpaid" : true,"bookingdates" : {
                        "checkin" : "2018-01-01",
                        "checkout" : "2019-01-01"
                    },
                    "additionalneeds" : "Breakfast"
                }
                """;
        ValidatableResponse validatableResponse=given().body(body)
                .header("Content-Type","application/json")
                .header("Accept" , "application/json")
                .header("Cookie","token="+token)
                .log().all().when().put(endpoint).then();
        validatableResponse.body("firstname", equalTo("James"));
        validatableResponse.statusCode(200);

    }
    @Test(priority = 2)
    public void testGetBooking(){
        String endpoint="https://restful-booker.herokuapp.com/booking/"+bookingId;
        ValidatableResponse validatableResponse=given()
                .header("Content-Type", "application/json")
                .log().all().when().get(endpoint).then();
      validatableResponse.body("firstname", equalTo("James"));

      validatableResponse.statusCode(200);



    }
    @Test(priority = 3)
    public void testDeletedBooking(){
        String endpoint = "https://restful-booker.herokuapp.com/booking/" +bookingId;
        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .header("cookie" , "token=" + token)
                .log().all().when().delete(endpoint).then();

       validatableResponse.statusCode(201);
        Response response = validatableResponse.extract().response();
        Assert.assertEquals(response.asString() , "Created");
    }

    }


    

