package com.pandaroid.dps.factory.factories.abstractf.infrastructure;

import com.pandaroid.dps.factory.factories.abstractf.iproducts.ICurrentTrustBid;
import com.pandaroid.dps.factory.products.ATrustBid;

public class InfrastructureCurrentTrustBid extends ATrustBid implements ICurrentTrustBid {
    public InfrastructureCurrentTrustBid(String memo) {
        super(memo);
    }

    @Override
    public void pubTrustBid() {
        System.out.println("[InfrastructureCurrentTrustBid: " + trustBidMemo + "]发布信托标的：基础设施类信托。类型：活期标。" + this.toString());
    }
}
