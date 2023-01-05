package com.coder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("AlphaMybatis")
@Primary
public class AlphaDaoMybatisIMPL implements  AlphaDao{
    @Override
    public String select() {
        return "Mybatis";
    }
}
