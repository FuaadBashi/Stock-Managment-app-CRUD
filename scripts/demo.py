#!/usr/bin/env python3
"""Exercise a running application using only the Python standard library."""
import base64
import http.cookiejar
import json
import os
import urllib.request
import uuid
from urllib.parse import urlparse

base = os.environ.get("APP_URL", "http://127.0.0.1:8080").rstrip("/")
parsed = urlparse(base)
if parsed.scheme not in {"http", "https"} or not parsed.hostname:
    raise SystemExit("APP_URL must be an HTTP(S) URL")
if parsed.scheme != "https" and parsed.hostname not in {"localhost", "127.0.0.1", "::1"}:
    raise SystemExit("Use HTTPS when sending credentials outside localhost")
password = os.environ.get("APP_PASSWORD")
if not password:
    raise SystemExit("Set APP_PASSWORD to the running application's password")
credentials = base64.b64encode(f'{os.environ.get("APP_USERNAME", "admin")}:{password}'.encode()).decode()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
headers = {"Authorization": f"Basic {credentials}", "Content-Type": "application/json"}


def request(method, path, data=None):
    payload = None if data is None else json.dumps(data).encode()
    req = urllib.request.Request(base + path, data=payload, headers=headers, method=method)
    with opener.open(req, timeout=15) as response:
        return json.load(response)


token = request("GET", "/csrf")
headers[token["headerName"]] = token["token"]
suffix = uuid.uuid4().hex[:10]
user = request("POST", "/users", {"firstName": "Demo", "lastName": "Operator", "email": f"demo-{suffix}@example.com"})
supplier = request("POST", "/suppliers", {"name": f"Demo supplier {suffix}", "companyNumber": suffix})
stock = request("POST", "/stock", {"name": "Coffee beans", "supplierId": supplier["id"], "retailPrice": 12.50})
receipt = request("POST", f'/stock/{stock["stockItemId"]}/receipts', {
    "incomingDate": "2026-09-01", "expiryDate": "2027-09-01", "quantity": 5, "storage": "COOL_AND_DRY"})
ingredient = request("POST", "/ingredients", {"name": "Coffee", "unit": "G"})
product = request("POST", "/products", {"name": "Espresso", "desc": "Double shot", "retailPrice": 2.35})
request("PUT", f'/products/{product["id"]}/recipe', [{"ingredientId": ingredient["id"], "quantity": 18}])
order = request("POST", "/customer-orders", {"userId": user["id"], "itemRequests": [{"productId": product["id"], "quantity": 3}]})
request("POST", f'/customer-orders/{order["customerOrderId"]}/start')
finished = request("POST", f'/customer-orders/{order["customerOrderId"]}/complete')
assert finished["status"] == "COMPLETED" and finished["total"] == 7.05
print(json.dumps({"receiptId": receipt["id"], "completedOrder": finished}, indent=2))
