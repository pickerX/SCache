# SCache
A Super Cache anim to cache data easily for android to avoiding ANR, replacing SharedPreference in some place

## Usage
### Get & Set
get and set cache like that,

    SCache.get(key:String)

    SCache.put(key:String, value:String, default:String?)

### Observer(In progress...)
Support live data observer

    SCache.get(key:String).observer { value->
        // notify when data of key has changed 
    }

more detail see sample

