# Stock Photos App 
**[Under development]**  
An Android application that allows its user to browse and download stunning photos.  
Data used are retrieved from [Pexels](https://www.pexels.com/api/)' free stock photos.  
This project is a training in Android programming and is dedicated to personal use.  
It is currently in the development phase.:construction:

## Overview
</br><p align="center">
  <img src="img/app-screenshot.png" alt="Screenshot of the application" height=480/>
</p></br>

The home screen of the app shows a selection of popular photos.  
Photos can be browsed using a search bar.

## Run the app
This intermediate version of the project can be cloned and launched using [Android Studio](https://developer.android.com/studio).  
An ***API key*** must be retrieved from [Pexels' website](https://www.pexels.com/api/?locale=en-US) in order to run the app.  
Just create an account to get one instantly. This step is necessary to allow the application to access the photo database.:key:  
The key has to be copied into the file `api-key.gradle` at the root of the project, just like this :  
`ext.api_key = "your_key_in_quotes"`
