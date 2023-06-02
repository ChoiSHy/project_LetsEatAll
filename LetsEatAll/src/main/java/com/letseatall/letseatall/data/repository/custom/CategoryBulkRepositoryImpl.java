package com.letseatall.letseatall.data.repository.custom;

import com.letseatall.letseatall.data.Entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryBulkRepositoryImpl implements CategoryBulkRepository{
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Category> categories) {
        String sql = "insert into Category(id, name) " +
                "values (?,?)";
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Category category = categories.get(i);
                        ps.setInt(1, category.getId());
                        ps.setString(2, category.getName());
                    }

                    @Override
                    public int getBatchSize() {
                        return categories.size();
                    }
                }
        );
    }
}