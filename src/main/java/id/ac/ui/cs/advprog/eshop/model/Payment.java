package id.ac.ui.cs.advprog.eshop.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;

@Builder
@Getter
public class Payment {
    @Setter
    String id;
    String method;
    String status;
    Map<String, String> paymentData;
    Order order;

    public Payment(String id, String method, Map<String, String> paymentData, Order order) {
        this.id = id;
        this.order = order;
        this.method = method;
        this.paymentData = paymentData;

        if (paymentData.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (order == null) {
            throw new IllegalArgumentException();
        }

        boolean valid = false;
        switch (method) {
            case "VOUCHER_CODE":
                String voucherCode = paymentData.get("voucherCode");

                if (voucherCode != null
                        && voucherCode.length() == 16
                        && voucherCode.startsWith("ESHOP")) {
                    int numCharCount = 0;
                    for (int i = 0; i < voucherCode.length(); i++) {
                        if (Character.isDigit(voucherCode.charAt(i))) {
                            numCharCount++;
                        }
                    }
                    if (numCharCount == 8) {
                        valid = true;
                    }

                }

                break;
            case "CASH_ON_DELIVERY":
                String address = paymentData.get("address");
                String deliveryFee = paymentData.get("deliveryFee");

                if (address != null && !address.isEmpty()
                        && deliveryFee != null && !deliveryFee.isEmpty()) {
                    valid = true;
                }

                break;
            default:
                throw new IllegalArgumentException();
        }

        if (valid) {
            setStatus("SUCCESS");
        } else {
            setStatus("REJECTED");
        }
    }

    public Payment(String id, String method, String status, Map<String, String> paymentData, Order order) {
        this.id = id;
        this.order = order;
        this.method = method;
        this.paymentData = paymentData;
        setStatus(status);
    }

    public void setStatus(String status) {
        if (PaymentStatus.contains(status)) {
            this.status = status;
            if (status.equals(PaymentStatus.SUCCESS.getValue())) {
                order.setStatus("SUCCESS");
            } else if (status.equals(PaymentStatus.REJECTED.getValue())) {
                order.setStatus("FAILED");
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}