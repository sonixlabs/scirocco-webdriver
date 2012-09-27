### Quick Start Scirocco for WebDriver - Eclipse Install Tutorial


##### 1.GET Eclipse
 - Download Eclipse from http://www.eclipse.org/downloads/ 
(Eclipse IDE for Java Developers or Eclipse Classic, version 4.2 or 3.7.2 or 3.6.2)

##### 2.GET Android SDK
- Download Android SDK from 
http://developer.android.com/intl/ja/sdk/index.html
Then unzipped, and save in your workspace.

##### 3.INSTALL ADT
- Activate Eclipse, then select the menu [Help] - [Install New Software...] 
- Click the [Add...] button 
- In the  [Name] field, type the name you want to use to identify this, for example [Android Plugin].
- In the  [Location] field, type [https://dl-ssl.google.com/android/eclipse/].
- Click the [OK] button
- Follow the Wizard instructions and install ADT, then reboot.
 
##### 4.INSTALL Scirocco for WebDriver
- Activate Eclipse, then select the menu [Help] - [Install New Software...] 
- Click the [Add...] button 
- In the  [Name] field, type the name you want to use to identify this, for example [Scirocco for WebDriver].
- In the  [Location] field, type[http://scirocco.sonixlabs.com/update].
- Click the [OK] button
- Follow the Wizard instructions and install Scirocco for WebDriver, then reboot.

##### 5.CREATE the Test Project
- Select the menu [File] - [New] - [Other]
- Open [Scirocco for WebDriver] from the list, and select [Android Test Project], then click the [Next > ] button.
- In the [Project name] field, type the name of your test project.
- In the [Target app package] field, type the name of the package of APK which you will test.(you can change this anytime later)
- Specify the root directory of the Android SDK which you save the [Android sdk path]
- Click the [Finish] button.

##### ６. RUN the test. 
- Create the test class in the test project which you generate, in the proper package.
 (about the program code, refer the sample in the "test.sample package".)
- You can test the Android driver just to write the source code. (you can also use the sample package.）
- For the Native driver, you need to incorporate the library for the Native driver. (library download url => http://scirocco.sonixlabs.com/server-standalone.jar) Also you need to install the APK which you add the notes to the manifest, into the target device which you do the test. (Learn more => http://code.google.com/p/nativedriver/wiki/AndroidMakeAnAppTestable)
The APK file which you install must be one. (If you install multiple APK files, an error will occur.）