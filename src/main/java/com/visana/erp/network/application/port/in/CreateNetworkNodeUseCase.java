package com.visana.erp.network.application.port.in;

import java.util.UUID;

public interface CreateNetworkNodeUseCase {
    UUID execute(CreateNetworkNodeCommand command);
}
