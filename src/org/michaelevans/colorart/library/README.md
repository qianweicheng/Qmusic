About

ColorArt is a library that uses an image to create a themed image/text display. It's a port of the idea found on the Panic Blog to work on Android.

Usage

(ColorArt is supported on Android 2.1+.)

Add ColorArt as a dependency to your build.gradle file:

compile 'org.michaelevans.colorart:library:0.0.2'﻿
Then you can use the library like this:

// get a bitmap and analyze it
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.album);
ColorArt colorArt = new ColorArt(bitmap);

// get the colors
colorArt.getBackgroundColor()
colorArt.getPrimaryColor()
colorArt.getSecondaryColor()
colorArt.getDetailColor()
FadingImageView

mFadingImageView.setBackgroundColor(colorArt.getBackgroundColor(), FadingImageView.FadeSide.LEFT);
This will set the fading edge on the left side, with that background color. You can also enable/disable the fade with:

mImageView.setFadeEnabled(true/false);