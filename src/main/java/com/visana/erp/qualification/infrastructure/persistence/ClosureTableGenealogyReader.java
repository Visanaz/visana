package com.visana.erp.qualification.infrastructure.persistence;

import com.visana.erp.qualification.application.port.out.GenealogyClosureReader;
import com.visana.erp.qualification.domain.network.GenealogyMemberDepth;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ClosureTableGenealogyReader implements GenealogyClosureReader {
    private final JdbcTemplate jdbcTemplate;

    public ClosureTableGenealogyReader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenealogyMemberDepth> memberAndDescendants(UUID memberId) {
        return jdbcTemplate.query(
                """
                select descendant_member_id, depth
                from genealogy_closure
                where ancestor_member_id = ?
                order by depth, descendant_member_id
                """,
                (resultSet, row) -> new GenealogyMemberDepth(
                        UUID.fromString(resultSet.getString("descendant_member_id")),
                        resultSet.getInt("depth")),
                memberId);
    }
}
