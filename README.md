# SCache
A Super Cache anim to cache data easily for android to avoiding ANR, replacing SharedPreference in some place.  
Inspired by ACache

## Feature
+ Memory cache
+ File IO cache(like SharedPreference)

## Usage
### Get & Set
get and set cache like that,

    // IO Cache
    val io = Caches.io(context)
    // Memory cache
    val cache = Caches.get()

    io.put("int", 11)

    val int = io.getInt("int")

more functions you can check the [SCache interface](https://github.com/pickerX/SCache/blob/master/scache/src/main/java/com/pickerx/scache/SCache.kt) 

### Observer(In progress...)
Support live data observer

    SCache.get(key:String).observer { value->
        // notify when data of key has changed 
    }

more detail see sample

