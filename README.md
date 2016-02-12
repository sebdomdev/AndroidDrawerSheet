# AndroidDrawerSheet

Google calls it navigation drawer or bottom sheet: This drawer sheet can be opened from the left, right, top, and bottom and is displayed on top of the rest of the content.

## Screenshots

![alt tag](https://cloud.githubusercontent.com/assets/12089383/12999742/66264a0a-d107-11e5-8f94-14de8c301429.png)
![alt tag](https://cloud.githubusercontent.com/assets/12089383/12999744/6627d2a8-d107-11e5-9a96-4e32476e502e.png)
![alt tag](https://cloud.githubusercontent.com/assets/12089383/12999741/66210b6c-d107-11e5-987a-183d627e65bc.png)
![alt tag](https://cloud.githubusercontent.com/assets/12089383/12999743/6627aa26-d107-11e5-8608-2a80300e1650.png)
![alt tag](https://cloud.githubusercontent.com/assets/12089383/12999745/6628b560-d107-11e5-9b1d-e7f331501ef6.png)
[![ScreenShot](https://cloud.githubusercontent.com/assets/12089383/12999746/662c84e2-d107-11e5-840b-f5ba2ffc0951.png)](https://youtu.be/LT7Mb0yxv2Y)

## Code Example

In your layout xml file:
```xml
<RelativeLayout...>
...content that's outside of the drawer...
<de.mxapplications.androiddrawersheet.AndroidDrawerSheet
        android:layout_width="400dp"
        android:layout_height="match_parent"
        app:drawerOffset="48dp"
        app:drawerAlignment="right"
        android:id="@+id/right_drawer">
        ...content that's inside the drawer...
    </de.mxapplications.androiddrawersheet.AndroidDrawerSheet>
</RelativeLayout>
```
**Important: The AndroidDrawerSheet has to be inside of a RelativeLayout!!!**

## Installation

###1. Gradle dependency (JCenter)
Add the following to your build.gradle:
```gradle
compile 'com.github.sebdomdev:android-drawer-sheet:1.0'
```
###2. Maven dependency (JCenter)
Add the following to your pom.xml:
```maven
<dependency> <groupId>com.github.sebdomdev</groupId> <artifactId>android-drawer-sheet</artifactId> <version>1.0</version> <type>pom</type> </dependency>
```

## Optional Settings

####In your layout xml file:

Set the **alignment** of the drawer which is on of top, bottom, left, or right:
```xml
app:drawerAlignment="right"
```

Set the **offset** in pixels. The offset determines how much of the drawer is visible when the drawer is closed:
```xml
app:drawerOffset="48dp"
```

Set if the is **offset is visible** or not. If it is visible, the drawer is partially visible even when closed.
How much the drawer is visible depends on the offset (see setOffset(int)).
If the offset is invisible, the drawer can still be dragged to open but it is not visible when closed:
```xml
app:drawerOffsetInvisible="true"
```

Set the **minimum closing size** in dp. This size determines how far the drawer has to be closed by the user, before it completely closes when the user releases it:
```xml
app:minimumClosingSize="200dp"
```

Set the **minimum opening size** in dp. This size determines how far the drawer has to be opened by the user, before it completely opens when the user releases it:
```xml
app:minimumOpeningSize="200dp"
```

Set **sticky drag** enabled or disabled. If sticky drag is enabled, the drawer will close completely, if the user releases the drawer after dragging it and the drawer is less then half open.
The drawer will open completely, if the user releases the drawer after dragging it and the drawer is more then half open.
If stick drag is enabled, minimum closing size and minimum opening size are ignored:
```xml
app:stickyDrag="true"
```

####In your code:
```java
AndroidDrawerSheet rightDrawerSheet = (AndroidDrawerSheet)findViewById(R.id.right_drawer);

//Open the drawer.
rightDrawerSheet.openDrawer();

//Close the drawer.
rightDrawerSheet.closeDrawer();

//Toogle the drawer, i.e. open it if it's closed and close it if it's open
rightDrawerSheet.toggleDrawer();

//Set the alignment of the drawer which is on of ALIGNMENT_TOP, ALIGNMENT_LEFT, ALIGNMENT_RIGHT, or ALIGNMENT_BOTTOM.
rightDrawerSheet.setAlignment(AndroidDrawerSheet.ALIGNMENT_RIGHT);

//Set the offset in pixels. The offset determines how much of the drawer is visible when the drawer is closed.
rightDrawerSheet.setOffset(200);

//Set if the is offset is visible or not. If it is visible, the drawer is partially visible even when closed.
//How much the drawer is visible depends on the offset (see setOffset(int)).
//If the offset is invisible, the drawer can still be dragged to open but it is not visible when closed.
rightDrawerSheet.setInvisibleOffset(true);

//Set the minimum closing size in pixels. This size determines how far the drawer has to be closed by the user, before it completely closes when the user releases it.
rightDrawerSheet.setMinimumClosingSize(200);

//Set the minimum opening size in pixels. This size determines how far the drawer has to be opened by the user, before it completely opens when the user releases it.
rightDrawerSheet.setMinimumOpeningSize(200);

//Set sticky drag enabled or disabled. If sticky drag is enabled, the drawer will close completely, if the user releases the drawer after dragging it and the drawer is less then half open.
//The drawer will open completely, if the user releases the drawer after dragging it and the drawer is more then half open.
//If stick drag is enabled, minimum closing size and minimum opening size are ignored.
rightDrawerSheet.setStickyDrag(true);
 ```

## MIT License

Copyright (c) 2016 Sebastian Dombrowski

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
