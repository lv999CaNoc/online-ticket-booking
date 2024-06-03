package com.actvn.cinema.repositories;


import com.actvn.cinema.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByValue(final String value);
}