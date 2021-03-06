package com.ARA;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * implementation of Car Test - CRUD & Token Test (create a car with token verified)
 * @author Edam & Ruby
 * @version 4.0.0
 * @Note: Action 1 && action 2 are needed for test
 *        1. Mark out email duplicate validation in DriverDAO 123-127
 *        2. Change required every test run -
 *        - "emailAddress" in requestBodyX
 *        - "email" in requestBodyToken (email & password need to match requestBodyX)
 */

public class CarTest {
    String testCarID;
    String testCarNGID;
    String testDriverXID;
    String token;

    String requestBodyCar = "{" +
            "'make':'Tesla'," +
            "'model':'S'," +
            "'license':'12345'," +
            "'carType':'sedan'," +
            "'maxPassengers':10," +
            "'color':'White'," +
            "'validRideTypes':  [ \"EXECUTIVE\" ]" +
            "}";

    String requestBodyDriver = "{" +
            "'firstName':'Mark'," +
            "'lastName':'Azi'," +
            "'emailAddress':'qq999@att.com'," +
            "'password':'1234567890'," +
            "'addressLine1':'120 El, CA'," +
            "'addressLine2':''," +
            "'city':'MV'," +
            "'state':'CA'," +
            "'zip':'99900'," +
            "'phoneNumber':'333-999-0000'," +
            "'drivingLicense':'X7890'," +
            "'licensedState':'CA'" +
            "}";

    String requestBodyToken = "{" +
            "'email':'qq999@att.com'," +
            "'password':'1234567890'" +
            "}";

    /** This test is used to test Car.
     * 1. create driver for token use
     * 2. create token for driver
     * 3. create a car
     * 4. get car
     * 5. patch car
     * 6. delete car
     * 7. should not create a car with missing make
     * 8. should not create a car with long make
     * */
    @Test
    public void Car() throws IOException {

        //create a driver for token use
        TestResponse resPostX = TestResponse.request("POST", "/v1/drivers", requestBodyDriver);
        Map<String, String> jsonPostX = resPostX.json();
        assertEquals(200, resPostX.status);
        assertEquals("Mark", jsonPostX.get("firstName"));
        assertEquals("Azi", jsonPostX.get("lastName"));
        assertEquals("120 El, CA", jsonPostX.get("addressLine1"));
        assertEquals("", jsonPostX.get("addressLine2"));
        assertEquals("MV", jsonPostX.get("city"));
        assertEquals("CA", jsonPostX.get("state"));
        assertEquals("99900", jsonPostX.get("zip"));
        assertEquals("333-999-0000", jsonPostX.get("phoneNumber"));
        assertEquals("X7890", jsonPostX.get("drivingLicense"));
        assertEquals("CA", jsonPostX.get("licensedState"));
        assertNotNull(jsonPostX.get("id"));
        testDriverXID = jsonPostX.get("id");

        //get token for the driver
        TestResponse resToken = TestResponse.request("POST", "/v1/sessions", requestBodyToken);
        Map<String, String> jsonToken = resToken.json();
        assertEquals(200, resToken.status);
        assertNotNull(jsonToken.get("token"));

        token = jsonToken.get("token");

        //create a car for the driver
        TestResponse resCarCreate = TestResponse.request("POST", "/v1/drivers/"+testDriverXID+"/cars", requestBodyCar, token);
        Map<String, String> jsonCarCreate = resCarCreate.json();
        assertEquals(200, resCarCreate.status);
        assertEquals("Tesla", jsonCarCreate.get("make"));
        assertEquals("S", jsonCarCreate.get("model"));
        assertEquals("12345", jsonCarCreate.get("license"));
        assertEquals("sedan", jsonCarCreate.get("carType"));
        assertEquals("White", jsonCarCreate.get("color"));
        assertEquals(Double.valueOf(10), jsonCarCreate.get("maxPassengers"));
        assertEquals(Arrays.asList("EXECUTIVE"), jsonCarCreate.get("validRideTypes"));
        assertNotNull(jsonCarCreate.get("id"));

        testCarID = jsonCarCreate.get("id");

        //getCar just created
        TestResponse resGet = TestResponse.request("GET", "/v1/cars/"+testCarID+"");
        Map<String, String> jsonGet = resGet.json();
        assertEquals(200, resGet.status);
        assertEquals("Tesla", jsonGet.get("make"));
        assertEquals("S", jsonGet.get("model"));
        assertEquals("12345", jsonGet.get("license"));
        assertEquals("sedan", jsonGet.get("carType"));
        assertEquals("White", jsonGet.get("color"));
        assertEquals(Double.valueOf(10), jsonGet.get("maxPassengers"));
        assertEquals(Arrays.asList("EXECUTIVE"), jsonGet.get("validRideTypes"));
        assertNotNull(jsonGet.get("id"));

        //patchCar
        String requestBodyPatch = "{" +
                "'make':'Honda'" +
                "}";
        TestResponse resPatch = TestResponse.request("PATCH", "/v1/cars/"+testCarID+"",requestBodyPatch);
        Map<String, String> jsonPatch = resPatch.json();
        assertEquals(200, resPatch.status);
        assertEquals("Honda", jsonPatch.get("make"));
        assertEquals("S", jsonPatch.get("model"));
        assertEquals("12345", jsonPatch.get("license"));
        assertEquals("sedan", jsonPatch.get("carType"));
        assertEquals("White", jsonPatch.get("color"));
        assertEquals(Double.valueOf(10), jsonPatch.get("maxPassengers"));
        assertEquals(Arrays.asList("EXECUTIVE"), jsonPatch.get("validRideTypes"));
        assertNotNull(jsonPatch.get("id"));

        //patchCar - Change back to Tesla
        String requestBodyPatch2 = "{" +
                "'make':'Tesla'" +
                "}";
        TestResponse resPatch2 = TestResponse.request("PATCH", "/v1/cars/"+testCarID+"",requestBodyPatch2);
        Map<String, String> jsonPatch2 = resPatch2.json();
        assertEquals(200, resPatch2.status);
        assertEquals("Tesla", jsonPatch2.get("make"));
        assertEquals("S", jsonPatch2.get("model"));
        assertEquals("12345", jsonPatch2.get("license"));
        assertEquals("sedan", jsonPatch2.get("carType"));
        assertEquals("White", jsonPatch2.get("color"));
        assertEquals(Double.valueOf(10), jsonPatch2.get("maxPassengers"));
        assertEquals(Arrays.asList("EXECUTIVE"), jsonPatch2.get("validRideTypes"));
        assertNotNull(jsonPatch2.get("id"));

        //deleteCar
        TestResponse resDelete = TestResponse.request("DELETE", "/v1/cars/"+testCarID+"");
        Map<String, String> jsonDelete  = resDelete .json();
        assertEquals(200, resDelete.status);
        assertEquals("Tesla", jsonDelete.get("make"));
        assertEquals("S", jsonDelete.get("model"));
        assertEquals("12345", jsonDelete.get("license"));
        assertEquals("sedan", jsonDelete.get("carType"));
        assertEquals("White", jsonDelete.get("color"));
        assertEquals(Double.valueOf(10), jsonPatch2.get("maxPassengers"));
        assertEquals(Arrays.asList("EXECUTIVE"), jsonPatch2.get("validRideTypes"));
        assertNotNull(jsonPatch.get("id"));

        testCarNGID = jsonPatch.get("id");

        //should not get deleted car
        TestResponse resGetNG = TestResponse.request("GET", "/v1/cars/"+testCarNGID+"");
        assertEquals(400, resGetNG.status);

        //should not create a car with missing make
        String requestBodyCreateMissingMake = "{" +
                "'model':'S'," +
                "'license':'12345'," +
                "'carType':'sedan'," +
                "'maxPassengers':10," +
                "'color':'White'," +
                "'validRideTypes':  [ \"EXECUTIVE\" ]" +
                "}";
        TestResponse resCreateMissingMake = TestResponse.request("POST", "/v1/drivers/"+testDriverXID+"/cars", requestBodyCreateMissingMake, token);
        assertEquals(500, resCreateMissingMake.status);

        //should not create a car with long make
        String requestBodyCreateLongMake = "{" +
                "'make':'TeslaToyotaAudiBMWTeslaToyotaAudiBMWTeslaToyotaAudiBMWTeslaToyotaAudiBMWTeslaToyotaAudiBMW'," +
                "'model':'S'," +
                "'license':'12345'," +
                "'carType':'sedan'," +
                "'maxPassengers':10," +
                "'color':'White'," +
                "'validRideTypes':  [ \"EXECUTIVE\" ]" +
                "}";
        TestResponse resCreateLongMake = TestResponse.request("POST", "/v1/drivers/"+testDriverXID+"/cars", requestBodyCreateLongMake, token);
        assertEquals(400, resCreateLongMake.status);
    }
}
