package com.example.saaapi.model.repository;

import com.example.saaapi.model.entity.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface DepositoRepository extends JpaRepository<Deposito, Long> {
    @Query("select deposito from Deposito deposito join deposito.salas sala where sala.id = :idSala")
    Optional<Deposito> buscarPorIdSala(@Param("idSala") Long idSala);
}
