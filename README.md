E-commerce Platform With MicroServices                        date: 26-04-2025
									          writer: Sajjan Sharma

Tech stack:
	Spring boot
	Spring Cloud Gateway
	Kafka
	PostgresQL
	Stripe
Optional:
	ReactJs Front end
	MogoDB
RabbitMQ

Description of application:
An e-commerce back end which allows customers to buy items from a list of items and checkout with their bank card.

Requirements:

✔️ = done ❌= not started 🪲= working on

Endpoints/Controllers:
Customers can login with valid credentials✔️
Jwt tokens to validate user for a set time✔️
Customers can register with credentials✔️
There is input validation (eg email looks like email@organisation.com, checked using regex)❌


Customers can add items to a basket.✔️
There can only be one active basket at a time to keep checkout integrity✔️
Once a basket is checked out a new empty on is given🪲
Ensure integrity of the database and make sure only one active basket at any given time 🪲

Optional:
A receipt system which sends receipts to the user email❌
Email authentication to ensure the email the user registered with is theirs❌

Security:
Server only sends Jwt Tokens to clients which send valid customer/user details✔️
Ensures that data stored in database is secure and XSS attack immune❌
Server only accepts requests from verified clients urls✔️
End points are conditioned by roles ✔️
A separate role/authority for Administrator/Owner to modify the items ✔️


Apache Kafka
Apache  Kafka is a distributed event streaming platform used to decouple service and ensure reliable, scalable communication. It acts as an intermediary where services can publish events and other services can independently subscribe and react to those events. This design improves system resilience, scalability, and flexibility without tightly coupling different parts of the application.
Key words: intermediary, event, services, resilience, scalability, flexibility, reliable, scalable.

Apache kafka used to active Stripe Payment order❌
