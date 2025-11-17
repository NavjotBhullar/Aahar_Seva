Aahar - Food Donation App Version 1.0.0
Aahar is a food donation app built on Java and powered by Firebase. Its primary objective is to facilitate the process of food donation, connecting food donators with receivers in a seamless manner. With Aahar, you can contribute to reducing food waste and helping those in need by donating surplus food items.



<b>Features</b><br>
✅ User Registration & Authentication - Secure email-based registration with verification<br>
✅ Food Donation - Pin your location on map to donate surplus food<br>
✅ Food Receiver - Find nearby food donations with navigation support<br>
✅ Interactive Map - View all available food donations in real-time<br>
✅ Donation History - Track your past contributions<br>
✅ Profile Management - Update profile details, email, and password<br>
✅ Contact Privacy - Optional phone number visibility<br>


Screenshots will be updated with project-specific data before final submission.<br>




<b>Tech Stack</b><br>
Frontend: Java, XML<br>
Backend: Firebase Authentication, Firebase Realtime Database<br>
Maps: OpenStreetMap (OSMDroid)<br>
Image Hosting: External URL storage<br>
Architecture: MVC Pattern<br>



Getting Started
Prerequisites
Android Studio (latest version)
JDK 11 or higher
Firebase account
Android device/emulator (API 24+)
Installation
Clone the repository



git clone <your-repository-url>
Open project in Android Studio



Setup Firebase:

Create a new Firebase project
Enable Authentication (Email/Password)
Enable Realtime Database
Download google-services.json
Place it in app/ directory
Build and run the project



User Registration
To get started with Aahar, register by providing:

Email: Used for account verification and communication
Password: Secure password for account protection
Name: Displayed to other users during food donations
Phone Number: Contact number for donor-receiver communication
Email Verification
After registration, you will receive an email verification link. Click the link to verify your email address and activate your account.



How It Works
Donating Food
Donate Activity:

Pin your location on the map
Enter food item details:
Food Item Name
Food Item Description
Phone Number Visibility (Optional)
Note: If you choose to display your phone number, it will be visible to food receivers for direct contact.



Food Map Activity:
Users can view all available food donations on an interactive map. Click on any pin to see donation details (phone numbers are hidden in this view).



<b>Receiving Food
Receiver Activity</b>:



Browse food pins near your location
Click on a pin to view full details
See donor contact information (if shared)
Navigate to donor's location using Google Maps
Collect the food
Important: Once food is collected, the donor should remove their pin from the map.



<b>Additional Features</b><br>
History Management<br>
View all past food donations<br>
Delete individual donation records<br>
Complete donation history tracking<br>
Account Management<br>
Reset Password: Recover account access<br>
Change Email: Update registered email<br>
Update Profile: Modify name and phone number<br>
Profile Picture: Upload custom profile photo via URL<br>
Privacy Controls<br>
Optional phone number visibility<br>
Secure authentication<br>
Email verification required<br>

<br>
<b>Database Structure</b><br>

Firebase Realtime Database:<br>

├── Registered Users/<br>
│   └── {userUID}/<br>
│       ├── fullName: "User Name"<br>
│       ├── email: "user@example.com"<br>
│       ├── mobile: "1234567890"<br>
│       └── photoUrl: "https://..."<br>
├── FoodMap/<br>
│   └── {donationID}/<br>
│       ├── name: "Donor Name"<br>
│       ├── food: "Food Item"<br>
│       ├── number: "Phone Number"<br>
│       ├── lat: 28.6139<br>
│       └── lng: 77.2090<br>
├── History/<br>
└── DonateIdMapping/<br>
Project Structure<br>
text<br>




app/
├── src/main/java/com/example/aahar100/<br>
│   ├── MainActivity.java<br>
│   ├── sign_up.java<br>
│   ├── landing_page.java<br>
│   ├── Donate.java<br>
│   ├── Receive.java<br>
│   ├── FoodMap.java<br>
│   ├── History.java<br>
│   ├── UserProfileActivity.java<br>
│   ├── uploadProfilePicActivity.java<br>
│   ├── ProfileHelper.java<br>
│   └── ReadWriteUserDetails.java<br>
└── src/main/res/<br>
├── layout/<br>
├── drawable/<br>
└── values/<br>



Contributing
This is a student project developed for educational purposes. Contributions, issues, and feature requests are welcome!<br>

License
This project is developed as part of B.Tech coursework and is available for educational use.<br>

Acknowledgments<br>
Firebase for backend services<br>
OpenStreetMap for map integration<br>
Material Design components<br>
Support<br>
For issues and queries, please create an issue in the repository or contact through the app's feedback system.<br>

Developed as a B.Tech TR-103 Project<br>
By Navjot Bhullar<br>
Version: 1.0.0<br>
Last Updated: 2024<br>
