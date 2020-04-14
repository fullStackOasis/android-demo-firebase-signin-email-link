# Demo using Firebase to sign in with an emailed link

This app has two screens. The initial screen is a sign-in page where the user enters their email address.

After hitting the SIGN IN button, Firebase is used to send an email with a clickable link to their email address.

The user then navigates to their email client in the device, opens the email, and clicks on the link. They can choose to open the link either using a web browser or the app. When choosing the app, the second page opens, showing that they are signed in.

Using Firebase is tedious, to be honest. Personally, I got numerous unexplained errors while adding Firebase to my demo app. I believe that some of these errors were caused because Firebase just needed some time for my setup changes to propagate throughout their system. So it may be that you should follow the setup steps, then go take a break before trying to set up authentication in your app.

Notice that I've used "dynamic linking" to create deep links to my app. There is [more info about deep linking here](https://developer.android.com/training/app-links/deep-linking).

Here are a couple of tips to get started:

First, [add Firebase to your project](https://firebase.google.com/docs/android/setup)

Note: I added google-services.json to my .gitignore file. [YMMV according to StackOverflow](https://stackoverflow.com/questions/37358340/should-i-add-the-google-services-json-from-firebase-to-my-repository).

Second, follow the [steps to authenticate via an email link using Firebase](https://firebase.google.com/docs/auth/android/email-link-auth).

Screenshot of the app after the user has signed in:

![User signed in](https://github.com/fullStackOasis/android-demo-firebase-signin-email-link/raw/master/demo-firebase-email-link-sign-in.png)

Screenshot of the app when the user is at the sign-in screen:

![Sign in screen](https://github.com/fullStackOasis/android-demo-firebase-signin-email-link/raw/master/demo-firebase-sign-in-screen.png)
