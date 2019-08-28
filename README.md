# CSDTSLOGPROCESSOR

## Build project

To build the project run:

```
./gradlew build
```

## Run the project

Log processor takes log file as input (default path is```log-file.log``` ).

Log processor creates database if not exists. 

Program waits 10 seconds to 

To start application run:
```
./gradlew bootRun
```

To change file path run:

```
./gradlew bootRun --args=--logProcessor.filePath=${FILE_PATH}
```

To clear database run:

```
./gradlew clearDatabase
```

