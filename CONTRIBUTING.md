# DEvid Android Contribution Guide

First things first: I would like to humbly thank you for wanting to support the
DEvid project.  You’re awesome!

## Environment Setup

Before doing anything, please follow the setup guide found
[here](https://git.sandtler.club/sandtler/devid/blob/master/doc/dev-setup.md).
This will walk you through setting up the backend server used by the app.
When you have everything ready, start the server to make sure everything is
working properly.

You will need a recent version of Android Studio, along with the latest SDK
(Gradle will make you install all dependencies automatically anyways).
When Gradle has finished making your entire PC freeze for 5 minutes straight,
you need to change the backend URL in
`club.sandtler.devid.lib.Constants.URLPaths#BACKEND_ROOT`.

Also note that Android requires you to use HTTPS rather than plain HTTP, meaning
you will probably want to set up nginx or something comparable as a reverse
proxy to the backend.  You won't need to worry about certificates because debug
builds have SSL checks disabled by default.
