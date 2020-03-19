package com.pandaroid.dps.factory.factories.method;

import com.pandaroid.dps.factory.products.ITrustBid;
import com.pandaroid.dps.factory.products.bids.RealEstateTrustBid;

public class RealEstateTrustFactory extends ATrustBidFactory implements ITrustBidMethodFactory {
    @Override
    public ITrustBid createTrustBid() {
        return new RealEstateTrustBid("RealEstateTrustFactory.createTrustBid");
    }
}

