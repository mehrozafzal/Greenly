# Greenly
# GreenelyAndroid
[![Build Status](https://travis-ci.com/GreenelyAB/GreenelyAndroid.svg?token=kMEEb3oSqiDqFZTkEY74&branch=develop)](https://travis-ci.com/GreenelyAB/GreenelyAndroid)

## Credentials for build signing.

The credentials to sign the application are stored json files. In *credentials.json.example* you can find an example format. 

What you can do is rename the example file to  *greenely_credentials.json* and then provide proper signing credentials.

The credentials can be also flavor dependant. See module *build.grade* for specifications about the naming
for the different flavors.

## Gitflow

This is our gitflow for adding new features/bugfixes/hotfixes and etc.

https://github.com/GreenelyAB/GreenelyAndroid/wiki/Greenely-and-Gitflow

## Architecture reference

This is an architecture reference which covers main parts of the app

https://github.com/GreenelyAB/GreenelyAndroid/wiki/Android-app-architecture-reference

## Code guidelines
These are guidelines and not rules. We should try to follow them as closely as possible but feel
free to step outside the box if you feel the need to but discuss with the rest of the team if you
decide to do so.
 The code follows a MVVM pattern. The view consists of two parts, the Activity/Fragment and the xml
layout.
 ### The ViewModel
 
The ViewModel should communicate with the XML using databindings. See 
https://developer.android.com/topic/libraries/data-binding/index.html for more information on the 
databinding library.  

 The goal of the ViewModel is to contain as much of the business logic as possible and it should be
testable using PURE java unit tests.

 The ViewModel should avoid having direct references to a Context since this will encurrage you
(the developer) to use android SDK dependencies directly in the ViewModel and this makes it almost
impossible to unit test the viewModel without extensive mocking.

 Avoid putting event handlers like click listeners and similar straight in the ViewModel but rather
try to keep that somewhere else. The reason for that is a lot of the time you need to do other
android related stuff in the events like showing a dialog or similar. Rather create an event handler
interface and have the Activity/Fragment implement it. Then pass this event handler to the xml using
databindings and call the ViewModel from the Activity/Fragment. This way you can have a callback
passed to the ViewModel method and make the View respond to events such as errors.

 ### The Activity/Fragment
The Activity/Fragment should be in charge of instantiating the ViewModel and setting up the 
databindings. It should also handle events such as click events and propagate the actual work to
the ViewModel.
 The Activity/Fragment is also the part that is in charge of navigation so starting new activities
should be done by the Activity.
 ### The XML layout
 XML Layout view names should follow camelCase naming convention. Currently databinding parses snake_case and camelCase view names from XML and provides them as camelCase in our code. However if in the future we would like to switch from databinding to let's say, Kotlin synthetics, then we would have a hard time because it supports only camelCase. Therefore it makes most sense to follow camelCase naming convention for XML views, in order to make sure that we are not creating unnecessary technical debt for future.
 
Other than that, in the XML layout there are not really any strict rules other than common sense. Try to avoid having
too much code in the bindings of the layout. The layout should only layout stuff and not do business
logic related things.
 ### Adapters
Views like a ViewPager or a RecyclerView require adapters in order to draw their stuff. You should
aim towards having a direct reference to the adapters in the ViewModel and pass it to the View
via databindings. 
 The adapters should be instantiated in the Activity/Fragment to be passed to the ViewModel via the 
constructor. This way the adapter can be mocked while unit testing.
 ### API Communication
The goal is that all direct API communication should be handled by the ViewModel. Stuff like errors
and/or responses can be propagated to the Activity/Fragment view callback interfaces such as the
ApiResponseHandler utility class.
 
