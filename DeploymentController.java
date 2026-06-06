package com.deploytracker.controller;

import com.deploytracker.dto.DeploymentResponse;
import com.deploytracker.model.Deployment;
import com.deploytracker.service.DeploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deployments")
public class DeploymentController {

    private final DeploymentService service;

    public DeploymentController(DeploymentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<DeploymentResponse> getDeployments(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String status) {

        List<Deployment> deployments = this.service.getDeploymentsByFilters(service, status);
        DeploymentResponse response = new DeploymentResponse(deployments, deployments.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deployment> getDeploymentById(@PathVariable String id) {
        Deployment deployment = service.getDeploymentById(id);
        return ResponseEntity.ok(deployment);
    }
}
