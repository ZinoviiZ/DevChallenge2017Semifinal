# DevChallengeSemifinal
Application for DevChallenge2017 semifinal, backend nomination, middle-senior category. </br>
With that project I've passed semifinal and have been chosen for final. </br>
https://devchallenge.it/ </br>

## DevChallenge
All-Ukrainian software development championship among junior, middle and senior programmers. The championship takes place in 3 stages: Qualification, Semifinal and offline Finals in Kyiv. Participants compete in eleven nominations in three directions: Web, Mobile, Game. Each nomination has 2 categories: Pro (Senior, Middle-to-Senior) and Standard (Middle, Junior-to-Middle). At the final, the tasks were from the partner Amazon.

# Task
**Implement a monitoring system for changes in news/ad texts on the site, saving the history of changes.**
Input: Select any site that contains a list of publications (with a paging agent). For example, this may be this site: http://brovary-rada.gov.ua/documents (or any other). </br>
Monitoring: The system should be able to crawl publications that are linked, browse through pages and save content (only HTML, no files).</br>
It is necessary to provide a regular scan of the most recent pages, and slightly less frequently - older pages. In case of detecting changes in the publication test (document), the system must save the new version.</br>
Note that the page may also disappear, therefore, such cases should also be detected. You can start job scanning in any way convenient for you:</br>
- Automatically by the timer (or scheduler)</br>
- By calling the API method (regularity, in this case, will be provided from the outside by regular call of the method)</br>
- Other ways that you choose</br>
Expected result: The API that provides access to the list of all publications, to the list of edits or deleted posts, and to different versions of the same publication.</br>
We expect to see a diff in a convenient way between different versions of the application. Output, for example, can be by calling the API method with parameters - the versions of the document, in response can be a collection of added and deleted parts, for example. You choose the algorithm and the form of the result completely - most importantly, describe your course of thinking. You can use third-party libraries.

# BUILD APPLICATION

docker run --name [mongo-container] -d mongo </br>
docker build -t [image-name] . </br>
docker run -it --name [container-name] -p 8080:8080 --link [mongo-container]:mongo -e random=[RANDOM_MODE] -e pages=[PAGES_NUMBER] [image-name] </br>

[RANDOM_MODE]= (true - включити рандом мод, false - звичайний мод) (про нього далі) </br>
[PAGES_NUMBER]= (<=0 - скрапити всі сторінки, n - скрапити n сторінок)  </br>

## Methodology
Restful API with HATEOAS. Why? It's easy to scale this API and for getting the info you need only one endpoint. Besides, using AOP for the easily scalable handle of exceptions.

I've paralleled scrapping and monitoring, to get the most efficient result of the server. Already after the start of the application, you can test this API. The documents will be added at runtime. It takes around 1m30s for my laptop (MacBook'13 8RAM, 2014) for the server to scrap and save all documents - around 8832.</br>
I've selected MongoDB as DB. Quite fast, flexible in settings, and well suited for storing information in the document's form.</br>
I decided to scrap the site out of the condition: http://brovary-rada.gov.ua/documents/ </br>
When you run the application, all threads initialize the ThreadExecutor class that creates the ScheduledExecutorService - to start streams over the graph.


### RANDOM MODE:
For better and easier testing, the application added random_mode - to create a case for modified documents. With this mod for scrapping documents, every tenth is considered a new version of the document - an existing id is set up;

The application scrapes content of the tag: `<div class="row otstupVertVneshn">`. It stores all important page information. An example of this information for this tag is shown in the image (example.png) </br>
When scrapping documents, their original text (version 0) is stored. When there are changed documents, the Page.java class stores only the changed characters. PageDiff class in the diff field are saved. In the future, to work with text changes, I use the google-DiffMatchPatch library, which quickly relieves changes in the text, and provides a compact text format. An example of a content difference is in the file (example.html).

#### All info about API: </br>
http://localhost:8080/swagger-ui.html - here you can test API. </br>
The UML diagram is (diagram.png)</br>
All information about classes is in JavaDoc</br>
SCALEME.txt contains all additional information for scaling.</br>
For testing flexibility, other environment settings are used. In this case, the database is used on the Heroku, but if necessary, there may be additional specific settings.
