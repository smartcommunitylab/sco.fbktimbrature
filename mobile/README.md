## Important
 To make it work and compile, ensure the target Android sdk is 26:
```
target=android-26
```

For development to debug in inspect, add the following to the package.json:
```
  "config": {	
    "ionic_bundler": "webpack",	
    "ionic_source_map_type": "#inline-source-map"	
  },

```