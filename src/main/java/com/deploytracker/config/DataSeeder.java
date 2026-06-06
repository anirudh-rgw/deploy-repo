package com.deploytracker.config;

import com.deploytracker.model.Deployment;
import com.deploytracker.repository.DeploymentRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final DeploymentRepository repository;
    private final Random random = new Random();

    private static final String[] SERVICES = {
        "billing-api",
        "user-service",
        "payment-gateway",
        "notification-service",
        "analytics-engine",
        "auth-service"
    };

    private static final String[] STATUSES = {"success", "failed", "in_progress"};
    private static final double[] STATUS_WEIGHTS = {0.6, 0.3, 0.1};

    public DataSeeder(DeploymentRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seedData() {
        logger.info("Seeding database with deployment data...");

        List<Deployment> deployments = new ArrayList<>();
        int deploymentCount = 32;

        for (int i = 1; i <= deploymentCount; i++) {
            String id = String.format("deploy_%03d", i);
            String service = SERVICES[(i - 1) % SERVICES.length];
            String status = getWeightedRandomStatus();
            Integer duration = 30 + random.nextInt(571);
            LocalDateTime timestamp = LocalDateTime.now().minusDays(random.nextInt(30))
                .minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60));
            String commitSha = generateRandomCommitSha();

            deployments.add(new Deployment(id, service, status, duration, timestamp, commitSha));
        }

        repository.saveAll(deployments);

        long successCount = deployments.stream().filter(d -> "success".equals(d.getStatus())).count();
        long failedCount = deployments.stream().filter(d -> "failed".equals(d.getStatus())).count();
        long inProgressCount = deployments.stream().filter(d -> "in_progress".equals(d.getStatus())).count();

        logger.info("Seeding complete: {} deployments", deploymentCount);
        logger.info("Services: {}, Success: {}, Failed: {}, In Progress: {}",
            SERVICES.length, successCount, failedCount, inProgressCount);
    }

    private String getWeightedRandomStatus() {
        double value = random.nextDouble();
        double cumulative = 0.0;

        for (int i = 0; i < STATUSES.length; i++) {
            cumulative += STATUS_WEIGHTS[i];
            if (value <= cumulative) {
                return STATUSES[i];
            }
        }

        return STATUSES[0];
    }

    private String generateRandomCommitSha() {
        int length = 6 + random.nextInt(3);
        StringBuilder sha = new StringBuilder();
        String hexChars = "0123456789abcdef";

        for (int i = 0; i < length; i++) {
            sha.append(hexChars.charAt(random.nextInt(hexChars.length())));
        }

        return sha.toString();
    }
}
