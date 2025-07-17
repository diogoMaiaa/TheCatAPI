Initially, I started by developing the application's graphical interface using Jetpack Compose. I focused on the breed listing, using a grid layout (LazyVerticalGrid) and simple navigation between screens.

After completing the interface base, I implemented the breed listing functionality with manual pagination support and viewing of each breed's details. Subsequently, I developed all the features related to the favorites system, which allows the user to mark/unmark breeds.

At the end of development, I performed unit tests, end-to-end (E2E) tests, and corrected minor errors that were identified.

In order to allow the application to be used offline, all breed data is stored locally through Room. The data is initially loaded and automatically synchronized with the external API every 10 minutes, whenever there is an internet connection.
