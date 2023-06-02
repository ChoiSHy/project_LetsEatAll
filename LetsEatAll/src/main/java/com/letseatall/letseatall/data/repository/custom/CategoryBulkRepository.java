package com.letseatall.letseatall.data.repository.custom;

import com.letseatall.letseatall.data.Entity.Category;

import java.util.List;

public interface CategoryBulkRepository {
    void saveAll(List<Category> categories);
}
