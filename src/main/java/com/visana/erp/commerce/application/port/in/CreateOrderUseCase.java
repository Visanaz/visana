package com.visana.erp.commerce.application.port.in;

import com.visana.erp.commerce.application.dto.CreateOrderCommand;
import java.util.UUID;

public interface CreateOrderUseCase {
    UUID execute(CreateOrderCommand command);
}
