package com.deploytracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "deployments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deployment {

    @Id
    private String id;

    private String service;

    private String status;

    private Integer duration;

    private LocalDateTime timestamp;

    private String commitSha;
}
