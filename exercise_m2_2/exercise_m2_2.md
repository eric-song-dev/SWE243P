# Exercises 1-7 from Chapter 4, Section 1

## Exercise 1
Write a SELECT statement that returns all columns from the Vendors table inner-joined with all columns from the Invoices table. This should return 114 rows.

```sql
SELECT *
FROM vendors
JOIN invoices ON vendors.vendor_id = invoices.vendor_id;
```

Use an `INNER JOIN` (can be written simply as `JOIN`) to combine rows from both tables based on the common column `vendor_id`.

> Here is a short summary of the differences between SQL JOINs in Markdown.

A **JOIN** clause combines rows from two or more tables based on a related column between them.

| JOIN Type | Logic | Description |
| --- | --- | --- |
| **INNER JOIN** | **Matches Only** | Returns rows **only** when there is a match in **both** tables. |
| **LEFT JOIN** | **All Left + Matches** | Returns **all** rows from the **left** table, and matched rows from the right table (or `NULL` if no match). |
| **RIGHT JOIN** | **All Right + Matches** | Returns **all** rows from the **right** table, and matched rows from the left table (or `NULL` if no match). |
| **FULL JOIN** | **Everything** | Returns **all** rows when there is a match in **either** table. *(Note: MySQL does not support `FULL JOIN`; use `UNION` to simulate it).* |

> A Foreign Key is a constraint used to link two tables together. It ensures Referential Integrity of the data.

1. Links Tables: It creates a relationship between a column in a Child Table and the Primary Key of a Parent Table.

2. Enforces Integrity: It prevents you from inserting a value in the child table that does not exist in the parent table.

3. Prevents Errors: It stops the deletion or update of a parent row if it has linked rows in the child table (unless cascading is enabled).

4. Automates Maintenance: With ON DELETE CASCADE or ON UPDATE CASCADE, changes in the parent table automatically update or delete corresponding rows in the child table.

> The CONSTRAINT keyword is used to assign a specific, unique name to a rule (like a Foreign Key). Its specific roles are:

1. Identification: It names this specific rule.

2. Management: It allows you to easily modify or drop this specific foreign key later using its name.

3. Debugging: If an insert/update fails, the error message will explicitly cite the rule name, making it easier to find the problem.

## Exercise 2
Write a SELECT statement that returns `vendor_name`, `invoice_number`, `invoice_date`, and `balance_due` (invoice_total - payment_total - credit_total). Return one row for each invoice with a non-zero balance. Sort by `vendor_name` ascending.

```sql
SELECT v.vendor_name,
       i.invoice_number,
       i.invoice_date,
       i.invoice_total - i.payment_total - i.credit_total AS balance_due
FROM vendors v
JOIN invoices i ON v.vendor_id = i.vendor_id
WHERE i.invoice_total - i.payment_total - i.credit_total > 0
ORDER BY v.vendor_name ASC;
```

Join `vendors` and `invoices` on `vendor_id`. Calculate `balance_due` in the SELECT clause. Use the `WHERE` clause to filter for balances greater than 0. Use table aliases (`v`, `i`) for brevity. Sort by `vendor_name`.

## Exercise 3
Write a SELECT statement that returns `vendor_name`, `default_account` (from default_account_number), and `description` (from account_description). Return one row for each vendor. Sort by `account_description` then `vendor_name`.

```sql
SELECT v.vendor_name,
       v.default_account_number AS default_account,
       a.account_description AS description
FROM vendors v
JOIN general_ledger_accounts a ON v.default_account_number = a.account_number
ORDER BY a.account_description, v.vendor_name;
```

Join `vendors` and `general_ledger_accounts` on the account number fields.

## Exercise 4
Write a SELECT statement that returns `vendor_name`, `invoice_date`, `invoice_number`, `li_sequence` (invoice_sequence), and `li_amount` (line_item_amount). Sort by vendor, date, number, and sequence.

```sql
SELECT v.vendor_name,
       i.invoice_date,
       i.invoice_number,
       li.invoice_sequence AS li_sequence,
       li.line_item_amount AS li_amount
FROM vendors v
JOIN invoices i ON v.vendor_id = i.vendor_id
JOIN invoice_line_items li ON i.invoice_id = li.invoice_id
ORDER BY v.vendor_name, i.invoice_date, i.invoice_number, li.invoice_sequence;
```

Perform a three-way join: `vendors` to `invoices` on `vendor_id`, then `invoices` to `invoice_line_items` on `invoice_id`.

## Exercise 5
Write a SELECT statement that returns `vendor_id`, `vendor_name`, and `contact_name` (combined first and last name) for vendors whose contact has the same last name as another vendor's contact.

```sql
SELECT v1.vendor_id,
       v1.vendor_name,
       CONCAT(v1.vendor_contact_first_name, ' ', v1.vendor_contact_last_name) AS contact_name
FROM vendors v1
JOIN vendors v2 ON v1.vendor_id <> v2.vendor_id 
               AND v1.vendor_contact_last_name = v2.vendor_contact_last_name
ORDER BY v1.vendor_contact_last_name;
```

Use a self-join on the `vendors` table (aliased as `v1` and `v2`). The join condition must ensure they are different vendors (`v1.vendor_id <> v2.vendor_id`) but match on the last name.

## Exercise 6
Write a SELECT statement that returns `account_number` and `account_description` from General_Ledger_Accounts for accounts that have never been used in the Invoice_Line_Items table.

```sql
SELECT g.account_number,
       g.account_description
FROM general_ledger_accounts g
LEFT JOIN invoice_line_items li ON g.account_number = li.account_number
WHERE li.invoice_id IS NULL
ORDER BY g.account_number;
```

We need to find records in the "left" table (`general_ledger_accounts`) that have no matching records in the "right" table (`invoice_line_items`).

Use a `LEFT JOIN` (or LEFT OUTER JOIN) to include all accounts. Filter with `WHERE li.invoice_id IS NULL` to keep only the rows where no match was found in the line items table.

## Exercise 7
Use the UNION operator to generate a result set with `vendor_name` and `vendor_state`. If the state is 'CA', display "CA"; otherwise, "Outside CA". Sort by vendor name.

```sql
SELECT vendor_name, 'CA' AS vendor_state
FROM vendors
WHERE vendor_state = 'CA'
UNION
SELECT vendor_name, 'Outside CA' AS vendor_state
FROM vendors
WHERE vendor_state <> 'CA'
ORDER BY vendor_name;
```

The requirement asks for a combined result set derived from two different conditions on the database, using `UNION`.

Create two separate `SELECT` queries: one filtering for `vendor_state = 'CA'` and hardcoding the state column as 'CA', and another for `vendor_state <> 'CA'` hardcoding 'Outside CA'. Combine them with `UNION`.

Here is a short summary of the MySQL **UNION** operator in Markdown.

> The `UNION` operator is used to **combine** the result sets of two or more `SELECT` statements into a single result set.

* **Vertical Combination:** Unlike `JOIN` (which combines columns horizontally), `UNION` stacks results on top of each other (vertically).
* **Removes Duplicates:** By default, `UNION` automatically removes duplicate rows from the results.
* **Requirements:**
  * Every `SELECT` statement must have the **same number of columns**.
  * The columns must be in the **same order** and have compatible **data types**.

> UNION vs. UNION ALL

| Operator | Action | Performance |
| --- | --- | --- |
| **UNION** | Combines results & **removes** duplicates. | Slower (needs to check for duplicates). |
| **UNION ALL** | Combines results & **keeps** duplicates. | Faster (no checking required). |
