Aahar - Food Donation App Version 1.0.0
Aahar is a food donation app built on Java and powered by Firebase. Its primary objective is to facilitate the process of food donation, connecting food donators with receivers in a seamless manner. With Aahar, you can contribute to reducing food waste and helping those in need by donating surplus food items.

Features
✅ User Registration & Authentication - Secure email-based registration with verification
✅ Food Donation - Pin your location on map to donate surplus food
✅ Food Receiver - Find nearby food donations with navigation support
✅ Interactive Map - View all available food donations in real-time
✅ Donation History - Track your past contributions
✅ Profile Management - Update profile details, email, and password
✅ Contact Privacy - Optional phone number visibility

Screenshots will be updated with project-specific data before final submission.

Tech Stack
Frontend: Java, XML
Backend: Firebase Authentication, Firebase Realtime Database
Maps: OpenStreetMap (OSMDroid)
Image Hosting: External URL storage
Architecture: MVC Pattern

Getting Started
Prerequisites
Android Studio (latest version)
JDK 11 or higher
Firebase account
Android device/emulator (API 24+)
Installation
Clone the repository
Bash

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

Receiving Food
Receiver Activity:

Browse food pins near your location
Click on a pin to view full details
See donor contact information (if shared)
Navigate to donor's location using Google Maps
Collect the food
Important: Once food is collected, the donor should remove their pin from the map.

Additional Features
History Management
View all past food donations
Delete individual donation records
Complete donation history tracking
Account Management
Reset Password: Recover account access
Change Email: Update registered email
Update Profile: Modify name and phone number
Profile Picture: Upload custom profile photo via URL
Privacy Controls
Optional phone number visibility
Secure authentication
Email verification required
Database Structure
text

Firebase Realtime Database:
├── Registered Users/
│   └── {userUID}/
│       ├── fullName: "User Name"
│       ├── email: "user@example.com"
│       ├── mobile: "1234567890"
│       └── photoUrl: "https://..."
├── FoodMap/
│   └── {donationID}/
│       ├── name: "Donor Name"
│       ├── food: "Food Item"
│       ├── number: "Phone Number"
│       ├── lat: 28.6139
│       └── lng: 77.2090
├── History/
└── DonateIdMapping/
Project Structure
text

app/
├── src/main/java/com/example/aahar100/
│   ├── MainActivity.java
│   ├── sign_up.java
│   ├── landing_page.java
│   ├── Donate.java
│   ├── Receive.java
│   ├── FoodMap.java
│   ├── History.java
│   ├── UserProfileActivity.java
│   ├── uploadProfilePicActivity.java
│   ├── ProfileHelper.java
│   └── ReadWriteUserDetails.java
└── src/main/res/
├── layout/
├── drawable/
└── values/
Contributing
This is a student project developed for educational purposes. Contributions, issues, and feature requests are welcome!

License
This project is developed as part of B.Tech coursework and is available for educational use.

Acknowledgments
Firebase for backend services
OpenStreetMap for map integration
Material Design components
Support
For issues and queries, please create an issue in the repository or contact through the app's feedback system.

Developed as a B.Tech TR-103 Project
By Navjot Bhullar
Version: 1.0.0
Last Updated: 2024