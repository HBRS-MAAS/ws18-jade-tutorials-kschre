[![Build Status](https://travis-ci.org/HBRS-MAAS/ws18-jade-tutorials-kschre.svg?branch=master)](https://travis-ci.org/HBRS-MAAS/ws18-jade-tutorials-kschre)

# Notes

In Start.java you can modify numberOfBuyerAgents to simulate more or less buying agents.
Books.java can be used to add or remove book titles.
Currently there are 20 buyers, 4 sellers, 5 different book titles and 20 total copies (there is an infinite amount of eBooks). All sellers have 2 books and 2 eBooks that all are different. Just run Start.java and you can see the transactions.


# Jade Tutorials

Make sure to keep this README updated, particularly on how to run your project from the **command line**.


## Dependencies
* JADE v.4.5.0
* Java 8
* Gradle

## How to run
Just install gradle and run:

    gradle run

It will automatically get the dependencies and start JADE with the configured agents.
In case you want to clean you workspace run

    gradle clean

## Eclipse
To use this project with eclipse run

    gradle eclipse

This command will create the necessary eclipse files.
Afterwards you can import the project folder.
