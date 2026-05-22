package com.TeamVisibility.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.TeamVisibility.App.model.RideRequest;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long>{
    
}
