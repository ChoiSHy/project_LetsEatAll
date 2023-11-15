package com.letseatall.letseatall.data.repository.custom;

import com.letseatall.letseatall.data.Entity.menu.Menu;
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
public class MenuBulkRepositoryImpl implements MenuBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Menu> menus) {
        String sql = "insert into Menu(id, name, price, category_id, score, restaurant_id, franchise_id)"+
                "values(?,?,?,?,?,?,?)";

        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Menu menu = menus.get(i);
                        ps.setLong(1, menu.getId());
                        ps.setString(2, menu.getName());
                        ps.setInt(3,menu.getPrice());
                        ps.setObject(4, menu.getCategory());
                        ps.setDouble(5,menu.getScore());
                        ps.setObject(6,menu.getRestaurant());
                        ps.setObject(7, menu.getFranchise());
                    }

                    @Override
                    public int getBatchSize() {
                        return menus.size();
                    }
                });
    }
}
