# printerservice
Simple implementation of standard android print API for custom documents

## dependencies
In the root project build.gradle
```
allprojects {
   ...
    repositories {
        ...
        maven {
            url  "https://dl.bintray.com/alexeypanchenko/maven"
        }
    }
}
```
In the app module build.gradle
```groovy
compile 'com.github.alexeypanchenko:printerservice:0.0.2'
```
