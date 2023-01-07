package com.coder.community.service;

import com.coder.community.dao.ProductMapper;
import com.coder.community.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    ProductMapper productMapper;
    public int insertProduct(Product product){
        if(product == null){
            throw new NullPointerException("参数为空！");
        }
        return productMapper.insertProduct(product);
    }
}
