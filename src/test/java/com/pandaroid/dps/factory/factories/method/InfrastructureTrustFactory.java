package com.pandaroid.dps.factory.factories.method;

import com.pandaroid.dps.factory.products.ITrustBid;
import com.pandaroid.dps.factory.products.bids.InfrastructureTrustBid;

public class InfrastructureTrustFactory extends ATrustBidFactory implements ITrustBidMethodFactory {
    @Override
    public ITrustBid createTrustBid() {
        return new InfrastructureTrustBid("InfrastructureTrustFactory.createTrustBid");
    }
}
