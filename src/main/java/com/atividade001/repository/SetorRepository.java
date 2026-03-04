package com.atividade001.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atividade001.model.Setor;

public interface SetorRepository extends JpaRepository<Setor, Integer> {
    List<Setor> findAllByOrderByNomeAsc();
    Optional<Setor> findByNomeIgnoreCase(String nome);
}
