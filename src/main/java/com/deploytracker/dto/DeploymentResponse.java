package com.deploytracker.dto;

import com.deploytracker.model.Deployment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeploymentResponse {
    private List<Deployment> data;
    private int count;
}
