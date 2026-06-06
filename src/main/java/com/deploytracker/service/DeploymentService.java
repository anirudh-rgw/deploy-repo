package com.deploytracker.service;

import com.deploytracker.exception.DeploymentNotFoundException;
import com.deploytracker.model.Deployment;
import com.deploytracker.repository.DeploymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeploymentService {

    private final DeploymentRepository repository;

    public DeploymentService(DeploymentRepository repository) {
        this.repository = repository;
    }

    public List<Deployment> getAllDeployments() {
        return repository.findAll();
    }

    public Deployment getDeploymentById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new DeploymentNotFoundException(id));
    }

    public List<Deployment> getDeploymentsByFilters(String service, String status) {
        if (service != null && status != null) {
            return repository.findByServiceAndStatus(service, status);
        } else if (service != null) {
            return repository.findByService(service);
        } else if (status != null) {
            return repository.findByStatus(status);
        } else {
            return repository.findAll();
        }
    }
}
