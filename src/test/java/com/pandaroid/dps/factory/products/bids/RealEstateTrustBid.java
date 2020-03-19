package com.pandaroid.dps.factory.products.bids;

import com.pandaroid.dps.factory.products.ATrustBid;
import com.pandaroid.dps.factory.products.ITrustBid;

public class RealEstateTrustBid extends ATrustBid implements ITrustBid {

    public RealEstateTrustBid(String trustBidMemo) {
        super(trustBidMemo);
    }

    @Override
    public void pubTrustBid() {
        System.out.println("[RealEstateTrustBid: " + trustBidMemo + "]发布信托标的：房地产类信托。" + this.toString());
    }
}
