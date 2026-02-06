# Database and Entity Design Plan

This plan outlines the design for the database schema and corresponding Java entities for the user, credit, and activation code systems.

## 1. Database Schema (SQL)

I will create a SQL script to define the table structures. All primary keys will be `BIGINT` and are intended to be used with a Snowflake ID generator.

-   `t_user`: Stores user information.
-   `t_user_auth`: Stores user authentication and token information.
-   `t_product`: Stores product information.
-   `t_credit_balance`: Stores user credit balances.
-   `t_credit_log`: Stores the history of credit transactions.
-   `t_activation_code`: Stores activation codes for products.
    -   A `UNIQUE` index will be added to the `code` column to prevent race conditions.

## 2. Java Entities

I will create Java entity classes corresponding to each database table. These classes will use Lombok for boilerplate code reduction and MyBatis-Plus annotations for ORM mapping. The primary key fields will be annotated with `@TableId(type = IdType.ASSIGN_ID)` to indicate the use of a Snowflake ID generator.

-   **`base.ecs32.top.entity.User`**: Maps to `t_user`.
-   **`base.ecs32.top.entity.UserAuth`**: Maps to `t_user_auth`.
-   **`base.ecs32.top.entity.Product`**: Maps to `t_product`.
-   **`base.ecs32.top.entity.CreditBalance`**: Maps to `t_credit_balance`.
-   **`base.ecs32.top.entity.CreditLog`**: Maps to `t_credit_log`.
-   **`base.ecs32.top.entity.ActivationCode`**: Maps to `t_activation_code`.

## 3. Enums

I will create Java enums for fields with a fixed set of values.

-   **`base.ecs32.top.enums.CreditLogType`**: For the `type` field in `t_credit_log`.
-   **`base.ecs32.top.enums.UserStatus`**: For the `status` field in `t_user`.
-   **`base.ecs32.top.enums.ActivationCodeStatus`**: For the `status` field in `t_activation_code`.

## 4. Project Structure

I will organize the files into the following package structure:

```
src/main/java/base/ecs32/top/
├── entity/
│   ├── User.java
│   ├── UserAuth.java
│   ├── Product.java
│   ├── CreditBalance.java
│   ├── CreditLog.java
│   └── ActivationCode.java
└── enums/
    ├── CreditLogType.java
    ├── UserStatus.java
    └── ActivationCodeStatus.java
```

## 5. Implementation Steps

I will now proceed with creating the SQL schema and then the Java entities. I will ask for your approval of the plan before switching to `code` mode to implement it.
