package com.visana.erp.compensation.domain.service;

import com.visana.erp.commerce.domain.model.OrderId;
import com.visana.erp.compensation.domain.model.Commission;
import com.visana.erp.compensation.domain.model.CommissionType;
import com.visana.erp.core.domain.model.Money;
import com.visana.erp.core.domain.model.Percentage;
import com.visana.erp.core.domain.model.TenantId;
import com.visana.erp.network.domain.model.AffiliateId;
import com.visana.erp.network.domain.model.GenealogyNode;
import com.visana.erp.qualification.domain.model.AffiliateQualification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Domain Service puro que implementa el plan de compensación Unilevel (8 niveles).
 *
 * PRINCIPIO DE DISEÑO:
 * Los porcentajes NO están hardcodeados. El `unilevelPlan` es un Map<nivel, Percentage>
 * que se inyecta externamente desde la capa de Aplicación/Infraestructura.
 * Esto resuelve que el negocio pueda cambiar L1=15% a L1=12% sin redeploy.
 *
 * FLUJO (architecture-rules.mcp.md):
 * SALE -> ORDER -> PAYMENT CONFIRMED -> ... -> COMPENSATION ENGINE (este servicio) -> LEDGER
 */
public class UnilevelCompensationCalculatorService {

    private static final int MAX_UNILEVEL_DEPTH = 8;

    /**
     * Calcula las comisiones Unilevel para todos los ancestros calificados en la línea ascendente.
     *
     * @param order                        La orden pagada que origina el pago. DEBE estar en estado PAID.
     * @param upline                       Lista ORDENADA de nodos ascendentes (índice 0 = patrocinador directo, índice 7 = nivel 8).
     * @param qualificationsByAffiliateId  Mapa de AffiliateId -> AffiliateQualification para el periodo en curso.
     * @param unilevelPlan                 Mapa externo de nivel -> Percentage. Ej: {1 -> 0.15, 2 -> 0.10, ...}
     * @return Lista de Commission calculadas para cada ancestro calificado.
     */
    public List<Commission> calculateUnilevelCommissions(
            TenantId tenantId,
            OrderId orderId,
            Money orderTotal,
            List<GenealogyNode> upline,
            Map<AffiliateId, AffiliateQualification> qualificationsByAffiliateId,
            Map<Integer, Percentage> unilevelPlan) {

        Objects.requireNonNull(tenantId, "TenantId cannot be null");
        Objects.requireNonNull(orderId, "OrderId cannot be null");
        Objects.requireNonNull(orderTotal, "OrderTotal cannot be null");
        Objects.requireNonNull(upline, "Upline cannot be null");
        Objects.requireNonNull(qualificationsByAffiliateId, "Qualifications map cannot be null");
        Objects.requireNonNull(unilevelPlan, "Unilevel plan cannot be null");

        List<Commission> commissions = new ArrayList<>();

        for (int i = 0; i < Math.min(upline.size(), MAX_UNILEVEL_DEPTH); i++) {
            int networkLevel = i + 1; // Nivel 1 = patrocinador directo, Nivel 8 = octavo ancestro
            GenealogyNode ancestor = upline.get(i);

            Percentage levelPercentage = unilevelPlan.get(networkLevel);
            if (levelPercentage == null) {
                // Sin configuración para este nivel: se omite (Configuration over Hard-code)
                continue;
            }

            AffiliateQualification qualification = qualificationsByAffiliateId.get(ancestor.getAffiliateId());
            if (qualification == null || !qualification.isQualified()) {
                // El ancestro no está calificado para este período: no recibe comisión (compresión dinámica futura)
                continue;
            }

            Money commissionAmount = orderTotal.multiply(levelPercentage);

            Commission commission = Commission.calculate(
                    tenantId,
                    ancestor.getAffiliateId(),
                    orderId,
                    commissionAmount,
                    CommissionType.UNILEVEL_BONUS,
                    networkLevel
            );

            commissions.add(commission);
        }

        return commissions;
    }
}
