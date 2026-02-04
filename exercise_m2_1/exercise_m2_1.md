# Exercises 1-14 from Chapter 3, Section 1

**Note:** Exercises 1-7 are review/GUI interaction steps.
The following SQL corresponds to the "Enter and run your own SELECT statements" and subsequent sections (Exercises 8-14).

## Exercise 8
Write a statement that returns `vendor_name`, `vendor_contact_last_name`, and `vendor_contact_first_name` from the Vendors table. Add an `ORDER BY` clause to sort by last name and then first name in ascending sequence.

```sql
SELECT vendor_name, vendor_contact_last_name, vendor_contact_first_name
FROM vendors
ORDER BY vendor_contact_last_name, vendor_contact_first_name;
```

The default sort order is ASC (ascending).

## Exercise 9
Write a statement that returns a column named `full_name` (combining last name and first name in the format "Doe, John"). Sort by last name and then first name. Filter for contacts whose last name begins with A, B, C, or E.

```sql
SELECT CONCAT(vendor_contact_last_name, ', ', vendor_contact_first_name) AS full_name
FROM vendors
WHERE vendor_contact_last_name LIKE 'A%'
   OR vendor_contact_last_name LIKE 'B%'
   OR vendor_contact_last_name LIKE 'C%'
   OR vendor_contact_last_name LIKE 'E%'
ORDER BY vendor_contact_last_name, vendor_contact_first_name;
```

The `%` wildcard matches zero or more characters. 

`CONCAT()` joins multiple strings together.

## Exercise 10
Write a statement returning `Due Date`, `Invoice Total`, `10%`, and `Plus 10%`. Filter for invoice totals between 500 and 1000 inclusive. Sort by `invoice_due_date` descending.

```sql
SELECT invoice_due_date AS 'Due Date',
       invoice_total AS 'Invoice Total',
       invoice_total * 0.1 AS '10%',
       invoice_total + (invoice_total * 0.1) AS 'Plus 10%'
FROM invoices
WHERE invoice_total >= 500 AND invoice_total <= 1000
ORDER BY invoice_due_date DESC;
```

## Exercise 11
Write a statement returning `invoice_number`, `invoice_total`, `payment_credit_total`, and `balance_due`. Sort by `balance_due` descending and use `LIMIT` to show only the top 5 largest balances.

```sql
SELECT invoice_number,
       invoice_total,
       payment_total + credit_total AS payment_credit_total,
       invoice_total - payment_total - credit_total AS balance_due
FROM invoices
ORDER BY balance_due DESC
LIMIT 5;
```

## Exercise 12
Write a statement returning `invoice_number`, `invoice_date`, `balance_due`, and `payment_date`. Filter for rows where `payment_date` is `NULL`.

```sql
SELECT invoice_number,
       invoice_date,
       invoice_total - payment_total - credit_total AS balance_due,
       payment_date
FROM invoices
WHERE payment_date IS NULL;
```

## Exercise 13
Write a statement without a `FROM` clause using `CURRENT_DATE` to return the current date. Use `DATE_FORMAT` to display it as `mm-dd-yyyy`.

```sql
SELECT DATE_FORMAT(CURRENT_DATE, '%m-%d-%Y') AS 'current_date';
```

> What happens without a FROM clause?

1. The database engine treats this line as a simple calculation instruction or system function call.
2. It generates a virtual single-row result set.
3. This is much faster than querying a table because the database doesn't need to scan data files on the hard drive; it simply performs the calculation in memory.

## Exercise 14
Write a statement without a `FROM` clause to create a row with `starting_principal`, `interest`, and `principal_plus_interest`.

```sql
SELECT 50000 AS starting_principal,
       50000 * 0.065 AS interest,
       50000 + (50000 * 0.065) AS principal_plus_interest;
```

or use subquery to avoid repeating yourself

```sql
SELECT starting_principal, 
       interest, 
       (starting_principal + interest) AS principal_plus_interest
FROM (
    SELECT 50000 AS starting_principal,
           50000 * 0.065 AS interest
) AS temp_table;
```