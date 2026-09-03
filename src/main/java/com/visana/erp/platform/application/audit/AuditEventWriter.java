package com.visana.erp.platform.application.audit;

import com.visana.erp.platform.domain.audit.AuditEvent;

public interface AuditEventWriter {
    void append(AuditEvent event);
}
