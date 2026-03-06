package com.atividade001.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.atividade001.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    @Query("select f from Funcionario f left join fetch f.setor order by f.id")
    List<Funcionario> findAllComSetorOrdenados();

    @Query("""
            select coalesce(s.nome, 'Sem setor'), count(f)
            from Funcionario f
            left join f.setor s
            group by s.nome
            order by s.nome
            """)
    List<Object[]> countFuncionariosPorSetor();

    @Query("""
            select coalesce(s.nome, 'Sem setor'), coalesce(avg(f.salarioBruto), 0)
            from Funcionario f
            left join f.setor s
            group by s.nome
            order by s.nome
            """)
    List<Object[]> avgSalarioBrutoPorSetor();

    @Query("""
            select function('year', f.dataAdmissao), count(f)
            from Funcionario f
            where f.dataAdmissao is not null
            group by function('year', f.dataAdmissao)
            order by function('year', f.dataAdmissao)
            """)
    List<Object[]> countAdmissoesPorAno();

    @Query("""
            select coalesce(f.sexo, 'Nao informado'), count(f)
            from Funcionario f
            group by f.sexo
            order by f.sexo
            """)
    List<Object[]> countFuncionariosPorSexo();
}
