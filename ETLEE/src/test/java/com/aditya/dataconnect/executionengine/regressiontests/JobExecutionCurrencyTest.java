package com.aditya.dataconnect.executionengine.regressiontests;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


@Slf4j
@ActiveProfiles("local")
public class JobExecutionCurrencyTest {
    private static final String BASE_URL_EXECUTE = "http://localhost:8082/data-connect/data-integration/jobs/execute/";
    private static final String BASE_URL_STATUS = "http://localhost:8080/data-connect/execution-engine/data-connect/jobs/execution-logs/";

    private static final String JOB_ID = "7236a216-4b10-4569-9ee9-e49cbf255804";



    @Test
    @Disabled
    public void testJobExecutionAndStatus() throws InterruptedException {
        // Step 1: Execute the job
        String requestBody = "{ \"data_unit_list\": { \"object_ids\": [] } }";

        Response executeResponse = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(BASE_URL_EXECUTE + JOB_ID + "?operationType=CREATE");
        log.info("Job Execute Url - {}", BASE_URL_EXECUTE + JOB_ID + "?operationType=CREATE");
        log.info("Job execute response - {}", executeResponse.getBody().asString());
        assertEquals(201, executeResponse.getStatusCode());

        // Step 2: Extract execution ID from response
        String responseBody = executeResponse.getBody().asString();

        String execId = responseBody.replace("Job is executed with id : ", "").trim().replace("\"", "");

        // Step 3: Wait 2 seconds before polling
        Thread.sleep(2000);

        // Step 4: Poll for status up to 5 times
        boolean isCompleted = false;
        for (int i = 0; i < 10; i++) {
            log.info("Base status URL - {}", BASE_URL_STATUS + execId);
            Response statusResponse = given()
                    .get(BASE_URL_STATUS + execId);

            assertEquals(200, statusResponse.getStatusCode());

            String status = statusResponse.jsonPath().getString("status");
            System.out.println("Attempt " + (i + 1) + ": Status = " + status);

            if ("COMPLETED".equalsIgnoreCase(status)) {
                isCompleted = true;
                break;
            }

            Thread.sleep(2000); // wait 2 seconds before next attempt
        }

        if (!isCompleted) {
            fail("Job did not complete within 5 attempts.");
        }
    }
}

