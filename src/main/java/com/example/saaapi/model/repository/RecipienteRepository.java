package com.example.saaapi.model.repository;

import com.example.saaapi.model.entity.Recipiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipienteRepository extends JpaRepository<Recipiente, Long> {
    @Query("select count(recipiente) from Recipiente recipiente where recipiente.sala.id = :idSala")
    long contarPorIdSala(@Param("idSala") Long idSala);
}
