# HiFood

![Mockup-Images](https://github.com/Explore-In-HMS/Hi-Food/blob/master/app/src/main/res/raw/mock.png)

# :notebook_with_decorative_cover: Intruduction

HiFood is a reference application for HMS kits for phones running with the android-based HMS service. It provides calorie tracking and suggesting recipe according to your foods which are recognized by taking photo. It is developed by kotlin language.

[Cloud DB](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-clouddb-introduction) service is impelemented by repository pattern. Recipes, barcodes and user informations are stored on Cloud Db synchronously.

# :iphone: Features List and Screens

## Auth Service- Account Kit
If I want to login to the application as a user, I can create a profile by specifying username and password, or I can log into the application with an easy and fast authentication using Huawei Login.

As a user, I can use the application as a visitor with anonymous login without creating an account.
to complete the account creation, I should fill in the age, height, weight and date of birth information required to calculate the daily calorie needed and log into the application.


![Auth](https://github.com/Explore-In-HMS/Hi-Food/blob/master/app/src/main/res/raw/auth.gif)

## Recognize food on the table
On the main screen of the app, I can search for any recipe or scan the ingredients I have, to list which recipes are more suitable.
In the recipe I searched or scanned for, I can read the recipe or listen via text-to-speech feature or  watch the video of the recipe with the in-app purchase service and also I am able to add recipe to my list. 


![recognize](https://github.com/Explore-In-HMS/Hi-Food/blob/master/app/src/main/res/raw/recognize.gif)

## Scan barcode to find convenience food
To record convenience food to daily summary, I scan the barcode of it and see the details. Then, I can add it to my diary for calory calculation.

![scan](https://github.com/Explore-In-HMS/Hi-Food/blob/master/app/src/main/res/raw/scan.gif)


## Calculate needed calories 
I can view the distribution and summary of my calorie account according to nutritional values in the profile menu.

![Needed Calories](https://github.com/Explore-In-HMS/Hi-Food/blob/master/app/src/main/res/raw/summary.jpg)


## Records food users eats - summarize calories info daily
When I add the recipe to my list, by specifying the portion I eat and ensure that the nutritional values are used in my daily calorie account.

![Needed Calories](https://github.com/Explore-In-HMS/Hi-Food/blob/master/app/src/main/res/raw/adding.gif)

 
## Show nearby markets on map 
If there is an ingredient I need to buy according to the recipe, nearby markets displays on the map. 

![map](https://github.com/Explore-In-HMS/Hi-Food/blob/master/app/src/main/res/raw/map.gif)

## Text to speech
I can even listen the recipe details by text to speech.

![ttts](https://github.com/Explore-In-HMS/Hi-Food/blob/master/app/src/main/res/raw/recipes.gif)

## Video watch - IAP
Watch the video of the recipe with the in-app purchase service

![iap-video](https://github.com/Explore-In-HMS/Hi-Food/blob/master/app/src/main/res/raw/iap.gif)

 ## :question: What You Will Need 

**Hardware Requirements**

- A computer that can run Android Studio.
- An Android phone for debugging.

**Software Requirements**

- Android SDK package
- Android Studio 3.X
- HMS Core (APK) 4.X or later


# :wrench: Huawei Kits and Services Used

- [Auth Service](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-auth-service-introduction) - Huawei ID, Anonymous
- [Map Kit](https://developer.huawei.com/consumer/en/hms/huawei-MapKit)
- [Site Kit](https://developer.huawei.com/consumer/en/hms/huawei-sitekit) - Nearby Place Search
- [Location Kit](https://developer.huawei.com/consumer/en/hms/huawei-locationkit)
- [ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit) - Image Classification, Text To Speech (TTS) 
- [Scan Kit](https://developer.huawei.com/consumer/en/hms/huawei-scankit) 
- [In-App Purchases](https://developer.huawei.com/consumer/en/hms/huawei-iap/) 
- [Cloud DB](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-clouddb-introduction) 

# :wrench: Libraries
- Navigation

 ## :question: Before You Start 
 **You need to agconnect-services.json for run this project correctly.**

- If you don't have a Huawei Developer account check this document for create; https://developer.huawei.com/consumer/en/doc/start/10104
- Open your Android project and find Debug FingerPrint (SHA256) with follow this steps; View -> Tool Windows -> Gradle -> Tasks -> Android -> signingReport
- Login to Huawei Developer Console (https://developer.huawei.com/consumer/en/console)
- If you don't have any app check this document for create; https://developer.huawei.com/consumer/en/doc/distribution/app/agc-create_app
- Add SHA256 FingerPrint into your app with follow this steps on Huawei Console; My Apps -> Select App -> Project Settings
- Make enable necessary SDKs with follow this steps; My Apps -> Select App -> Project Settings -> Manage APIs
- For this project you have to set enable Map Kit, Site Kit, Auth Service, ML Kit, Scan
- Than go again Project Settings page and click "agconnect-services.json" button for download json file.
- Move to json file in base "app" folder that under your android project. (https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/69407812#h1-1577692046342)
- Go to app level gradle file and change application id of your android project. It must be same with app id on AppGallery console you defined.

# :link: Useful Links

[Huawei Developers Medium Page](https://medium.com/huawei-developers)
[Huawei Developers Forum](https://forums.developer.huawei.com/forumPortal/en/home)

# :notebook_with_decorative_cover: Contributors
Busra Gungor 
Hakan Erbas
