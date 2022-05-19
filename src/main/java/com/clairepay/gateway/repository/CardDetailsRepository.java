package com.clairepay.gateway.repository;

import com.clairepay.gateway.models.CardDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardDetailsRepository extends JpaRepository<CardDetails, String> {
}
