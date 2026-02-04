# Exercises 1-5 from Chapter 11, Section 2

## Exercise 1
Write a script that adds an index to the AP database for the zip code field in the Vendors table.

```sql
USE ap;

CREATE INDEX vendors_zip_code_ix
  ON vendors (vendor_zip_code);
```

The `CREATE INDEX` statement creates a new index named `vendors_zip_code_ix` on the `vendor_zip_code` column of the `vendors` table. This improves the performance of queries that filter or sort by zip code.

## Exercise 2
Write a script that contains the CREATE TABLE statements needed to implement the following design in the EX database:
*   **members table**: `member_id`, `first_name`, `last_name`, `address`, `city`, `state`, `phone`
*   **committees table**: `committee_id`, `committee_name`
*   **members_committees table**: `member_id`, `committee_id`

Additional requirements:
*   The `member_id` and `committee_id` columns are the primary keys of the Members and Committees tables, and these columns are foreign keys in the `Members_Committees` table.
*   Include any constraints or default values that you think are necessary.
*   Include statements to drop the tables if they already exist.

```sql
USE ex;

DROP TABLE IF EXISTS members_committees;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS committees;

CREATE TABLE members
(
  member_id     INT           PRIMARY KEY   AUTO_INCREMENT,
  first_name    VARCHAR(50)   NOT NULL,
  last_name     VARCHAR(50)   NOT NULL,
  address       VARCHAR(50)   NOT NULL,
  city          VARCHAR(50)   NOT NULL,
  state         CHAR(2)       NOT NULL,
  phone         VARCHAR(20)   NOT NULL
);

CREATE TABLE committees
(
  committee_id    INT           PRIMARY KEY   AUTO_INCREMENT,
  committee_name  VARCHAR(50)   NOT NULL
);

CREATE TABLE members_committees
(
  member_id     INT    NOT NULL,
  committee_id  INT    NOT NULL,
  CONSTRAINT members_committees_pk PRIMARY KEY (member_id, committee_id),
  CONSTRAINT members_committees_fk_members FOREIGN KEY (member_id) REFERENCES members (member_id),
  CONSTRAINT members_committees_fk_committees FOREIGN KEY (committee_id) REFERENCES committees (committee_id)
);
```

*   `DROP TABLE IF EXISTS` ensures we start with a clean slate. We drop `members_committees` first because it depends on the other two.
*   `members` and `committees` tables use `AUTO_INCREMENT` for their surrogate primary keys.
*   `members_committees` is a linking table (many-to-many relationship). Its primary key is a composite of `member_id` and `committee_id`. Both columns are also defined as foreign keys referencing their respective parent tables.

## Exercise 3
Write INSERT statements that add rows to the tables that are created in exercise 2.
*   Add two rows to the Members table for the first two member IDs.
*   Add two rows to the Committees table for the first two committee IDs.
*   Add three rows to the Members_Committees table: one row for member 1 and committee 2; one for member 2 and committee 1; and one for member 2 and committee 2.
*   Write a SELECT statement that joins the three tables and retrieves the committee name, member last name, and member first name. Sort the results by the committee name, member last name, and member first name.

```sql
INSERT INTO members (first_name, last_name, address, city, state, phone)
VALUES 
('John', 'Smith', 'UCI', 'Irvine', 'CA', '123'),
('Jane', 'Doe', 'UCI', 'Irvine', 'CA', '456');

INSERT INTO committees (committee_name)
VALUES 
('Finance'),
('Social');

INSERT INTO members_committees (member_id, committee_id)
VALUES 
(1, 2), -- Member 1 (John), Committee 2 (Social)
(2, 1), -- Member 2 (Jane), Committee 1 (Finance)
(2, 2); -- Member 2 (Jane), Committee 2 (Social)

SELECT c.committee_name, m.last_name, m.first_name
FROM members m
JOIN members_committees mc ON m.member_id = mc.member_id
JOIN committees c ON mc.committee_id = c.committee_id
ORDER BY c.committee_name, m.last_name, m.first_name;
```

The `SELECT` statement uses two `JOIN` clauses to link `members` to `members_committees` and then to `committees` to retrieve the human-readable names.

## Exercise 4
Write an ALTER TABLE statement that adds two new columns to the Members table created in exercise 2.
*   Add one column for annual dues that provides for three digits to the left of the decimal point and two to the right. This column should have a default value of 52.50.
*   Add one column for the payment date.

```sql
ALTER TABLE members
ADD annual_dues DECIMAL(5,2) DEFAULT 52.50,
ADD payment_date DATE;
```

The `ALTER TABLE` statement is used to modify the structure of an existing table. We verify `DECIMAL(5,2)` accommodates 3 digits to the left (total 5 precision, 2 scale). `DEFAULT` sets the initial value for new rows (and existing rows if not specified).

## Exercise 5
Write an ALTER TABLE statement that modifies the Committees table created in exercise 2 so the committee name in each row has to be unique. Then, use an INSERT statement to attempt to insert a duplicate name. This statement should fail due to the unique constraint.

```sql
ALTER TABLE committees
ADD CONSTRAINT committee_name_uq UNIQUE (committee_name);

-- This statement should fail
INSERT INTO committees (committee_name) VALUES ('Finance');
```

```sql
Error Code: 1062. Duplicate entry 'Finance' for key 'committees.committee_name_uq'

```

We use `ALTER TABLE` to add a `UNIQUE` constraint to the `committee_name` column. The subsequent `INSERT` attempts to add 'Finance' again, which was already inserted in Exercise 3, causing a violation of the unique constraint and raising an error.
