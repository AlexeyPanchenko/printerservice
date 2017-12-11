# printerservice
Simple implementation standart android print API for custom documents

## dependencies
In the root build.gradle
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
In the project build.gradle
```groovy
compile 'com.github.alexeypanchenko:printerservice:0.0.2'
```
