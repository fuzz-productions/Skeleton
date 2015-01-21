This library will help you to reduce boiler code, by auto generating view tags for you.
View tag is a class that initialized with layout or a view, and perform all the findViewById() calls, along with proper casting. 

Setup
------
```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.fuzz.skeleton:skeleton:1.0.0.RELEASE'
    }
}
apply plugin: "com.fuzz.skeleton"
```

Usage
-----
Each time the project builds, the resource folder will be cleared and regenerated. Hit Command + F9 for quick build.

Future
------

 1. include tag - Will created a nested view tag, to represent the include
 2. costume views - Costume views are fully supported
 3. multi layout folders - When using several layouts with the same name, a merged view tag will be created, it will include the ids from all the participating layouts.
 4. flavors - All the above supported with flavors too. Even when you have a flavor that define new layout directory, it will be merged with the other resources as expected.

Name convention
---------------
I found this naming style works the best when using the skeleton plugin.

 - Resources names: Separate each word with an underline, it will generate a viewTags classes with proper Java style name convention.
 - Resources ids: Use java parameters name convention for ids. Its fine to have multiple ids with the same name, as now you have direct access to the id 
and you don't need to prefix it anymore.

License
--------

	This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, as well as to the
	Additional Term regarding proper attribution. The latter is located in Term 11 of the License. If a
	copy of the MPL with the Additional Term was not distributed with this file, You can obtain one at
	http://static.fuzzhq.com/licenses/MPL
