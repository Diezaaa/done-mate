# Done Mate

An offline-first task management app with motivating elements.

## Demonstration Videos

Note: the quick demonstration video doesn't show the whole app functionality.

Quick demonstration video: https://youtu.be/uwmiUMOx60w?si=d-fuB3J8I7QNXDK6  

Full demonstration video: https://youtu.be/AjwqRIbyQik?si=fJpnQq7UIXohyS4L

## Screenshot

The following image is featuring the Tasks and Inspiration screens that are part of the app's screens:

![Two of the main screens in the app](https://i.ibb.co/zTRCcYC3/image.png)

## Features

- Account system: an account can be created using email and password or by linking a Google account. It's possible to link Google or email and password to an existing account. The user can log out or delete his account. In addition, the user can change his password and email. The user can reset his password using a link that is sent to his email. The user must verify email or link a Google account in order to use the app.

- Task management: the user can create\edit\delete a task and access it offline and on other devices. A task consists of a title, description, progress phase (not started, in progress, or completed), due time, reminder time, and an image. The user can sort tasks by criteria like reminder time. It's possible to filter tasks by progress phase, to search tasks by title\description, and to archive a task so it doesn't appear among other tasks on the tasks screen.

- Custom camera interface: capturing an image using the camera of the device is done on a custom-built screen.

- Reminders: the user can set reminders for tasks. A reminder will appear as a notification on the specified date and time.

- Inspiration screen: an endless scrollable feed of motivational quotes that are also accessible offline. The user can like quotes he likes and access the liked quotes on other devices.

- Appearance: choosing between a dark or light theme.

Note: the inspiration screen relies on getting motivational quotes from a third-party/external API (server). So, sometimes this server might be down.

Note: tasks and quotes syncing across devices is done on the Tasks and Inspiration screens, respectively.

Note: to sign up, you must choose a 6+ character password. 

## Installation

Note: The app was tested on a physical phone (Google Pixel 7) and on an emulated phone (Google Pixel 10 Pro) that are both running Android 17.

Note: It's recommended to run the app on Google Pixel 10 Pro or Google Pixel 7 running Android 17.

Note: The user cannot write using Hebrew in the app. Only English typing was tested.

### Link to the app as an installable file on a phone

Link to Google Drive: https://drive.google.com/drive/folders/1zkqrfQI2WlbRyjgj1tr_K5FI07i0PZ_U?usp=sharing

## Tech Stack

- Kotlin

- Jetpack Compose

- Dagger Hilt

- Jetpack Navigation 3

- Room

- Retrofit

- CameraX

- Firebase Firestore

- Firebase Authentication

- Firebase Storage

- Jetpack DataStore

- Git & GitHub

- Material Design 3

- Coil

- Firebase Local Emulator Suite

- Paging 3

## Architecture

The project follows a layered architecture. The data layer exposes the data of the app to the rest of the app using repositories. It also contains the main business logic. The UI layer serves as the primary point of user interaction and displays the application data on the screen. The app also contains a domain layer that contains logic that is used by multiple ViewModels.

UI (Jetpack Compose)

  :arrow_down:

ViewModel

  :arrow_down:

        

Use Cases (Domain Layer)     

  :arrow_down:

        

Repository

  :arrow_down:

        

Data Sources

In order to simplify providing dependencies and follow current industry standards, I used a dependency injection library called Dagger Hilt.

The UI layer follows the UDF pattern (Unidirectional Data Flow) that makes debugging easier.

The app is fully offline-first. The user is able to create/edit/delete tasks while offline, and when the device is online, the changes will be synced. Also, all the liked quotes are synced with other devices. The offline-first approach was implemented using Room (for local persistence), Firebase Firestore (for storing data remotely), and Firebase Storage (for storing tasks' images remotely; Firebase Firestore isn't suitable for storing images).

The project is a single activity application. Navigation is done using Jetpack Navigation 3.

Firebase Local Emulator Suite was used to emulate all Firebase services while developing the app in order to protect the data in the production environment (the data in the Firebase Console).

## Implementation Highlights

- The app's UI was built entirely with Jetpack Compose because it's the latest industry standard for building mobile UI. XML wasn't used at all in this project for building the UI.

- I built a custom image capturing screen using the CameraX library (a library made by Google). The following image features this screen:

![task image capturing screen](https://i.ibb.co/4RmZjCRn/Whats-App-Image-2026-09-10-at-12-45-16-6-border.jpg)

- The app asks for camera permission to capture images for tasks. In addition, alarm and reminder permission and notification permission are requested in order to remind the user about his tasks.

- Not even one single line of code in Java was written. The project uses Kotlin, coroutines, and flows.

- The app features an endless feed of motivational quotes that is also offline-first, meaning that the quotes are persisted in the local storage and it's possible to view them offline. This is done using the Paging3 library for getting pages of quotes, Retrofit for requesting the data from the server, and Room for persisting the quotes on the device and viewing them in offline mode.

- The authentication system was made using Firebase Authentication.

- The project injects dependencies using Hilt, which is the industry standard nowadays. No manual DI was used.

- Navigation was fully built with Jetpack Navigation 3. The third version of Jetpack Navigation is the latest version of Jetpack Navigation. At the beginning of the project, the navigation was built using Jetpack Navigation 2.

- There are screens that use the LCE (Loading/Content/Error) approach, e.g., tasks and inspiration screens. 

- I implemented error messages on some screens, e.g., an error message appears in the Inspiration screen if the quotes API is down.

## Known Issues

There are some known issues that need to be addressed in future updates.

- The user cannot write using Hebrew in the app. Only English typing was tested.

- If the system language is set to Hebrew, the text in the app might be displayed incorrectly.

- Changing password while using a slow internet connection causes logging out from the account.

- A task isn't deleted after deleting it and immediately changing its progress using a notification.

- The camera's preview becomes black after revoking the camera permission. In addition, the user isn't redirected to the task form.

- Capturing an image while the device is in landscape mode causes the image to be incorrectly rotated.

- Signing out with unsynced tasks/quotes causes deletion of these unsynced items.

- If the time on the phone isn't set automatically, tasks and quotes sync might work incorrectly
