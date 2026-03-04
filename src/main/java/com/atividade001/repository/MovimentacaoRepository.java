package com.atividade001.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.atividade001.model.Movimentacao;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {
    @EntityGraph(attributePaths = { "setor", "funcionario" })
    List<Movimentacao> findAllByOrderByDataDescIdDesc();
}
