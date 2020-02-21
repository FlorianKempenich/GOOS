# GOOS


## A Working Example
This is a working version of the project used an example in [_"Growing Object Oriented Software Guided By Tests"_](http://www.growing-object-oriented-software.com/) by [Nat Pryce](https://twitter.com/natpryce) and [Steve Freeman](https://twitter.com/sf105).

Given that the book is starting to show its age, it wasn't straightforward to have a working version of this project. Old versions of the libraries were having problem with the newer versions of Java, build system was using Ant, there wasn't much information on how to configure Openfire, etc... But thanks to [@skinny85](https://github.com/skinny85) and his similar project [goos-book-code](https://github.com/skinny85/goos-book-code) I was able to figure it out 😊

## Differences From The Book
My version is slightly different than the one from the book
- It uses `Mockito` instead of `jMock`
- It uses `JUnit Jupiter (JUnit 5)`
- It uses `eJabberd` instead of `Openfire`
  - Docker based, so no config needed
  - There are 2 scripts which are sufficient to get a working E2E test environment:
    - `./ejabberd/start.sh`
    - `./ejabberd/prepare_for_end_to_end_tests.sh`
- It uses the latest version of the Smack library, the API was updated quite a bit
- It offers 2 gradle tasks for tests to allow to run independently Unit tests from E2E tests
  - `./gradlew test`
  - `./gradlew testE2E`
