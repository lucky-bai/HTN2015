# Sleep Guardian

##Inspiration
If you’re like me, the first thing you do when you wake up is open up laptop and check Reddit. The last thing you do is check Quora one more time, then you close the lid and fall asleep.

By looking at your browser history timestamps, a chrome extension can figure out when you sleep and wake up! Traditional sleep tracker apps require you to manually tell it when you go to sleep but our software will do it magically.

With this information, we can plot your sleep schedule graphically to the user.

##What it does
Sleep Guardian infers your sleeping history by combining your digital footprints across a plethora of devices:

Chrome extension uploads list of timestamps from browser history
Chrome extension tracks mouse movement to determine when user is active and idle
Android app tracks apps being launched and closed, which signifies user activity
All this information is aggregated by a central server where the user can access his data using a streamlined interface.

##How I built it
The data is collected using multiple channels:
- Chrome extension
- Android OS application

The website is built with:
- Flask
- MongoDB
- Bootstrap
- jQuery

## Challenges I ran into
The biggest challenge for this idea is that different people use their digital devices in different ways before and after sleeping. Hence it was a challenge to make the software robust and work correctly despite all this variation.

## What's next for Sleep Guardian
- iOS integration
- Better analytics
- Gathering data from more sources to increase accuracy
- Sleep coach
- App Store launch
- 
## Try it out
www.sleepguardian.co

## Screenshots

![](http://challengepost-s3-challengepost.netdna-ssl.com/photos/production/software_photos/000/291/489/datas/gallery.jpg)
![](http://challengepost-s3-challengepost.netdna-ssl.com/photos/production/software_photos/000/291/488/datas/gallery.jpg)
![](http://i.imgur.com/q8YbYqD.png)
