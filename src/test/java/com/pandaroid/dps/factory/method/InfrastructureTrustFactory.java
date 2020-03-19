package com.pandaroid.dps.factory.method;

import com.pandaroid.dps.factory.ITrustBid;
import com.pandaroid.dps.factory.products.InfrastructureTrustBid;

public class InfrastructureTrustFactory extends ATrustBidFactory implements ITrustBidMethodFactory {
    @Override
    public ITrustBid createTrustBid() {
        return new InfrastructureTrustBid("InfrastructureTrustFactory.createTrustBid");
    }
}
