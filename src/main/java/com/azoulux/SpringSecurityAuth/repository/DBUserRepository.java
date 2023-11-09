package com.azoulux.SpringSecurityAuth.repository;

import com.azoulux.SpringSecurityAuth.model.DBUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DBUserRepository extends JpaRepository<DBUser, Integer> {
    public DBUser findByUsername(String username);
}
