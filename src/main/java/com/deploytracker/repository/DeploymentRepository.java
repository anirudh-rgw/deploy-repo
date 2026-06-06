package com.deploytracker.repository;

import com.deploytracker.model.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, String> {

    List<Deployment> findByService(String service);

    List<Deployment> findByStatus(String status);

    List<Deployment> findByServiceAndStatus(String service, String status);
}
