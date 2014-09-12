Streams
=========================

Goals
-------------------------
Collections of tools that could be used for different purposes.

Currently two specialized input streams, which could be used to simulate a big stream of data
without consuming the memory

Description and usage
-------------------------
* create stream that will repeat content until reaches 32 GB

repeatingStream = new RepeatingInputStream("123, 124,".bytes(), 32 * 1024 * 1024) 

example result:
123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,

* create stream that will stream json opening tag then repeat content until reaches 32 GB  and stream json closing tag

enclosingRepeatingStream = new EnclosedContentOnlyRepeatingInputStream(
    "{ 'content: ['.bytes(), 
    "123, 124,".bytes(),
    "125] }"
    32 * 1024 * 1024)

example result:
{ 'content: [123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,123, 124,125] }
