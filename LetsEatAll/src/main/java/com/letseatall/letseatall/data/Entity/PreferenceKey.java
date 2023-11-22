package com.letseatall.letseatall.data.Entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PreferenceKey implements Serializable {
    Long userId;
    int categoryId;
}
