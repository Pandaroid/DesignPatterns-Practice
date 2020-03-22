package com.pandaroid.dps.factory.factories.abstractf.estate.bids;

import com.pandaroid.dps.factory.factories.abstractf.iproducts.ICurrentTrustBid;
import com.pandaroid.dps.factory.products.ATrustBid;

public class RealEstateCurrentTrustBid extends ATrustBid implements ICurrentTrustBid {
    public RealEstateCurrentTrustBid(String memo) {
        super(memo);
    }

    @Override
    public void pubTrustBid() {
        System.out.println("[RealEstateCurrentTrustBid: " + trustBidMemo + "]发布信托标的：房地产类信托。类型：活期标。" + this.toString());
    }
}
