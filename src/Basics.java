import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Basics {

    public static void main(String[] args) {

        //end to end test -- use API to add a place, update the address, then get the place to verify change

        RestAssured.baseURI= "https://rahulshettyacademy.com";
        RestAssured.useRelaxedHTTPSValidation();    //needed to resolve SSL errors

        //add place
        System.out.println("Adding a place");
        String response =
        given().log().all().queryParam("key", "qaclick123").header("Content-Type", "application/json")
                .body(Payload.addPlace())   //abstract out payload
                .when().post("maps/api/place/add/json")
                .then().log().all().assertThat().statusCode(200)
                .body("scope", equalTo("APP"))  //verify body
                .header("server", "Apache/2.4.18 (Ubuntu)") //verify a header
                .extract().response().asString();
        System.out.println(response);

        JsonPath js = new JsonPath(response);   //convert Json to String
        String placeId = js.getString("place_id");  //get place_id value from response body
        System.out.println(placeId);


        //update address
        System.out.println("Updating the address");
        String newAddress = "88 Lego Land, USA";
        given().log().all().queryParam("key", "qaclick123").header("Content-Type", "application/json")
                .body("{\n" +
                        "\"place_id\":\""+placeId+"\",\n" +
                        "\"address\":\""+newAddress+"\",\n" +
                        "\"key\":\"qaclick123\"\n" +
                        "}\n")
                .when().put("maps/api/place/update/json")
                .then().log().all().assertThat().statusCode(200).body("msg", equalTo("Address successfully updated"));


        //getting the place and verifying the new address
        System.out.println("Verifying the address");
//        given().log().all().queryParam("key", "qaclick123").queryParam("place_id", placeId)
//                .when().get("maps/api/place/get/json")
//                .then().log().all().assertThat().statusCode(200).body("address", equalTo(newAddress));

//        or use TestNG:

        String getResponse = given().log().all().queryParam("key", "qaclick123").queryParam("place_id", placeId)
                .when().get("maps/api/place/get/json")
                .then().log().all().assertThat().statusCode(200)
                        .extract().response().asString();
        JsonPath js1 = new JsonPath(getResponse);
        String actualAddress = js1.getString("address");
        Assert.assertEquals(actualAddress, newAddress);

    }
}
