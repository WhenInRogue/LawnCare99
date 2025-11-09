package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.models.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
                                        //JpaRepository provides built-in CRUD operations and pagination
public interface SupplyRepository extends JpaRepository<Supply, Long> {
    List<Supply> findByNameContainingOrDescriptionContaining(String name, String description);

    //Find all products where the name contains the given name parameter OR the description contains the given description parameter.

    //Spring automatically generates the SQL query for me
//SELECT * FROM supplies
//WHERE name LIKE '%<name>%' OR description LIKE '%<description>%';
}

//JpaRepository inherits many methods such as:
//findAll()
//findById(Long id)
//save(Supply supply)
//deleteById(Long id), etc.