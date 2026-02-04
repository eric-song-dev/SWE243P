# Exercises 1-8 from Chapter 6, Section 2

## Exercise 1
Write a SELECT statement that returns one row for each vendor in the Invoices table that contains these columns:
* The `vendor_id` column from the `Invoices` table
* The sum of the `invoice_total` columns in the `Invoices` table for that vendor

```sql
SELECT vendor_id,
       SUM(invoice_total) AS total_invoice_amount
FROM invoices
GROUP BY vendor_id;
```

Use the aggregate function `SUM` to calculate the total invoice amount and `GROUP BY` to categorize the results by `vendor_id`.

> The `GROUP BY` statement groups rows that have the same values into **summary rows**. It essentially "collapses" multiple raw records into single buckets.

* **The Logic:** It takes many rows and reduces them into one row per unique value in the specified column.
* **Aggregation:** It is rarely used alone; it is typically paired with **aggregate functions** like `COUNT()`, `SUM()`, `AVG()`, `MAX()`, or `MIN()`.
* **Execution Order:** It happens **after** the `WHERE` clause filters individual rows, but **before** the `HAVING` clause filters the groups.

**How it works conceptually:**

1. **Group:** Identify all rows where meet the condition.
2. **Aggregate:** Calculate the total (e.g., `SUM(amount)`) for just those rows.
3. **Output:** Return a single row.

## Exercise 2
Write a SELECT statement that returns one row for each vendor that contains these columns:
* The `vendor_name` column from the `Vendors` table
* The sum of the `payment_total` columns in the `Invoices` table for that vendor

Sort the result set in descending sequence by the payment total sum for each vendor.

```sql
SELECT v.vendor_name,
       SUM(i.payment_total) AS total_payment
FROM vendors v
JOIN invoices i ON v.vendor_id = i.vendor_id
GROUP BY v.vendor_name
ORDER BY total_payment DESC;
```

Join the `vendors` and `invoices` tables. Group by `vendor_name` to aggregate payments per vendor, and sort the result by the calculated total in descending order.

## Exercise 3
Write a SELECT statement that returns one row for each vendor that contains three columns:
* The `vendor_name` column from the `Vendors` table
* The count of the invoices in the `Invoices` table for each vendor
* The sum of the `invoice_total` columns in the `Invoices` table for each vendor

Sort the result set so the vendor with the most invoices appears first.

```sql
SELECT v.vendor_name,
       COUNT(*) AS invoice_count,
       SUM(i.invoice_total) AS total_invoice_amount
FROM vendors v
JOIN invoices i ON v.vendor_id = i.vendor_id
GROUP BY v.vendor_name
ORDER BY invoice_count DESC;
```

Use `COUNT(*)` to count the number of invoices and `SUM()` for the total amount. Group by `vendor_name` and order by the count of invoices.

## Exercise 4
Write a SELECT statement that returns one row for each general ledger account number that contains three columns:
* The `account_description` column from the `General_Ledger_Accounts` table
* The count of the items in the `Invoice_Line_Items` table that have the same `account_number`
* The sum of the `line_item_amount` columns in the `Invoice_Line_Items` table for that account

Return only rows where the count of line items is greater than 1. Group the result set by account description and sort it by the sum of line item amounts in descending sequence.

```sql
SELECT gla.account_description,
       COUNT(*) AS item_count,
       SUM(ili.line_item_amount) AS total_amount
FROM general_ledger_accounts gla
JOIN invoice_line_items ili ON gla.account_number = ili.account_number
GROUP BY gla.account_description
HAVING item_count > 1
ORDER BY total_amount DESC;
```

Join `General_Ledger_Accounts` with `Invoice_Line_Items`. Use `HAVING` to filter groups where the count is greater than 1, as `WHERE` cannot be used with aggregate functions on the group itself.

> The `HAVING` clause is used to **filter groups** based on a specific condition. It was designed because the standard `WHERE` keyword cannot be used with aggregate functions (like `SUM` or `COUNT`).

* **The Filter Target:** While `WHERE` filters individual **rows** (before grouping), `HAVING` filters entire **groups** (after grouping).
* **Timing:** It executes **after** the `GROUP BY` operation and **after** the calculations are complete.

**How it works conceptually:**

1. **Group & Calculate:** The database first processes the `GROUP BY` instructions, organizing rows into buckets and performing the necessary mathematical calculations (aggregation) for each bucket.
2. **Evaluate Result:** The `HAVING` clause then inspects the **calculated result** of each bucket.
3. **Post-Filter:** If the calculated value satisfies the condition, the bucket is kept; otherwise, the entire group is discarded from the final output.

| Feature | `WHERE` | `HAVING` |
| --- | --- | --- |
| **Filters...** | Individual Rows | Aggregated Groups |
| **Executes...** | **Before** Grouping | **After** Grouping |
| **Can use Aggregates?** | No (Cannot use `SUM()`) | Yes (Can use `SUM()`) |

## Exercise 5
Modify the solution to exercise 4 to return only invoices dated in the second quarter of 2018 (April 1, 2018 to June 30, 2018).

```sql
SELECT gla.account_description,
       COUNT(*) AS item_count,
       SUM(ili.line_item_amount) AS total_amount
FROM general_ledger_accounts gla
JOIN invoice_line_items ili ON gla.account_number = ili.account_number
JOIN invoices i ON ili.invoice_id = i.invoice_id
WHERE i.invoice_date BETWEEN '2018-04-01' AND '2018-06-30'
GROUP BY gla.account_description
HAVING item_count > 1
ORDER BY total_amount DESC;
```

Add a join to the `invoices` table to access `invoice_date`. Use a `WHERE` clause to filter for the date range before grouping.

## Exercise 6
Write a SELECT statement that answers this question: What is the total amount invoiced for each general ledger account number?

The result set should contain these columns:
* The `account_number` column from the `Invoice_Line_Items` table
* The sum of the `line_item_amount` columns from the `Invoice_Line_Items` table

Use the `WITH ROLLUP` operator to include a row that gives the grand total.

```sql
SELECT account_number,
       SUM(line_item_amount) AS total_amount
FROM invoice_line_items
GROUP BY account_number WITH ROLLUP;
```

Use `WITH ROLLUP` after the `GROUP BY` clause to automatically calculate a summary row (grand total) for the grouped set.

> The `WITH ROLLUP` modifier is an extension of the `GROUP BY` clause. It allows you to generate **subtotals** and a **grand total** in a single query, alongside your standard grouped results.

* **The Logic:** It performs hierarchical aggregation. After calculating the standard groups, it progressively "rolls up" the data by removing the right-most grouping column and re-calculating the aggregate.
* **Dimensional Reduction:** It moves from specific details to broader summaries.
  * *Group by (A, B)* -> Calculates totals for A & B.
  * *Roll up* -> Calculates total for A (ignoring B).
  * *Roll up* -> Calculates Grand Total (ignoring A and B).

* **The "Total" Marker:** It uses `NULL` to represent the "super-aggregate" (Total) rows. Here, `NULL` conceptually means "All Values combined."

**How it works conceptually (Grouping by Country, then City):**


If you `GROUP BY Country, City WITH ROLLUP`:

1. **Level 1 (Detailed):** Shows revenue for **USA / New York**.
2. **Level 2 (Subtotal):** Shows revenue for **USA / NULL** (This represents the total for *all* US cities).
3. **Level 3 (Grand Total):** Shows revenue for **NULL / NULL** (This represents the total for the entire table).

## Exercise 7
Write a SELECT statement that answers this question: Which vendors are being paid from more than one account?

The result set should return these columns:
* The `vendor_name` column from the `Vendors` table
* The count of distinct general ledger accounts that apply to the vendor's invoices

```sql
SELECT v.vendor_name,
       COUNT(DISTINCT ili.account_number) AS distinct_account_count
FROM vendors v
JOIN invoices i ON v.vendor_id = i.vendor_id
JOIN invoice_line_items ili ON i.invoice_id = ili.invoice_id
GROUP BY v.vendor_name
HAVING distinct_account_count > 1;
```

Join Vendors, Invoices, and Invoice_Line_Items. Use `COUNT(DISTINCT column)` to find how many unique accounts are used per vendor, and `HAVING` to filter for those > 1.

## Exercise 8
Write a SELECT statement that answers this question: What are the last payment date and total amount due for each vendor with each terms ID?

The result set should return these columns:
* The `terms_id` column from the `Invoices` table
* The `vendor_id` column from the `Invoices` table
* The last payment date for each combination of terms ID and vendor ID
* The sum of the balance due (invoice_total - payment_total - credit_total) for each combination of terms ID and vendor ID

Use the `WITH ROLLUP` operator to include rows that give the grand total and the totals for each terms ID. Use the `IF` and `GROUPING` functions to replace the null values in the terms_id and vendor_id columns of the summary rows with literals "Grand Total" and "Terms ID Total".

```sql
SELECT
    IF(GROUPING(terms_id) = 1, 'Grand Total', terms_id) AS terms_id_val,
    IF(GROUPING(vendor_id) = 1, 'Terms ID Total', vendor_id) AS vendor_id_val,
    MAX(payment_date) AS last_payment_date,
    SUM(invoice_total - payment_total - credit_total) AS balance_due
FROM invoices
GROUP BY terms_id, vendor_id WITH ROLLUP;
```

Group by `terms_id` and `vendor_id` with rollup. Use `IF(GROUPING(...))` to detect summary rows (where the grouped column is collapsed to NULL) and display a descriptive label instead.

> The `IF()` function is a control flow function that returns a value based on whether a condition is **TRUE** or **FALSE**.

* **The Logic:** It asks a question. If the answer is "Yes", it gives one result; if "No", it gives another.
* **Usage:** It is mostly used in the `SELECT` clause to transform raw data into readable labels on the fly.
* **Standard Alternative:** It is specific to MySQL. The standard SQL equivalent is the `CASE` statement.

**Syntax:**

```sql
IF(condition, value_if_true, value_if_false)
```

> The `GROUPING()` function is a helper used specifically with `WITH ROLLUP`. It identifies whether a row is a **super-aggregate (Total)** row or a regular data row.

* **The Problem:** `WITH ROLLUP` adds `NULL` to represent totals. However, your actual data might also contain `NULL`. How do you distinguish a "Total" from "Missing Data"?
* **The Solution:** `GROUPING(column_name)` returns a flag:
  * **`1`**: Indicates this row is a **Summary/Total** (the column was collapsed).
  * **`0`**: Indicates this row is a **Regular Value**.

**Common Usage:**

It is almost always used inside an `IF()` or `CASE` statement to replace the `NULL` displayed in the Total row with a readable label like "All" or "Grand Total".