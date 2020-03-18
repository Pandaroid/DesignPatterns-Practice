package com.pandaroid.dps.factory.simple.products;

import com.pandaroid.dps.factory.simple.ITrustBid;

public class RealEstateTrustBid extends ATrustBid implements ITrustBid {

    public RealEstateTrustBid(String trustBidMemo) {
        super(trustBidMemo);
    }

    @Override
    public void pubTrustBid() {
        System.out.println("[RealEstateTrustBid: " + trustBidMemo + "]发布信托标的：房地产类信托。" + this.toString());
    }
}
