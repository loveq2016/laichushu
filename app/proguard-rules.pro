# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\ASsdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#FBReader»ìÏý start

-keep class org.geometerplus.android.org.geometerplus.android.fbreader.util.** { *; }
-keep class org.geometerplus.android.util.** { *; }
-keep class org.geometerplus.org.geometerplus.android.fbreader.** { *; }
-keep class org.geometerplus.zlibrary.core.application.** { *; }
-keep class org.geometerplus.zlibrary.core.constants.** { *; }
-keep class org.geometerplus.zlibrary.core.drm.** { *; }
-keep class org.geometerplus.zlibrary.core.drm.embedding.** { *; }
-keep class org.geometerplus.zlibrary.core.encodings.** { *; }
-keep class org.geometerplus.zlibrary.core.filesystem.** { *; }
-keep class org.geometerplus.zlibrary.core.filesystem.tar.** { *; }
-keep class org.geometerplus.zlibrary.core.filetypes.** { *; }
-keep class org.geometerplus.zlibrary.core.fonts.** { *; }
-keep class org.geometerplus.zlibrary.core.html.** { *; }
-keep class org.geometerplus.zlibrary.core.image.** { *; }
-keep class org.geometerplus.zlibrary.core.language.** { *; }
-keep class org.geometerplus.zlibrary.core.library.** { *; }
-keep class org.geometerplus.zlibrary.core.options.** { *; }
-keep class org.geometerplus.zlibrary.core.resources.** { *; }
-keep class org.geometerplus.zlibrary.core.tree.** { *; }
-keep class org.geometerplus.zlibrary.core.util.** { *; }
-keep class org.geometerplus.zlibrary.core.view.** { *; }
-keep class org.geometerplus.zlibrary.core.xml.** { *; }
-keep class org.geometerplus.zlibrary.text.model.** { *; }

#FBReader»ìÏý end