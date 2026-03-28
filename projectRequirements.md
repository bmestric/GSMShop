# Online Shopping Application Specification

Create an application that enables online purchasing of selected products.

## Application Features

The application has the following characteristics:

- There are 2 roles in the application:
    - User (Customer)
    - Administrator

---

## USER

- Anonymous users can browse categories *(anonymous)*
- Anonymous users can browse products *(anonymous)*
- Users can add products to the cart *(anonymous)*
- Anonymous must be registered to complete a purchase *(registration and login functionalities must be implemented)*
- Users can define the quantity of a specific product added to the cart  
  *(e.g., 20 screen protectors for a mobile phone)* *(anonymous)*
- Users can modify the cart contents:
    - Remove a product completely
    - Remove a specific quantity of a product
    - Clear the entire cart  
      *(anonymous)*

- Users can complete an online purchase:
    - Payment options:
        - Cash on delivery
        - PayPal  
          *(All information about PayPal implementation, sandbox account creation, etc. can be found at: https://developer.paypal.com/)*  
          *(authenticated – customer)*

- Users must be able to view their purchase history with the following details:  
  *(authenticated – customer)*

    - What was purchased (cart contents, quantities, and product details)
    - When it was purchased
    - How it was purchased (cash on delivery or PayPal)

---

## ADMINISTRATOR

- Administrator manages categories and products  
  *(CRUD operations on categories and products)*  
  *(authenticated – admin)*

- Administrator can view login history:
    - Who logged in
    - When
    - From which address  
      *(authenticated – admin)*

- Administrator can view complete purchase history for all customers:
    - Same details as user purchase history
    - Must include simple filters:
        - By customer
        - By purchase time period  
          *(authenticated – admin)*

---

## GENERAL INSTRUCTIONS

- **Visual design:** Do your best *(hint: Bootstrap)*

- The application must be deployed to a hosting environment before presentation:
    - Hosting does not have to be public

- Mandatory technologies:
    - Spring MVC
    - Thymeleaf *(or equivalent template engine such as JSP + JSTL, FreeMarker, Mustache)*

- **Application design:** Apply best practices

- Must include at least one appropriate use of:
    - Filters
    - Listeners

- Implement JWT tokens:
    - Access token
    - Refresh token  
      *(to secure REST API endpoints)*

- Implement authentication and authorization using:
    - Spring Security

- Implement asynchronous functionalities

- No class may exceed **200 lines of code**

- SonarQube report must show **zero errors**