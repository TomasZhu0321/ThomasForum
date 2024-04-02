package com.thomasForum.dao;

import org.springframework.stereotype.Repository;

@Repository
public class AlphaDaoHibernateImp implements AlphaDao{
    @Override
    public void select() {
        System.out.println("Hibernate");
    }
}
