## BillGate: Simple Modern Billing System

BillGate is a set of REST services designed to act as:

* Authentication and authorization server, allowing to create, read, update and delete Client, Role and Policy records, and perform the authentication sequence;
* Product / subscription management server (each Client with a Business role can create Products in the database, specifying payment conditions like one-time payment or subscription; each Business Client can also maintain its own set of regular Clients (customers), so they can be authenticated using the same sequence);
* Payment methods gateway, accepting different payment options (like PayPal or Moneybookers) and updating the database, so products can be viewed as paid for or with payment pending.

### Authentication sequence

1. User issues HTTP(S) POST request to /auth/session. 
2. The application attempts to authenticate client with the credentials provided (using salted SHA-256).
3. If authentication has been successful, a new Session record is created in the database, containing the reference to the Client object (which contains the reference to corresponding Role and Policy). Session object should have an UUID, which is returned as a HTTP 200 response to the authenticating party. Response should also contain a Set-Cookie header, setting the "sid" parameter to session UUID. Sessions also have an expiration time; expired sessions are no longer valid.
4. If authentication has failed, HTTP 401 response is returned to the client, with "AUTH FAILED" content body.
5. To request any other services from BillGate, the client should present a cookie with session UUID in its HTTP request. If the corresponding session is found, and the client is allowed to perform an action (i.e. one of his Roles contains a policy entry allowing him to do this), the action is performed and its response is returned to the client. If the session is found, but the client is not allowed to perform the action, a HTTP 401 response is returned with "PERMISSION DENIED" content body. If session is not found, a HTTP 404 response is returned with "SESSION UNKNOWN" body.