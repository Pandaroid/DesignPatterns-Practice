package com.pandaroid.dps.factory.factories.abstractf.infrastructure.bids;

import com.pandaroid.dps.factory.factories.abstractf.iproducts.IExperienceTrustBid;
import com.pandaroid.dps.factory.factories.abstractf.iproducts.ITransferTrustBid;
import com.pandaroid.dps.factory.products.ATrustBid;

public class InfrastructureTransferTrustBid extends ATrustBid implements ITransferTrustBid {
    public InfrastructureTransferTrustBid(String memo) {
        super(memo);
    }

    @Override
    public void pubTrustBid() {
        System.out.println("[InfrastructureExperienceTrustBid: " + trustBidMemo + "]发布信托标的：基础设施类信托。类型：可转让标。" + this.toString());
    }
}
