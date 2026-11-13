# Rendu

**Nom :** Frin  
**Prénom :** Raphael  
**Campus :** Nantes

## Défauts trouvés en partie A

- **Règle C3 :** `crediter` accepte un montant nul au lieu de lever `IllegalArgumentException`.
  La condition vérifie seulement les montants négatifs et oublie le zéro.
  **Test :** `doitRefuserUnCreditNonStrictementPositif` (cas `0`).

- **Règle C5 :** `debiter` refuse un débit qui atteint exactement la limite du découvert.
  La condition rejette aussi l'égalité avec cette limite.
  **Test :** `doitAccepterUnDebitJusquaLaLimiteDuDecouvert` (cas `300.00`).