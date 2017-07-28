# react-native-refresh-control-wd

- react-native refresh control android expand

## Installation

### First step(Download):
Run `npm i react-native-refresh-control-wd --save`

### Second step(Plugin Installation):

#### Automatic installation

`react-native link react-native-refresh-control-wd` or `rnpm link react-native-refresh-control-wd`


## API(android)


Method            | Type     | Optional | Description
----------------- | -------- | -------- | -----------
onPullDistance    | functoin | number   | Listen for the pull distance
onPullEnable      | function | boolean  | Listen for refresh ture/flase
distance          | props    | number   | Set down how much distance to trigger the refresh

Other apis are consistent with official except "progressViewOffset"
