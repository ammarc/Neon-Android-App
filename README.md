# Neon-Client
## Introduction
This is the source repository of our android app which allows
users to find each other in relatively small areas like a stadium,
arena etc. Users also have the ability to chat with each other and
send friend requests. Furthermore, the location of users are shared
only when they have allowed others to view their location.

_**Note: the app interfaces with our servers on ports 3000 and 4000,
which maybe blocked by your network admin. If this is the case,
try changing to mobile data or to a different network.**_

## Signing In
The primary method the app uses to sign up users is to use Facebook.
When a user signs up, they are assigned a username so that they
can be identified in our database. This will also allow users to
find their friends.

## Tracking Friends
Ideally, the user will first use the built-in Google Maps to find each other
when they are further away and then they can use the AR which points
them to the person they are trying to find. Note that the arrow points
directly towards the other user (in straight line).

## Pre-requirements
Currently the app runs only through the Android Studio IDE. For detailed
pre-requirements for installting that IDE, go to https://developer.android.com/studio/index.html

## Testing
To run the unit-tests just go the itproject.neon\_client > tests. And then
click right-click > Run <test-name> to run the individual tests.
