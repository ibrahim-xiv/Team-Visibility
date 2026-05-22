package com.TeamVisibility.App.service;

import org.springframework.stereotype.Service;

import com.TeamVisibility.App.model.RideRequest;
import com.TeamVisibility.App.repository.RideRequestRepository;

@Service
public class RideRequestService {
    private final RideRequestRepository rideRequestRepository;

    public RideRequestService(RideRequestRepository rideRequestRepository){
        this.rideRequestRepository = rideRequestRepository;
    }
    public RideRequest createRideRequest(RideRequest rideRequest){
        return rideRequestRepository.save(rideRequest);
    }
}
