## Emmagee - a practical, handy performance test tool for specified Android App

Emmagee is a practical, handy performance test tool for specified Android App, which can monitor CPU, memory, 
network traffic, battery current and status([Some devices are not supported](https://github.com/NetEase/Emmagee/wiki/Some-devices-are-not-supported)), new features such as top activity and heap size if rooted([Root Toast may continously show](https://github.com/NetEase/Emmagee/wiki/FAQ)), are also supported in the [latest version](https://github.com/NetEase/Emmagee/releases). Additionally, it also provides several cool features such as customizing interval of collecting data,
rendering real-time process status in a floating window, and much more.

 * Homepage: https://github.com/NetEase/Emmagee
 * Wiki: https://github.com/NetEase/Emmagee/wiki
 * Issues: https://github.com/NetEase/Emmagee/issues
 * FAQ: https://github.com/NetEase/Emmagee/wiki/FAQ
 * Tags: Android, Java 

<img src="https://github.com/andrewleo/pictures/blob/master/Emmagee/V2.0/homepage.png" width="180px" />&nbsp;<img src="https://github.com/andrewleo/pictures/blob/master/Emmagee/V2.0/settings.png" width="180px" />&nbsp;<img src="https://github.com/andrewleo/pictures/blob/master/Emmagee/V2.0/mailsettings.png" width="180px" />

## Why should I use Emmagee?

Unlike most other performance test tools that only do system-level monitoring, Emmagee provides the ability to monitor any single App. Other advantages that
you should not miss:
* Open source
* Easy to use
* Process-specific monitoring, including CPU, memory, network traffic, battery current, launching time and status
* Floating window that renders real-time process status
* CSV format report that can be converted into any other format you want
* User-defined collecting interval
* Fully support Android 2.2 and above

## How to use Emmagee?

First of all ,you should have Emmagee.apk,download [here](https://github.com/NetEase/Emmagee/releases) or 
build the apk file youself [here](https://github.com/NetEase/Emmagee/wiki/How-to-build-emmage.apk%3F),then :

1. Start Emmagee App
2. Configure interval
3. Select a target process 
4. Click Start button

And Enjoy!

If you want to stop the test, just go back to Emmagee and click Stop button.

## Android 5.0 and above

* `Android 5.0 and above`: getRunningTasks() and getRunningAppProcesses() are deprecated and only return your application process, so it is unable to get TopActivity from Android 5.0.
* `Android 7.0`: Google has restricted access to /proc, and also can not get pid of target application from TOP command in Android 7.0, I am so sorry to tell that 7.0 can not be supported.

## Coming Soon
* We want you to decide!

## How to Contribute?

You are welcome to contribute to Emmagee, meanwhile you'd better follow the rules below

* It's *NOT* recommended to submit a pull request directly to Emmagee's `master` branch. `develop` branch is more appropriate
* Follow common Java coding conventions
* Put all Java class files under *com.netease* package
* Add the following [license](#license) in each Java class file

## Contributors
* NetEase, Inc.
* [yrom](https://github.com/yrom)
* [LukeOwncloud](https://github.com/LukeOwncloud)

## License
(The Apache License)

Copyright (c) 2012-2015 NetEase, Inc. and other contributors

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
