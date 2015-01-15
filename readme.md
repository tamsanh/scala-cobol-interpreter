# Scala Cobol Interpreter
## Summary
This is a fixed length streaming file parser written in Scala which supports reading some of COBOL's more funky features, namely switch based schemas, occurrence blocks, and any combination between the two.
This parser features two DSLs, one for reading and one for writing, which are simple to grasp and use.

## DSL
The two DSLs share some commonality.

* The first is that line terminators are both `\n` and `|`.
    The `|` is useful when you want to associate several fields or expressions together, like a Case and its Switch capture column, or a group of fields within an Occurs block.
* The second is that comments go on their own lines, and start with `#`.

Be sure to take a look at `src/tam/cobol_interpreter/examples` for example usage of these DSLs.

### Parser DSL
Everything parsed is represented as a char/byte array. Integers are transformed into their string representations. This, when output, the writer doesn't actually see a single integer to write, but instead a char array containing all the digits of the integer.

#### Expressions
Not all parts of the parser DSL are represented by discrete keywords, as you can see below, some expressions are represented solely by their pattern, as well as position. The most important of which is the **Total Bytes**, which must go at the bottom of the file.

    [TableName]
        The table name should be at the very beginning of the file.
        This essentially does nothing but help to organize the file.

    [Column Name] [Column Type] [Byte Number]
        The column pattern is your basic pattern
            that represents a column that will be parsed.
        The byte number represents how many bytes
            will be consumed by this column.
        Column Types: Int, Char, Comp3, Fill

    Switch [Switch Column Name] [Switch Value Type] [Switch Bytes]
    Case [Case Value]
        [Expressions]
    EndCase
        Switches and Cases allow the programmer to control the flow of parsing.
        Please refer to Switches/Cases for more in depth instruction

    Filler [Byte Number]
        This is a way to skip bytes that have no value and are meaningless.

    Occurs [Occurrence Number]
        [Expressions]
    EndOccurs
        This will multiply the Expressions by the number of occurrences.
        Please be sure to refer to the Writer section about how Occurs are written

    [Total Bytes]
        The final byte value.
        This must be equal to the sum of bytes for each possible branch.
        It will dictate how many bytes are read from the original file.

#### Columns
`Columns` are the basic building block, and will be used throughout the schema. Repetition is allowed in `Column` Name space; data is not overwritten but appended to, and is all accessible by the Writer later on.

##### Column Types
These column types determine specifically how to parse the bytes, and minimal formatting. All the final values are represented as characters in an array. For example, the number `42` is represented as `Array(34, 32)`, where `34` and `32` are the respective character codes for `'4'` and `'2'`

###### Integer
Int type will strip off any prefixed 0s, as well as prefix a '-' when appropriate

    Int: An integer type.
        It will consume however many bytes,
        and spit out a properly formatted integer

###### Char
The rawest form; does not transform the bytes.

    Char: The raw character type.
        Reads whatever bytes it encounters.

###### Comp3
This will decompress the comp3 bytes and then represent it as an integer.

    Comp3: COBOL's compressed integer type.
        This will decompress the Comp3 compressed bytes

###### Fill
Highly recommended that you use the `Filler` expression instead of manually specifying a Fill Column Type. Data in this type will not be saved, only ignored and skipped over.

    Fill: The filler type.
        Use the Filler expression instead

#### Switches/Cases
Switches and Cases are the most complicated part of the parser grammar. For every row of bytes encountered, Switches will parse their designated data, and attempt match that value to one of the cases. It is important to note that the value matching is done by first parsing the switch value using whatever type it holds, and then parsing the value for each associated case using the Char type.
This means that values the **Switch Values will be transformed, but the Case Values will not be**.
Here are some examples of when `Switch Values` equal `Case Values`:

    Examples of: SwitchBytes:Column Type == CaseBytes:(Always Char Type)

            0x9D:Comp3 == -9
            0003:Int == 3
            003:Char == 003
            Hello World:Char == Hello World
            Hello World :Char != Hello World

**The value `_` is the default matcher. Everything will match `_`**
I only pray you do not have a schema that actually depends on `'_'` as a real value

As for the Expressions per case, one thing to note is that Switches **Do NOT consume bytes**. This means that you should make sure each of your cases has enough bytes in total to cover the additional Switch bytes. It also should be noted that the **value parsed by the Switch will still be available to the writer under the Switch Name used**.

### Writer DSL
The writer DSL is much easier to parse, as it only contains 1 type of expression, with 2 options.

#### Expression
The only expression is the Column Name, plus two possible options. The order of the column names matters, as it will be the final output order of the data.

    [Column Name] (Persistent|Persist|Non-Empty|NonEmpty):Optional

##### Persistent
`Persistent` carries column data over, and doesn't reset it when a new row is read from the parser. This is useful if you have a field that only shows up occasionally, but you want its data to be associated with all the other ones.

##### Non-Empty
The row will only be written of the field labeled as `Non-Empty` is not empty. This is helpful when you have a Switch Case that you don't want to write out, but still want to parse and persist. Just label a field in the other switch case as needing to be non-empty before you write. **Caution:** Other fields which you may be interested in will not be written if an empty value appears for this field. You might be able to protect that data with `persist`, but it can still be overwritten in the next set of data.

#### Occurs Note and Rectangularization
Any columns in an `Occurs` block will likely have slightly more data than other expressions. What the writer will do is, for any Column that contains less data than any other Column, it will extend the shorter column to match the size of the longer one. For example, for a given cobol byte set, if there is only 1 field designating a store code and 1 field designating a store date, but 3 fields designating 3 delivery times for that single day, the writer will multiply those single fields to match the length of the delivery times field, and then writer 3 separate rows where all of the values of the Store code and Date are the same, but the 3 delivery times are different. Refer to the examples for a more concrete example of this.

## Examples
A StoreDeliveryTable example can be found in `src/com/tam/cobol_interpreter/examples`.
### Store Delivery Table
The StoreDeliveryTable is a hypothetical situation in which you are given a cobol stream containing information about stores, their thrice-a-day deliveries, and the items in each of the respective deliveries.
The DSL files are commented, and should properly explain how each of the expressions in the language are used. It also offers stylistic ideas for writing the various schema files.


## Todo
In somewhat of a descending priority order. (Except testing)

* ~~Consolidate syntax checking~~
  * ~~Current syntax checking is kind of all over; it should be in the expression generator.~~
* ~Auto Fill~
  * ~For when you're experimenting, and don't want to have to do the math to fill the rest of the bytes~
* Visitors for Schema Expressions
  * Just have visitors for the schema expressions instead of remaking the classes
* Warnings for unused data.
  * It's a bit of a manual process, keeping the parser and writer in sync. It would be nice if the writer were to warn you of data that isn't being utilized, or for the writer to know what data will never be available.
* Cobol Output Support
  * It would be really nice to have a writer which can also write data out in Cobol.
* Proper dependency manager and builder
  * Just relying on IntelliJ to build and distribute isn't going to fly for long
* Optimizing on Singletons
  * There's a lot of unnecessary class use. Would like to reduce the memory footprint with Singletons
* Comments can be better handled
  * No need for a matcher class. They can just be substring/stripped out of lines
* Writing multiple Occurs
  * Due to the nature of the shared contexts, more complex rectangularization is not yet possible. Would like it as a feature.
* Rewind for Parser
  * I like the idea of being able to re-parse bytes of data in another way.
  * You can kind of do this now by taking advantage of Switch's non-consumption
* Add an escape character to case
  * So that those poor souls who need _ as a value can make it available
* More testing
  * Overall, it's a pretty good testing set. Our coverage is around 95%, across the board (excluding some newly made classes and that examples method), but there's definitely cases missing.
