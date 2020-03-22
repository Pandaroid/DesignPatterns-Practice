package com.pandaroid.dps.factory.factories.abstractf.infrastructure.bids;

import com.pandaroid.dps.factory.factories.abstractf.iproducts.ICurrentTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.IExperienceTrustBid;
import com.pandaroid.dps.factory.products.ATrustBid;

public class InfrastructureExperienceTrustBid extends ATrustBid implements IExperienceTrustBid {
    public InfrastructureExperienceTrustBid(String memo) {
        super(memo);
    }

    @Override
    public void pubTrustBid() {
        System.out.println("[InfrastructureExperienceTrustBid: " + trustBidMemo + "]发布信托标的：基础设施类信托。类型：体验标。" + this.toString());
    }
}
