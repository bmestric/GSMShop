# IMPROVMENT.md

## GENERAL NOTES FROM MANUAL TESTING
- **PRODUCT CREATION FORM**:
    - when adding new product, if chosen Product Type is "Phone",
      the form should show all phone specification fields(Currently missing Camera)
    - if Product Type is "Phone" than Category must be Smartphone, so we can remove Category drop down from there

    - if Product Type is "Accessory", the form should hide all phone specification fields and have no SmartPhone Category type offered

- **GENERAL ADDING TO CART**:
  - User can only add item through a button, there is no spinner for choosing quantity
  - when user is redirected to cart after pressing add to cart he can change quantity in cart, but there is no update/add button for that quantity
  - user should be able to add multiple quantity of different products to cart without beeing redirected to cart, until he presses "Go to cart" button.
  - when adding product to cart, it should check if requested quantity is available in stock and show error message if not
  - when there is 0 stock it should be marked as "Out of stock" and not allow adding to cart, currently if user presses add to cart if allows them and stock goes negative.

- **/ADMIN/PRODUCTS**
    - when from api above we click New Product for the first time it goes blank with:
    - ```
      ERROR 33016 --- [gsmShop] [nio-8080-exec-7] a.e.ErrorMvcAutoConfiguration$StaticView :
       Cannot render error page for request [/admin/products/new]
       and exception [An error happened during template parsing (template: "class path resource [templates/admin/product/form.html]")]
       as the response has already been committed.
       As a result, the response may have the wrong status code.
        ```
      on the second click it works normally

- **/ADMIN/PRODUCT/EDIT/{ID}**
    - when ie. I edit stock and press "Save Product" it redirects me to /admin/products/save and that returns 500. instead of that it should just redirect back to all products screen
      also after that stock is not updated, it should update stock and all other fields that are changed in edit form