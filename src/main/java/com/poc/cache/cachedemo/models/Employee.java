package com.poc.cache.cachedemo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String role;
    private String phoneNumber;
}
