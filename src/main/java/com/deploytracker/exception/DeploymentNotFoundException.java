package com.deploytracker.exception;

public class DeploymentNotFoundException extends RuntimeException {

    public DeploymentNotFoundException(String id) {
        super("Deployment not found: " + id);
    }
}
