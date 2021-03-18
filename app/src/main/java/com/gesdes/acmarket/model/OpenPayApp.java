package com.gesdes.acmarket.model;

import mx.openpay.android.Openpay;

public class OpenPayApp  {
    private final Openpay openpay;

    public OpenPayApp(String merchantId,String privateKey,boolean produccion) {

        this.openpay = new Openpay(merchantId, privateKey, produccion);
    }


    public Openpay getOpenpay() {
        return this.openpay;
    }

}
