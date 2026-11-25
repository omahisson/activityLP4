package com.example.saaapi.model.repository;

import com.example.saaapi.model.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Long> {
}
