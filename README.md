**RooBus App**

This app will display the routes to catch RooBus and allows the user to set an alarm to remind when the Roobus is near by his selected stop


Below image is the snapshot when opening the app and the user can input “From” and “To” address either by typing in keyboard or using Google Voice. After entering the login details and pressing “Scan RooBus” button will show the operating RooBus services at that particular moment. For example, the app will show the Available routes as CHAPEL ROUTE and WEEKEND ROUTE when the time is 10:30 AM on Saturday.

![](https://sites.google.com/site/uakron12345/home/21.png)  
                                             

After selecting any of the routes, Google Maps will be displayed pointing the user input and destination locations.

The User location, destination location are indicated using human symbol and Green markers and RooBus stops are indicated using Bus markers. The User location to his/her nearest RooBus stop and RooBus drop point to user destination is indicated using red coloured lanes. The walking distance and RooBus location names are also displayed on the page. The distance is calculated using google distance matrix API's

![](https://sites.google.com/site/uakron12345/home/22.png)

**Remainder**: The user can set the alarm to remind him about the update of the bus when it is nearby. By browsing the “Roobus” and the required stop , min time to catch the bus is calculated . 
Using  Double map JSON files , GPS location of the bus is retrieved and remaining time for the bus to reach the destination is calculated at real time using double Map average travel reports and google distance matrix apis

![](https://sites.google.com/site/uakron12345/home/23.png)

 





  




