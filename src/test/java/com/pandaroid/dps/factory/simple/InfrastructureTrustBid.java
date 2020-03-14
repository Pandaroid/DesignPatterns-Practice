package com.pandaroid.dps.factory.simple;

/**
 * 基础设施类信托
 * 是指信托公司以受托人的身份，通过信托形式，接受委托人的信托资金，以自己的名义，将信托资金运用于交通、通讯、能源、市政、环境保护等基础设施项目，为受益人利益或者特定目的进行管理或者处分的经营行为。（基础设施类信托的钱最后投向国家的基础设施建设（修路、架桥、建公园））
 */
public class InfrastructureTrustBid implements ITrustBid {
    @Override
    public void pubTrustBid() {
        System.out.println("[InfrastructureTrustBid]发布信托标的：基础设施类信托");
    }
}
