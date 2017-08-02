# Greycat Additional Types

The Greycat Additional Types (gat) project is a plugin for [Greycat](https://github.com/datathings/greycat) offering additional types to the one natively handled by Greycat.
The project currently offers 2 familly of additional types, sets and gat.bitset. More could be added in the future.

## Why additional types?

Since V10, Greycat introduce the notion of Custom types that allow a user to store customize attribute in her nodes.
This made extension of greycat easier to users, however as the development of custom types might be a bit tedious to new users.
 Thus this project has the following objectives: 
 
  * add support to frequently used  Data Structures to Greycat
  * provide an example of custom types development to developers eager to do it by themselves :)
  
## How to use it?

This plugin can be used the same way as other Greycat plugins.

1 . Declare the plugin in your Graph Build

```java
Graph g = new GraphBuilder()
	.withPlugin(new AdditionalTypesPlugin())
	.build();
```

2 . Then call getOrCreatCustom() from the node that need the custom type and cast it.

```java
MyCustomType mycs = (MyCustomType) root.getOrCreateCustom("myattrub",MyCustomType.NAME);
mycs.bar();
```

3 . That's all =)

## About the Custom type in this Plugin

### BitSet and Bitmap
These two custom types are in fact wrapper of regular Java Class, these any modifications to these types requires a call to save in order to keep the modification

### IntSet and LongSet
These two are native custom types, yet in order to reduce the size of stored nodes, only the keys are stored and the information required by the set are recomputed on the fly at every load.