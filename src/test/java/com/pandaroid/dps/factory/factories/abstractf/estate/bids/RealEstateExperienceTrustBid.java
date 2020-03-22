package com.pandaroid.dps.factory.factories.abstractf.estate.bids;

import com.pandaroid.dps.factory.factories.abstractf.iproducts.IExperienceTrustBid;
import com.pandaroid.dps.factory.products.ATrustBid;

public class RealEstateExperienceTrustBid extends ATrustBid implements IExperienceTrustBid {
    public RealEstateExperienceTrustBid(String memo) {
        super(memo);
    }

    @Override
    public void pubTrustBid() {
        System.out.println("[RealEstateExperienceTrustBid: " + trustBidMemo + "]发布信托标的：房地产类信托。类型：体验标。" + this.toString());
    }
}
