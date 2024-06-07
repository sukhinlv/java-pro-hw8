package com.example.dao.repository;

import com.example.dao.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("select p from Product p where p.userId = :userId")
    List<Product> findBUserId(@Param("userId") long userId);
}
