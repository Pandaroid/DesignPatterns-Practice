package com.pandaroid.dps.factory.factories.abstractf.estate.bids;

import com.pandaroid.dps.factory.factories.abstractf.iproducts.ITransferTrustBid;
import com.pandaroid.dps.factory.products.ATrustBid;

public class RealEstateTransferTrustBid extends ATrustBid implements ITransferTrustBid {
    public RealEstateTransferTrustBid(String memo) {
        super(memo);
    }

    @Override
    public void pubTrustBid() {
        System.out.println("[RealEstateTransferTrustBid: " + trustBidMemo + "]发布信托标的：房地产类信托。类型：可转让标。" + this.toString());
    }
}
