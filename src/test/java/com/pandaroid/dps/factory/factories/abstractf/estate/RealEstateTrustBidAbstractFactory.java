package com.pandaroid.dps.factory.factories.abstractf.estate;

import com.pandaroid.dps.factory.factories.abstractf.ITrustBidAbstractFactory;
import com.pandaroid.dps.factory.factories.abstractf.estate.bids.RealEstateCurrentTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.estate.bids.RealEstateExperienceTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.estate.bids.RealEstateTransferTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.ICurrentTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.IExperienceTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.ITransferTrustBid;
import com.pandaroid.dps.factory.products.ITrustBid;
import com.pandaroid.dps.factory.products.bids.RealEstateTrustBid;

public class RealEstateTrustBidAbstractFactory implements ITrustBidAbstractFactory {
    @Override
    public ITrustBid createTrustBid() {
        return new RealEstateTrustBid("RealEstateTrustBidAbstractFactory.createTrustBid");
    }

    @Override
    public ICurrentTrustBid createCurrentTrustBid() {
        return new RealEstateCurrentTrustBid("RealEstateTrustBidAbstractFactory.createCurrentTrustBid");
    }

    @Override
    public ITransferTrustBid createTransferTrustBid() {
        return new RealEstateTransferTrustBid("RealEstateTrustBidAbstractFactory.createTransferTrustBid");
    }

    @Override
    public IExperienceTrustBid createExperienceTrustBid() {
        return new RealEstateExperienceTrustBid("RealEstateTrustBidAbstractFactory.createExperienceTrustBid");
    }
}
