## Contents
- 3 interfaces to interact with a database using JDBC, each with their own privileges.
- Java program used as a driver for the interfaces.

## About
- This project was completed for a Database Systems course.
- The interfaces act as user interactions with a car rental company.
- Below is the original text submitted along side the project.
---

Nate Cable

For data generation, I used an online resource to generate 100 customers, 200 vehicles, and 20 stores

When prompted for a store id, there are currently 20 store locations with id's rangine from 1-20

PL/SQL procedures are used within the manager interface to delete customers and vehicles


GETTING STARTED:

    1) You will first be prompted to login to the Oracle Database on Edgar1

    2) After establishing a database connection, a menu will appear with 4 interface options:
        - Customer Interface
        - Staff Interface
        - Manager Interface
        - Exit (Quits the program)

    3) Selecting an interface will bring you to a specific program designed for that user described below:
    

Customer Interface:
    As a customer, you are given the following options:
        - View Stores (This option prints out every location's information to be helpful for choosing the correct store id for other options)
        - View Inventory (This option shows the customer the available vehicles to rent/reserve at their desired location)
        - Rent or Reserve a Vehicle (Enter a store id, then you will be prompted to enter a vehicle's plate to rent)
        - View or Cancel Rentals/Reservations (Customers can view or cancel their personal rentals and reservations)
        - View or Create Account (Customers can register a new account or view their information, to change their info they must go through Staff)
        - Exit

    Feel free to create your own User and use that id for your experience!


Staff Interface:
    As a staff member, you are given the following options:
        - View Stores (This option prints out every location's information to be helpful for choosing the correct store id for other options)
        - View Inventory (This option shows staff the vehicles at their desired location)
        - Change A Rental/Reservation (This allows staff to change rates and periods, or delete customer's rentals/reservations)
        - Update a Customer's Address (Staff have the ability to update a customer's info at their request)
        - Exit


Manager Interface:
    As a manager, you are given more privileges than normal staff:
        - View Stores (This option prints out every location's information to be helpful for choosing the correct store id for other options)
        - View Inventory (This option shows staff the vehicles at their desired location)
        - Change A Rental/Reservation (This allows staff to change rates and periods, or delete customer's rentals/reservations)
        - Update a Customer's Address (Staff have the ability to update a customer's info at their request)
        - Remove a Customer/Staff Member (Can remove a customer or staff member from Hurt's Database)
        - Remove a Vehicle (Can remove a vehicle from a specific store)
        - Exit
