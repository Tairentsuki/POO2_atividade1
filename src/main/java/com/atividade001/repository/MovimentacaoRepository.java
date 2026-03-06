package com.atividade001.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.atividade001.model.Movimentacao;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {
    @EntityGraph(attributePaths = { "setor", "funcionario" })
    List<Movimentacao> findAllByOrderByDataDescIdDesc();

    @Query("""
            select coalesce(s.nome, 'Sem setor'),
                   coalesce(sum(case when m.tipo = com.atividade001.model.TipoMovimentacao.RECEITA then m.valor else 0 end), 0),
                   coalesce(sum(case when m.tipo = com.atividade001.model.TipoMovimentacao.DESPESA then m.valor else 0 end), 0)
            from Movimentacao m
            left join m.setor s
            group by s.nome
            order by s.nome
            """)
    List<Object[]> sumMovimentacoesPorSetor();
}
