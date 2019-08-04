# Setting Up A Local Development Environment

## Prerequisites

Just have a recent (>= 3.2) version of Android Studio.
Gradle will take care of the rest for you.

## Backend

Since the DEvid Android application uses that same backend server as its PWA
pendant, you will need to clone the
[DEvid](https://git.sandtler.club/sandtler/devid) repository and follow its
[setup guide](https://git.sandtler.club/sandtler/devid/blob/master/doc/dev-setup.md)
first, at least everything related to the backend part.

### SSL Proxy

After you have your backend server running, you will need an HTTPS proxy because
the Android Framework prohibits plain HTTP traffic.  The recommended software to
use is the nginx web server, which will also power the actual production
environment.  Please refer to the documentation on how to set it up as a
[reverse HTTP proxy](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)
as well as how to
[use SSL](https://docs.nginx.com/nginx/admin-guide/security-controls/terminating-ssl-http/#setting-up-an-https-server).

## App Configuration

The base URLs for both backend and CDN can be configured by **copying** the file
`build-config.default.properties` to `build-config.properties` and changing the
URLs accordingly.  The CDN specified in the default file is already working and
may be used for testing purposes (it hosts only one movie at the moment, see
<https://cdn.devid.sandtler.club/video/5d1d2339e710560cdf5c5b80>).  You may also
configure your nginx reverse proxy to serve static video files under a separate
virtual host, of course.
