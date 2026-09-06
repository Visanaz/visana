package com.visana.erp.qualification.domain.volume;

import com.visana.erp.core.domain.model.Money;
import java.util.Objects;

public record SaleMonetaryComponents(
        Money gross,
        Money discount,
        Money netBeforeTax,
        Money tax,
        Money shipping,
        Money paidAmount,
        Money refundOrCancellationAmount) {

    public SaleMonetaryComponents {
        requireNonNegative(gross, "gross");
        requireNonNegative(discount, "discount");
        requireNonNegative(netBeforeTax, "netBeforeTax");
        requireNonNegative(tax, "tax");
        requireNonNegative(shipping, "shipping");
        requireNonNegative(paidAmount, "paidAmount");
        requireNonNegative(refundOrCancellationAmount, "refundOrCancellationAmount");
    }

    private static void requireNonNegative(Money value, String name) {
        Objects.requireNonNull(value, name + " cannot be null");
        if (value.isReversal()) {
            throw new IllegalArgumentException(name + " must be a non-negative component");
        }
    }
}
