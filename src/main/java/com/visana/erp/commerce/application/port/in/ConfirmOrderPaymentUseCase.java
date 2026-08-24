package com.visana.erp.commerce.application.port.in;

import com.visana.erp.commerce.application.dto.ConfirmOrderCommand;

public interface ConfirmOrderPaymentUseCase {
    void execute(ConfirmOrderCommand command);
}
