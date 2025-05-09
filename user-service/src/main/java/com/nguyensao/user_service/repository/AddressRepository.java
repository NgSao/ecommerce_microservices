package com.nguyensao.user_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.user_service.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    List<Address> findAllByUserId(String userId);

}
