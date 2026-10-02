package com.harrydev.ticket_management_backend.repository;

import com.harrydev.ticket_management_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username); // JPA tự động generate query => Kiểm tra sự tồn tại của field username
                                               // trong DB với username ta truyền vào - không cần viết code nào hết
    Optional<User> findByUsername(String username);
}
