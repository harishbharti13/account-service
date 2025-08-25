# Account Service



## Run

```bash
mvn spring-boot:run
```

The app uses H2 in-memory DB by default.

## Endpoints

- `POST /api/v1/accounts` - create account
- `GET  /api/v1/accounts/{accountId}` - get account details
- `POST /api/v1/accounts/{accountId}/transactions` - perform transaction (DEBIT/CREDIT)
- `GET  /api/v1/accounts/{accountId}/transactions` - list transactions (optional startDate & endDate ISO-8601)

## Example cURL

Create account:
```bash
curl --location --request POST 'http://localhost:8080/api/v1/accounts/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "accountName": "Savings Account",
    "currency": "INR",
    "initialBalance": 5000.00
}'
```

Response - 
```bash
{
"accountId": "20797cad-e9d6-4916-ad8a-bd5633aa9b92",
"accountName": "Savings Account",
"currency": "INR",
"balance": 5000.00,
"createdAt": "2025-08-25T08:47:01.062871334Z",
"updatedAt": "2025-08-25T08:47:01.062871334Z"
}
```

Credit:
```bash
curl --location --request POST 'http://localhost:8080/api/v1/accounts/20797cad-e9d6-4916-ad8a-bd5633aa9b92/transactions' \
--header 'Content-Type: application/json' \
--data-raw '{
    "type": "CREDIT",
    "amount": 1300.00,
    "description": "salary"
}'
```

Response - 
```bash
{
    "transactionId": "45b27d50-ff63-4f78-9efd-5856adbe1ff7",
    "accountId": "20797cad-e9d6-4916-ad8a-bd5633aa9b92",
    "type": "CREDIT",
    "amount": 1300.00,
    "timestamp": "2025-08-25T10:13:53.832879531Z",
    "description": "salary",
    "updatedBalance": 5100.00
}
```

Debit:
```bash
curl --location --request POST 'http://localhost:8080/api/v1/accounts/20797cad-e9d6-4916-ad8a-bd5633aa9b92/transactions' \
--header 'Content-Type: application/json' \
--data-raw '{
    "type": "DEBIT",
    "amount": 1200.00,
    "description": "ATM withdrawal"
}'
```
Response -
```bash
{
    "transactionId": "98295236-d551-4863-ac74-ee636d4c2f49",
    "accountId": "20797cad-e9d6-4916-ad8a-bd5633aa9b92",
    "type": "DEBIT",
    "amount": 1200.00,
    "timestamp": "2025-08-25T09:36:01.403440965Z",
    "description": "ATM withdrawal",
    "updatedBalance": 3800.00
}
```
## Tests

Run:
```
mvn test
```

JaCoCo report is generated after tests.

Below will use the jacoco.exec file to generate an HTML report at -
target/site/jacoco/index.html
```
mvn jacoco:report
```


## Assumptions

- Uses H2 for simplicity.
- Amounts use `BigDecimal` with scale 2.
- Transactions are executed in a single `@Transactional` method.
