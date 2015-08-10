![NES-D-Pad](/src/main/resources/github/images/NES-D-Pad.jpg)

Solution to [a Golf Code challenge](http://codegolf.stackexchange.com/questions/53805/enter-your-name-via-a-d-pad) using [Hipster](http://www.hipster4j.org/).

DPader
======

DPader is a solution to a challenge that was posted at [Golf Code](http://codegolf.stackexchange.com/). Ordinarily, Golf Code solutions are very concise. However, I wanted to create a robust solution using [State Space Search](https://en.wikipedia.org/wiki/State_space_search). And do so using the Java library named [Hipster](http://www.hipster4j.org/).

A robust solution will allow the underlying problem to change (for example, change the keyboard format) and still produce a valid answer.

This solution also describes what actions were taken so that they can be verified or used.

What was the Golf Code challenge?
---------------------------------

### The puzzle:

Consider a console/hand-held game with a d-pad where you are required to enter a name of sorts. This appeared in many older games before the use of QWERTY was popularized in consoles (e.g. I believe the Wii uses a QWERTY keyboard layout for input). Typically, the on-screen keyboard looks to the effect of*:

Default:

```
0 1 2 3 4 5 6 7 8 9
A B C D E F G H I J
K L M N O P Q R S T
U V W X Y Z _ + ^ =
```
With the case switched:

```
0 1 2 3 4 5 6 7 8 9
a b c d e f g h i j
k l m n o p q r s t
u v w x y z - + ^ =
```
That is, all alphanumeric keys and the following:

`_`: A single space
<br />
`-`: A hyphen
<br />
`+`: Switch case for the next letter only
<br />
`^`: Toggle caps lock (that is, switch the case of all letters)
<br />
`=`: Enter, complete

*Obviously I replaced keys like "BKSP" and "ENTER" with shorter versions

And then the hardware would include a d-pad (or some form of control where you could go `up`, `down`, `left` and `right`)

The screen also typically let you move from one side directly to the other. That is, if you were focussed on the letter `J`, pressing `right` would allow you to move to the letter `A`.

Whenever I was entering my name, I'd always try to work out the quickest way to do so.

#### Goal:

Your program will take string input which may include any alphanumeric character including a space and hyphen, and your goal is to **output the shortest amount of key presses** on the d-pad to output the required string.

#### Considerations:

You do not need to include the key pressed for pressing the actual character.
Focus always starts at the `A`
Enter `=` must be pressed at the end

#### Example:

`input: Code Golf`
<br />
`output: 43`

##### Explained:

`A` -> `C` = 2
<br />
`C` -> `^` = 6 (moving to the left)
<br />
`^` -> `o` = 5
<br />
`o` -> `d` = 2
<br />
`d` -> `e` = 1
<br />
`e` -> `+` = 5
<br />
`+` -> `_` = 1
<br />
`_` -> `+` = 1
<br />
`+` -> `G` = 3
<br />
`G` -> `o` = 3
<br />
`o` -> `l` = 3
<br />
`l` -> `f` = 5
<br />
`f` -> `=` = 6
<br />

Note that it is quicker to hit the `+` twice for a `_` and a `G` than it is to hit `^` once, then swap back.

The winning submission (I'll allow at least 1w) will be the shortest solution (in bytes). As this is my first question, I hope this is clear and not too hard.


Running the solution
--------------------

#### Assembly to a Jar file
`mvn clean compile assembly:single`

#### Execute Jar file
```
cd target
java -jar dpader-0.0.1-SNAPSHOT-jar-with-dependencies.jar "Code Golf"
```

Example output
--------------

Command
`java -jar dpader-0.0.1-SNAPSHOT-jar-with-dependencies.jar "Golf Code"`

Output
```
A -> C = 2
C -> ^ = 6
^ -> o = 5
o -> d = 2
d -> e = 1
e -> + = 5
+ -> _ = 1
_ -> + = 1
+ -> G = 3
G -> o = 3
o -> l = 3
l -> f = 5
f -> = = 6

Total 'movement' actions = 43
Total 'user' actions = 56
Total actions = 59

Additional details...
Total solutions: 1
Total time: 22.64 ms
Total number of iterations: 163
+ Solution 1: 
 - States: 
	[''@(0,1), ''@(1,1), ''@(2,1), 'C'@(2,1), 'C'@(2,1), 'C'@(2,0), 'C'@(2,3), 'C'@(1,3), 'C'@(0,3), 'C'@(9,3), 'C'@(8,3), 'C'@(8,3), 'C'@(8,2), 'C'@(7,2), 'C'@(6,2), 'C'@(5,2), 'C'@(4,2), 'Co'@(4,2), 'Co'@(4,1), 'Co'@(3,1), 'Cod'@(3,1), 'Cod'@(4,1), 'Code'@(4,1), 'Code'@(4,1), 'Code'@(4,0), 'Code'@(4,3), 'Code'@(5,3), 'Code'@(6,3), 'Code'@(7,3), 'Code'@(7,3), 'Code'@(6,3), 'Code_'@(6,3), 'Code_'@(6,3), 'Code_'@(7,3), 'Code_'@(7,3), 'Code_'@(7,2), 'Code_'@(7,1), 'Code_'@(6,1), 'Code_G'@(6,1), 'Code_G'@(6,2), 'Code_G'@(5,2), 'Code_G'@(4,2), 'Code_Go'@(4,2), 'Code_Go'@(3,2), 'Code_Go'@(2,2), 'Code_Go'@(1,2), 'Code_Gol'@(1,2), 'Code_Gol'@(1,1), 'Code_Gol'@(2,1), 'Code_Gol'@(3,1), 'Code_Gol'@(4,1), 'Code_Gol'@(5,1), 'Code_Golf'@(5,1), 'Code_Golf'@(5,0), 'Code_Golf'@(5,3), 'Code_Golf'@(6,3), 'Code_Golf'@(7,3), 'Code_Golf'@(8,3), 'Code_Golf'@(9,3), 'Code_Golf='@(9,3)]
 - Actions: 
	[MOVE_RIGHT, MOVE_RIGHT, PRESS, STRATEGY_TOGGLE_CAPSLOCK, MOVE_UP, MOVE_UP, MOVE_LEFT, MOVE_LEFT, MOVE_LEFT, MOVE_LEFT, PRESS, MOVE_UP, MOVE_LEFT, MOVE_LEFT, MOVE_LEFT, MOVE_LEFT, PRESS, MOVE_UP, MOVE_LEFT, PRESS, MOVE_RIGHT, PRESS, STRATEGY_TOGGLE_SHIFT, MOVE_UP, MOVE_UP, MOVE_RIGHT, MOVE_RIGHT, MOVE_RIGHT, PRESS, MOVE_LEFT, PRESS, STRATEGY_TOGGLE_SHIFT, MOVE_RIGHT, PRESS, MOVE_UP, MOVE_UP, MOVE_LEFT, PRESS, MOVE_DOWN, MOVE_LEFT, MOVE_LEFT, PRESS, MOVE_LEFT, MOVE_LEFT, MOVE_LEFT, PRESS, MOVE_UP, MOVE_RIGHT, MOVE_RIGHT, MOVE_RIGHT, MOVE_RIGHT, PRESS, MOVE_UP, MOVE_UP, MOVE_RIGHT, MOVE_RIGHT, MOVE_RIGHT, MOVE_RIGHT, PRESS]
 - Search information: 
	WeightedNode{state='Code_Golf='@(9,3), cost=59.0, estimation=40.0, score=99.0}
```


What is this output?
====================

The output includes the challenge requirements to display:
* the number of movements between keys (eg `A -> C = 2`)
* the total number of movements (eg `Total 'movement' actions = 43`)

It also includes:
* the number of actions a user would have to take, for example pressing move and select buttons (eg `Total 'user' actions = 56`)
* the total actions the program used to complete challenge, this includes strategy selection (eg `Total actions = 59`)

The output of `Additional details...` is provided by Hipster to show which states were used, and action under taken.

Items in the states list are of the form ''@(x,y). Between the quotes is the message, and between the parenthesis is the location of the cursor in the keyboard map.
 

Lessons from the challenge
==========================

I learned a few things from this exercise...

* Gained experience framing a problem so that it can be solved using state space search
* The importance of using a valid hash code and equals method :-P (thank you Eclipse for auto-generating these)
* The concept of using a "strategy" to achieve an intermediate goal when the actual goal is not immediately obtainable (for example, the next key to be pressed is a lower case key but currently the keyboard is in upper case - so need to press shift or turn caps lock on first)

Future work
===========

This project was a first brush attempt at solving the problem. I welcome any suggestions for improvement or if you'd simply like to comment on the project. I don't plan on building on this example, but will incorperate any good suggestion.
