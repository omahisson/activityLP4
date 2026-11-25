package com.example.saaapi.model.repository;

import com.example.saaapi.model.entity.Armazenamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArmazenamentoRepository extends JpaRepository<Armazenamento, Long> {
}
