 This is a combination of Annotation & LifecycleObserver
 
 It's usage is quite simple,first you have to bind the activity in onCreate:
 
 ```javascript
Life.bind(this);
```

then add annotation on the method you want:

 ```javascript
@onCreate()
public void LifeTest() {
Log.e("orz", "sro1");
}
```

then LifeTest will run in onCreate

it also support other annotations,so far it contains:

* onCreate
* onStart
* onResume
* onPause
* onStop
* onDestroy

if you want to reorder the methods in the same cycle,you can set the position like this:

 ```javascript
@onCreate(pos = 1)
public void LifeTest() {
Log.e("orz", "sro1");
}
```

pos's default value is 0,the higher the later.

sorry for poor English,no one may use anyway.