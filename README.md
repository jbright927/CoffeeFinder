# CoffeeFinder

# Table of Contents
 
* [Synopsis](#synopsis)
* [Design Considerations](#design-considerations)
* [Testing](#testing)
* [Contributors](#contributors)

# <a name="synopsis"></a>Synopsis
This project incorporates the Foursquare API in order to find nearby coffee stores in order of proximity and then provide relevant data and directions.

# <a name="design-considerations"></a>Design Considerations

The app is designed to be relatively lightweight, seeing as network access will typically happen using mobile data. 

Updates occur if one of the following conditions has been met:

* 5 minutes has expired since the last geolocation update
* The user has initiated a search
* The user has moved at least 100 metres from the position of their last geolocation update
* The app gains focus after being in the background

Updates can also occur passively if requests for geolocation are being made outside of the app, but will not occur if the app has been put into the background.

The radius of locations included in searches is currently about 7.5km. This is representative of the Greater Sydney region, in which the density of coffee shops is quite low. 
For best results in a densely populated city, an optimal distance would be about 1.25km, assuming an average walking speed of between 4.5 and 5.5km/h and a maximum walking time of 15 minutes to reach a nearby coffee shop.

It's also important to note that a wider search radius results in the acquisition of more relevant and popular venues. The Foursquare does not have an option for "closest venues" but instead provides search results based on which locations you are most likely to check in to (if you were a user). 
This is likely based on factors such as the completeness of a venue's details, proximity and user rating. Future updates will incorporate other parameters in order to provide the best user experience.

# <a name="testing"></a>Testing
JUnit tests can be performed in IntelliJ by right clicking the TestHomeActivity.java file, selecting the "Select TestHomeActivity' option and then building the project. Automated testing is carried out using the Robolectric framework.

# <a name="contributors"></a>Contributors
Joshua Bright - jbright927@gmail.com
