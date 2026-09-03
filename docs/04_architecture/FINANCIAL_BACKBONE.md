# Financial Backbone propuesto

```text
Order -> PaymentIntent -> PaymentConfirmed
      -> CompensationCalculation(planVersion, rule, base, beneficiary)
      -> CommissionResult -> LedgerEntry -> Approval -> Settlement
      -> PayoutOrder -> TaxCalculation -> Payout -> Reconciliation
      -> reversal/void/refund as compensating events
```

| Etapa | Ownership | Invariantes propuestos |
|---|---|---|
| Payment | Commerce/payments | provider reference, firma, idempotency y estado verificable |
| Calculation | Rewards | plan/rule/base/porcentaje/beneficiario versionados y deterministas |
| Ledger | Finance | append-oriented; ningún borrado silencioso |
| Approval/settlement | Finance | actor, fecha, periodo de ganancia/cálculo/aprobación separados |
| Payout/tax | Finance | orden, neto, retención, resultado y conciliación trazables |
| Reversal | Rewards/Finance | evento compensatorio que relaciona el origen y efectos derivados |

No se proponen nombres definitivos de estados ni se resuelven DG-01, 02, 03, 04, 05, 06, 07, 08, 09, 10, 11, 12 o 13. `Commission.reverse()` actual es una base semántica parcial, no un rollback completo.
