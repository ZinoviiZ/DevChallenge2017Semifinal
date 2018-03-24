# DevChallengeSemifinal
Application for DevChallenge2017 semifinal, backend nomination, middle-senior category. </br>
With that project I've passed semifinal and have been chosen for final. </br>
https://devchallenge.it/ </br>

## DevChallenge
All-Ukrainian software development championship among junior, middle and senior programmers. The championship takes place in 3 stages: Qualification, Semifinal and offline Finals in Kyiv. Participants compete in eleven nominations in three directions: Web, Mobile, Game. Each nomination has 2 categories: Pro (Senior, Middle-to-Senior) and Standard (Middle, Junior-to-Middle). At the final, the tasks were from the partner Amazon.

# BUILD APPLICATION

docker run --name [mongo-container] -d mongo </br>
docker build -t [image-name] . </br>
docker run -it --name [container-name] -p 8080:8080 --link [mongo-container]:mongo -e random=[RANDOM_MODE] -e pages=[PAGES_NUMBER] [image-name] </br>

[RANDOM_MODE]= (true - включити рандом мод, false - звичайний мод) (про нього далі) </br>
[PAGES_NUMBER]= (<=0 - скрапити всі сторінки, n - скрапити n сторінок)  </br>

## Methodology
Restfull API із використанням hateoas. Чому? Легко маштабоване апі, та для отримання інформації потрібна тільки одна точка входу.
Також використовую AOP для легкомаштабованого керування помилковими ситуаціями.

Всю роботу скрапінга, та моніторинга розпаралелив, щоб отримати максимально швидкий результат роботи серверу. Відразу після старту додатку можна тестити апі - документи будуть добавлятись в рантаймі. Всі документи (наразі близько 8832) сервер скрапить і зберігає за ~ 1 хв 30 с. </br>
БД обрав MongoDB. Досить швидка, гнучка в налаштуваннях, та добре підходить для збереження інформації у вигляду документів.</br>
Вирішив скрапити сайт із умови: http://brovary-rada.gov.ua/documents/ </br>
При запуску додатку всі потоки ініціалізує клас ThreadExecutor який створює ScheduledExecutorService - для запуска потоків по графіку. 


### RANDOM MODE:
Для кращого та легшого тестування додатку добавив random_mode - для створення випадку змінених документів. Із цим модом при скрапінгу документів, кожний десятий вважається новою версією документа - встановлюється існуючий id; </br>

Додаток скрапить весь контент тега <div class="row otstupVertVneshn"> - в ньому зберігається вся важлива інформація сторінки. Приклад цього інформації цього тега показаний в картинці {root}/example.png </br>

При скрапінгу документів зберігається їхній оригінальний текст (версія 0). При знаходжені змінених документів, зберігаються тільки символьні зміни в класі Page.PageDiff в полі diff. В подальшому для роботи із змінами текста я користуюсь бібліотекою від google - DiffMatchPatch яка досить швидко відсідковує зміни в тексті, та надає компактний формат різниці текстів. Приклад різниці контенту лежить в файлі {root}/example.html

#### All info about API: </br>
http://localhost:8080/swagger-ui.html - тут же можна їх і протестити. </br>
Uml діаграма лежить в {root}/diagram.png </br>
Вся інформація щодо класів лежить в {root}/JavaDoc </br>

Для гнучкості при тестуванні використувається інші налаштування середовища. В даному випадку використовуєтсь БД яка лежить на heroku, але при необхідності там можуть бути додаткові специфічні налаштування.
