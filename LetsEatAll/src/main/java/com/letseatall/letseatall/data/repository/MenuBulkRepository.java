package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Category;
import com.letseatall.letseatall.data.Entity.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MenuBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Menu> menuList) {
        String sql = "insert into Menu(id, name, price, category_id, score, restaurant_id, franchise_id) " +
                "values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Menu menu = menuList.get(i);
                        ps.setLong(1, menu.getId());
                        ps.setString(2, menu.getName());
                        ps.setInt(3,menu.getPrice());
                        if (menu.getCategory()!=null)
                            ps.setInt(4, menu.getCategory().getId());
                        else
                            ps.setNull(4,Types.INTEGER);
                        ps.setInt(5, menu.getScore());
                        if(menu.getRestaurant()!=null)
                            ps.setLong(6,menu.getRestaurant().getId());
                        else
                            ps.setNull(6,Types.BIGINT);
                        if(menu.getFranchise()!=null)
                            ps.setLong(7,menu.getFranchise().getId());
                        else
                            ps.setNull(7, Types.BIGINT);
                    }

                    @Override
                    public int getBatchSize() {
                        return menuList.size();
                    }
                }
        );
    }
}
