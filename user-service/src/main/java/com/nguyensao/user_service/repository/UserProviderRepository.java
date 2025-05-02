package com.nguyensao.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.user_service.model.UserProvider;

@Repository
public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {
    boolean existsByProviderAndProviderId(String provider, String providerId);

}
