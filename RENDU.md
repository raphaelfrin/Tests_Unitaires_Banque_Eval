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

  - **Règle C6 :** `estVide` renvoie `false` pour un solde de `0.00` ou `0.000`.
  La comparaison tient compte du nombre de décimales au lieu de comparer seulement la valeur.
  **Test :** `doitIndiquerSiLeSoldeEstNul`.

  - **Règle F3 :** les frais de tenue sont offerts à 26 ans alors qu'ils devraient être facturés.
  La condition inclut 26 ans dans la gratuité au lieu de s'arrêter avant.
  **Test :** `doitAppliquerLaGratuiteSelonLAge` (cas `26`).