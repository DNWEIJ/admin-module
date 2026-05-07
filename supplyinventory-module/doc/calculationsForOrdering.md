INVENTORY REORDER ALGORITHM (Sales-driven, SupplyLocal model)

────────────────────────────────────────────
1. CURRENT STOCK
   ────────────────────────────────────────────
   currentStock =
   (quantityOfPackages × quantityPerPackage)
    + individualLeftInOpenPackage


────────────────────────────────────────────
2. DEMAND (FROM SALES)
   ────────────────────────────────────────────
   avgWeeklyConsumption =
   totalSold(last N weeks) / N

avgDailyConsumption =
avgWeeklyConsumption / 7

Fallback (no sales history):
avgDailyConsumption =
minQuantityForAlert / 14


────────────────────────────────────────────
3. LEAD TIME
   ────────────────────────────────────────────
   leadTimeDays = 7 (default or supplier-based)


────────────────────────────────────────────
4. SAFETY STOCK
   ────────────────────────────────────────────
   safetyStock =
   avgDailyConsumption × leadTimeDays × 0.5


────────────────────────────────────────────
5. REORDER POINT (R)
   ────────────────────────────────────────────
   reorderPoint =
   (avgDailyConsumption × leadTimeDays) + safetyStock

Simplified:
R = 1.5 × avgDailyConsumption × leadTimeDays


────────────────────────────────────────────
6. REORDER RULES
   ────────────────────────────────────────────
   🔴 REORDER NOW
   currentStock <= reorderPoint


🟠 WILL NEED REORDER SOON
currentStock <= reorderPoint × 1.5
AND currentStock > reorderPoint


────────────────────────────────────────────
7. ORDER QUANTITY (Q)
   ────────────────────────────────────────────
   targetCoverageDays = leadTimeDays + 14

targetStock =
avgDailyConsumption × targetCoverageDays

orderQty =
targetStock - currentStock


Constraints:
orderQty = max(orderQty, buyQuantityPerOrder)

orderQty must be rounded up to full packages:

ceilToPackage(x) =
ceil(x / quantityPerPackage) × quantityPerPackage

FINAL:
orderQty = ceilToPackage(orderQty)


────────────────────────────────────────────
8. DASHBOARD CLASSIFICATION
   ────────────────────────────────────────────
   🔴 Needs reorder now:
   currentStock <= R


🟠 Will need reorder soon:
R < currentStock <= 1.5R


📉 High consumption:
top 20% avgWeeklyConsumption


────────────────────────────────────────────
CORE PRINCIPLE
────────────────────────────────────────────
Inventory is derived from sales.
Reorder logic = demand rate × lead time + safety buffer.