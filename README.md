# Crossway multiplayer game

Crossway is a board game for two players played with a go set invented by Mark Steer in 2007.

## Rules

The match starts with an empty board and Black player starts first. Players take turns adding their stones to the board, one stone per turn.
If you have no more placements available, you forfeit your turn and your opponent can continue making placements until he completes his goal connection.

Player's goals are:
<ul>
  <li><b>Black</b>: form a contiguous sequence of white stones connecting the South edge to the North edge of the board.</li>
  <li><b>White</b>: form a contiguous sequence of white stones connecting the East edge to the West edge of the board.</li>
</ul>

Each stone in the sequence must be connected to neighboring stones in the sequence via horizontal, vertical, or diagonal adjacencies.
A corner is considered to be part of both adjoining edges.

### Diagonal Violation
A player must never complete the formation shown in figure or a 90 degree rotation of this formation.

![image](https://user-images.githubusercontent.com/59869096/140034591-766b3a64-0de7-480b-a1fe-d853ea0d9c7b.png)

### Pie Rule
Black makes the first placement. White then has the option of switching colors with Black, and claiming Black’s first move as his own. If White chooses to exercise the Pie rule, Black then becomes White and now makes the second move of the game. <b>The Pie rule can only be used once and only on the second move of the game.</b>

## Setup
- Go to the release page
  - Download the zip file
  - Extract the server and client jars
- Launch the server jar -> "java -jar server.jar"
- Launch the client jar -> "java -jar client.jar" (first player)
- Launch the client jar -> "java -jar client.jar" (second player)
- Have fun :)

## Devs
<a href="https://github.com/cecia234"> Ceschia Eugenio </a>

<a href="https://github.com/heapify00"> Nigri Davide </a>

<a href="https://github.com/MatteoScorcia"> Scorcia Matteo </a>
