package com.pandaroid.dps.factory.method;

import com.pandaroid.dps.factory.ITrustBid;
import com.pandaroid.dps.factory.products.RealEstateTrustBid;

public class RealEstateTrustFactory implements ITrustBidFactory {
    @Override
    public ITrustBid createTrustBid() {
        return new RealEstateTrustBid("RealEstateTrustFactory.createTrustBid");
    }
}
