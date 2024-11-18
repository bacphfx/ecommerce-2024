package com.ecommerce.checkout;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class CheckoutInfo {
    private float productCost;
    private float productTotal;
    private float shippingCostTotal;
    private float paymentTotal;
    private int deliverDays;
    private Date deliverDate;
    private boolean codSupported;

    public float getProductCost() {
        return productCost;
    }

    public void setProductCost(float productCost) {
        this.productCost = productCost;
    }

    public float getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(float productTotal) {
        this.productTotal = productTotal;
    }

    public float getShippingCostTotal() {
        return shippingCostTotal;
    }

    public void setShippingCostTotal(float shippingCostTotal) {
        this.shippingCostTotal = shippingCostTotal;
    }

    public float getPaymentTotal() {
        return paymentTotal;
    }

    public void setPaymentTotal(float paymentTotal) {
        this.paymentTotal = paymentTotal;
    }

    public int getDeliverDays() {
        return deliverDays;
    }

    public void setDeliverDays(int deliverDays) {
        this.deliverDays = deliverDays;
    }

    public Date getDeliverDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliverDays);
        return calendar.getTime();
    }

    public boolean isCodSupported() {
        return codSupported;
    }

    public void setCodSupported(boolean codSupported) {
        this.codSupported = codSupported;
    }

    public String getPaymentTotal4PayPal(){
        DecimalFormat formatter = new DecimalFormat("###.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(paymentTotal);
    }
}
