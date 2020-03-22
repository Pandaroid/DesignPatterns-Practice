package com.pandaroid.dps.factory.factories.abstractf.infrastructure;

import com.pandaroid.dps.factory.factories.abstractf.ITrustBidAbstractFactory;
import com.pandaroid.dps.factory.factories.abstractf.infrastructure.bids.InfrastructureCurrentTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.infrastructure.bids.InfrastructureExperienceTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.infrastructure.bids.InfrastructureTransferTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.ICurrentTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.IExperienceTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.ITransferTrustBid;
import com.pandaroid.dps.factory.products.ITrustBid;
import com.pandaroid.dps.factory.products.bids.InfrastructureTrustBid;

public class InfrastructureTrustBidAbstractFactory implements ITrustBidAbstractFactory {
    @Override
    public ITrustBid createTrustBid() {
        return new InfrastructureTrustBid("InfrastructureTrustAbstractFactory.createTrustBid");
    }

    @Override
    public ICurrentTrustBid createCurrentTrustBid() {
        return new InfrastructureCurrentTrustBid("InfrastructureTrustAbstractFactory.createCurrentTrustBid");
    }

    @Override
    public ITransferTrustBid createTransferTrustBid() {
        return new InfrastructureTransferTrustBid("InfrastructureTrustAbstractFactory.createTransferTrustBid");
    }

    @Override
    public IExperienceTrustBid createExperienceTrustBid() {
        return new InfrastructureExperienceTrustBid("InfrastructureTrustAbstractFactory.createExperienceTrustBid");
    }
}
