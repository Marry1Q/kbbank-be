package com.kopo_team4.kbbank_backend.domain.user.repository;

import com.kopo_team4.kbbank_backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByUserCi(String userCi);
    
    Optional<User> findByUserId(String userId);
    
    Optional<User> findByUserNum(String userNum);
    
    boolean existsByUserCi(String userCi);
    
    void deleteByUserCi(String userCi);
} 