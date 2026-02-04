# Exercises 1-7 from Chapter 7, Section 2

## Exercise 1
Write a SELECT statement that returns the same result set as this SELECT statement, but don't use a join. Instead, use a subquery in a WHERE clause that uses the IN keyword.
```sql
SELECT DISTINCT vendor_name
FROM vendors JOIN invoices
  ON vendors.vendor_id = invoices.vendor_id
ORDER BY vendor_name
```

```sql
SELECT vendor_name
FROM vendors
WHERE vendor_id IN
    (SELECT vendor_id
     FROM invoices)
ORDER BY vendor_name;
```

Use a subquery that selects all `vendor_id`s from the `invoices` table. The main query then filters `vendors` to include only those whose ID exists in that list, effectively replacing the inner join behavior.

> The `DISTINCT` keyword is used to return only **different (unique)** values. It automatically eliminates duplicate rows from your query result.

* **Filters Duplicates:** It scans the result set and removes any row that is identical to another.
* **Scope (Important):** It applies to **all** columns listed in the `SELECT` statement together.
  * *Logic:* It checks for unique **combinations** of rows, not just the first column.
* **NULL Handling:** It treats all `NULL` values as identical (it will keep only **one** `NULL` row).


## Exercise 2
Write a SELECT statement that answers this question: Which invoices have a payment total that's greater than the average payment total for all invoices with a payment total greater than 0?
Return the `invoice_number` and `invoice_total` columns for each invoice. This should return 20 rows.
Sort the results by the `invoice_total` column in descending order.

```sql
SELECT invoice_number, invoice_total
FROM invoices
WHERE payment_total >
    (SELECT AVG(payment_total)
     FROM invoices
     WHERE payment_total > 0)
ORDER BY invoice_total DESC;
```

Use a subquery to calculate the average `payment_total` for invoices where payment has been made (> 0). The outer query then filters for invoices exceeding this calculated average.

## Exercise 3
Write a SELECT statement that returns two columns from the `General_Ledger_Accounts` table: `account_number` and `account_description`.
Return one row for each account number that has never been assigned to any line item in the `Invoice_Line_Items` table. To do that, use a subquery introduced with the NOT EXISTS operator. This should return 54 rows.
Sort the results by the `account_number` column.

```sql
SELECT account_number, account_description
FROM general_ledger_accounts gla
WHERE NOT EXISTS
    (SELECT *
     FROM invoice_line_items ili
     WHERE ili.account_number = gla.account_number)
ORDER BY account_number;
```

Use a correlated subquery with `NOT EXISTS`. For each account in the `general_ledger_accounts` table, the subquery checks if there are any matching rows in `invoice_line_items`. If no match is found, the account is included in the result.

> The `NOT EXISTS` operator is used to test if a subquery returns **zero rows**. It is effectively a "None" check.

* **The Logic:** It returns **TRUE** if the subquery returns **nothing** (an empty result set).
* **Correlated Subquery:** It usually checks a relationship row-by-row between the outer table and the inner table.
* **Performance:** It is efficient because it **short-circuits**. As soon as it finds *one* matching row in the subquery, it stops looking and returns `FALSE`.

## Exercise 4
Write a SELECT statement that returns four columns: `vendor_name`, `invoice_id`, `invoice_sequence`, and `line_item_amount`.
Return a row for each line item of each invoice that has more than one line item in the `Invoice_Line_Items` table. Hint: Use a subquery that tests for `invoice_sequence > 1`. This should return 6 rows.
Sort the results by the `vendor_name`, `invoice_id`, and `invoice_sequence` columns.

```sql
SELECT v.vendor_name, i.invoice_id, ili.invoice_sequence, ili.line_item_amount
FROM vendors v
JOIN invoices i ON v.vendor_id = i.vendor_id
JOIN invoice_line_items ili ON i.invoice_id = ili.invoice_id
WHERE i.invoice_id IN
    (SELECT invoice_id
     FROM invoice_line_items
     WHERE invoice_sequence > 1)
ORDER BY v.vendor_name, i.invoice_id, ili.invoice_sequence;
```

The subquery identifies `invoice_id`s that have an `invoice_sequence` greater than 1, implying multiple line items. The main query joins `vendors`, `invoices`, and `invoice_line_items`, filtering for invoices that appear in that subquery list.

## Exercise 5
Write a SELECT statement that returns two columns: `vendor_id` and the largest unpaid invoice for each vendor. To do this, you can group the result set by the `vendor_id` column. This should return 7 rows.
Write a second SELECT statement that uses the first SELECT statement in its FROM clause. The main query should return a single value that represents the sum of the largest unpaid invoices for each vendor.

```sql
SELECT SUM(max_unpaid) AS total_largest_unpaid
FROM
    (SELECT vendor_id, MAX(invoice_total) AS max_unpaid
     FROM invoices
     WHERE invoice_total - credit_total - payment_total > 0
     GROUP BY vendor_id) t;
```

The inner query (derived table `t`) finds the maximum `invoice_total` for each `vendor_id` where there is an outstanding balance. The outer query then sums these maximum values to get a single total.

## Exercise 6
Write a SELECT statement that returns the name, city, and state of each vendor that's located in a unique city and state. In other words, don't include vendors that have a city and state in common with another vendor. This should return 38 rows.
Sort the results by the `vendor_state` and `vendor_city` columns.

```sql
SELECT vendor_name, vendor_city, vendor_state
FROM vendors
WHERE (vendor_city, vendor_state) IN
    (SELECT vendor_city, vendor_state
     FROM vendors
     GROUP BY vendor_city, vendor_state
     HAVING COUNT(*) = 1)
ORDER BY vendor_state, vendor_city;
```

Use a subquery that groups vendors by city and state, filtering with `HAVING COUNT(*) = 1` to find unique locations. The outer query selects the vendor details for those locations.

## Exercise 7
Use a correlated subquery to return one row per vendor, representing the vendor's oldest invoice (the one with the earliest date). Each row should include these four columns: `vendor_name`, `invoice_number`, `invoice_date`, and `invoice_total`. This should return 34 rows.
Sort the results by the `vendor_name` column.

```sql
SELECT vendor_name, invoice_number, invoice_date, invoice_total
FROM invoices i
JOIN vendors v ON i.vendor_id = v.vendor_id
WHERE invoice_date =
    (SELECT MIN(invoice_date)
     FROM invoices i2
     WHERE i2.vendor_id = i.vendor_id)
ORDER BY vendor_name;
```

A correlated subquery is used to find the minimum (earliest) `invoice_date` for the current vendor in the outer query. The `WHERE` clause ensures only the invoice matching that earliest date for each vendor is returned.
