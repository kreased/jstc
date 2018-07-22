# jstc

Tech challenge: Given an Amazon product ASIN (a unique identifier amazon uses for its products), build an application that can fetch the category, rank &amp; product dimensions of that product on Amazon, store that data in some sort of database, and display the data on the front-end. For example, the details for ASIN "B002QYW8LW" can be found here www.amazon.com/dp/B002QYW8LW .

You're probably going to think the best solution for the challenge would be to use Amazon's Product API & you're right - but registering for that is a mission! The API isn't available and you will need to figure out an alternative method :)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

Must have java installed

### Installing

1. Clone this repo to your local host
2. Build and run - will start service on http://localhost:8080 

```
./gradlew build && java -jar build/libs/harasemc-jstc-0.1.0.jar
or
./gradlew bootRun
```

3. Go to http://localhost:8080/productDetails/B002QYW8LW - try putting in different Amazon ASINs in the URL

## Running the tests

```
./gradlew test
```

## Authors

* **Chris Harasemchuk** - *Initial work* - [kreased](https://github.com/kreased)

## Acknowledgments

* Took some inspiration from [alessiovierti's amazon-item-lookup](https://github.com/alessiovierti/amazon-item-lookup) for usage of jsoup to do parsing, a head start on parsing some properties, and using the user-agent property to get real data. 

## Development notes & Design choices

* Used LMDB as persistent caching layer on top of scraper mostly due to my familiarity with lmdb-java and ease of getting it working. 
* Chose to represent items as essentially an ASIN plus a "bag of properties" (using enum as key) - easy to add other properties this way without affecting compatibility
* In the interests of time:
  * I've skimped on logging/metrics, fancy UI, & detailed error handling. I've provided many unit tests, but left out a handful of cases. 
  * All properties are strings - not going to try doing things like ints for rank, or normalizing dimensions, or things like that
  * Didn't implement DB cache removal/expiration, or any other fanciness on top of the DB
* Given that this is essentially a scraper, and that there are a variety of ways in which Amazon displays product data on its website, there are no guarantees that all ASINs will work - it's only as good as the sample inputs I threw at it during testing (see the unit tests for ScrapingProductDetailsLoader)
  * In fact, some ASINs don't provide all info in a cleanly scrapable way (ie: B01GEW27DA - Amazon Fire tablet) - when the app encounters stuff it can't get, it handles it as gracefully as it can, displaying UNKNOWN in the UI.
  * I didn't encounter this during my tests, but I'm pretty sure if you use this a lot, it'll break when Amazon starts serving up captchas instead of real content.
* Scraping code is isolated to one class, which can probably be easily substituted out for the real product API without much fuss
