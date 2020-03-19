package com.pandaroid.dps.factory.factories.abstractf.infrastructure;

import com.pandaroid.dps.factory.factories.abstractf.ITrustBidAbstractFactory;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.ICurrentTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.IExperienceTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.ITransferTrustBid;
import com.pandaroid.dps.factory.products.ITrustBid;
import com.pandaroid.dps.factory.products.bids.InfrastructureTrustBid;

public class InfrastructureTrustAbstractFactory implements ITrustBidAbstractFactory {
    @Override
    public ITrustBid createTrustBid() {
        return new InfrastructureTrustBid("InfrastructureTrustAbstractFactory.createTrustBid");
    }

    @Override
    public ICurrentTrustBid createCurrentTrustBid() {
        return new InfrastructureCurrentTrustBid("");
    }

    @Override
    public ITransferTrustBid createTransferTrustBid() {
        return null;
    }

    @Override
    public IExperienceTrustBid createExperienceTrustBid() {
        return null;
    }
}
