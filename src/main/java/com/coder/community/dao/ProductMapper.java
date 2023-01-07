package com.coder.community.dao;

import com.coder.community.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    int insertProduct(Product product);
    List<Product> selectAllProduct();
}
