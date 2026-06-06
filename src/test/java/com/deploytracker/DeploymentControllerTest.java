package com.deploytracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeploymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllDeployments() throws Exception {
        mockMvc.perform(get("/deployments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(greaterThan(30))))
            .andExpect(jsonPath("$.count", greaterThan(30)));
    }

    @Test
    void testGetDeploymentsByService() throws Exception {
        mockMvc.perform(get("/deployments")
                .param("service", "billing-api"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[*].service", everyItem(is("billing-api"))))
            .andExpect(jsonPath("$.count", greaterThan(0)));
    }

    @Test
    void testGetDeploymentsByStatus() throws Exception {
        mockMvc.perform(get("/deployments")
                .param("status", "failed"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[*].status", everyItem(is("failed"))))
            .andExpect(jsonPath("$.count", greaterThan(0)));
    }

    @Test
    void testGetDeploymentsByServiceAndStatus() throws Exception {
        mockMvc.perform(get("/deployments")
                .param("service", "billing-api")
                .param("status", "success"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[*].service", everyItem(is("billing-api"))))
            .andExpect(jsonPath("$.data[*].status", everyItem(is("success"))));
    }

    @Test
    void testGetDeploymentById() throws Exception {
        mockMvc.perform(get("/deployments/deploy_001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("deploy_001"))
            .andExpect(jsonPath("$.service").exists())
            .andExpect(jsonPath("$.status").exists())
            .andExpect(jsonPath("$.duration").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.commitSha").exists());
    }

    @Test
    void testGetDeploymentByIdNotFound() throws Exception {
        mockMvc.perform(get("/deployments/invalid_id"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(containsString("Deployment not found")))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.path").value("/deployments/invalid_id"));
    }

    @Test
    void testResponseStructure() throws Exception {
        mockMvc.perform(get("/deployments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").exists())
            .andExpect(jsonPath("$.data[0].service").exists())
            .andExpect(jsonPath("$.data[0].status").exists())
            .andExpect(jsonPath("$.data[0].duration").isNumber())
            .andExpect(jsonPath("$.data[0].timestamp").exists())
            .andExpect(jsonPath("$.data[0].commitSha").exists())
            .andExpect(jsonPath("$.count").isNumber());
    }
}
