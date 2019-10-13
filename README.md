# spookyLibrary
a Library that contains wall functions to help with mm script engine

## installation

download the latest [release](https://github.com/spookyGh0st/spookyLibrary/releases) and inflate the zip into your scripts folder. Your script folder should look something like this:

![scriptfolder: examplefiles, spookyLibrary](https://i.imgur.com/rzS0Uce.png)

## Usage

import the library into your script with

```const spookyLib = require("./spookyLibrary/spookyScripts");```

use functions from the library with

```spookylib.<functionName>```

The Library works with SpookyWalls, a diffferent way to define a wall. It defines 1 = one line Index and the center = 0. 
Everything else scales accordingly.

![1 height= 1 width](https://i.imgur.com/Uz7aIDg.png=100x100)

To create a SpookyWall from a given Wall use use
```
let wall = <providedwall>
let spookyWall = spookyLib.SpookyWall_init_za3rmp$(walls[i]);
```

you can also just create a SpookyWall from scratch.

```let spookyWall = spookyLib.SpookyWall(<StartTime>,<Duration>,<startHeight>,<height>,<startRow>,<width>)```

to transform the wall back use
```let normalWall = spookyWall.toWall()```

Head to the Documentation (TBA) for all the provided functions.

### Support me

Add a refresh button to mediocre mapper script window.
