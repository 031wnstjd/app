package com.example.app.repository;

import com.example.app.domain.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PayRepository extends JpaRepository<Pay, Long> {

    @Query("SELECT p FROM Pay p WHERE p.successStatus = true")
    List<Pay> findAllSuccess();
}
