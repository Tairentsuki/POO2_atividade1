package com.atividade001.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.atividade001.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    @Query("select f from Funcionario f left join fetch f.setor order by f.id")
    List<Funcionario> findAllComSetorOrdenados();
}
