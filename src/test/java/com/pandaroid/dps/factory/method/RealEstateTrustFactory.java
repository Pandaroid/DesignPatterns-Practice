package com.pandaroid.dps.factory.method;

import com.pandaroid.dps.factory.ITrustBid;
import com.pandaroid.dps.factory.products.RealEstateTrustBid;

public class RealEstateTrustFactory extends ATrustBidFactory implements ITrustBidMethodFactory {
    @Override
    public ITrustBid createTrustBid() {
        return new RealEstateTrustBid("RealEstateTrustFactory.createTrustBid");
    }
}

