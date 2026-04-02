package com.farmconnect.farmconnectbackend.strategy;

import com.farmconnect.farmconnectbackend.model.Negotiation;
import com.farmconnect.farmconnectbackend.model.NegotiationRequest;

import com.farmconnect.farmconnectbackend.model.NegotiationResponse;

public interface NegotiationStrategy {

    NegotiationResponse startNegotiation(NegotiationRequest request);

    NegotiationResponse processCounterOffer(Negotiation negotiation, NegotiationRequest request);
}
