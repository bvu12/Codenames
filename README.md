# My Personal Project

## Codenames
***
**What will the application do?**

This application will allow users to play the *Codenames* board game (https://en.wikipedia.org/wiki/Codenames_(board_game)). 

Codenames is a game played by a minimum of 4 users, split across two teams: <span style="color:red"> **Red** </span> and <span style="color:blue"> **Blue** </span>.
Each team has a single ***Spymaster*** and the rest of the members are ***Operatives***. The game is played on a 5x5 board with a word occupying each square.
Each team's Spymaster has a hold of the same key - an index of which of the 25 words (representing "*agents*") belong to their respective team. 
The team that starts first has **nine (9)** agents to extract, and other team has **eight (8)** agents to extract. There is one wildcard or "*assassin*" that is to be avoided. The rest of the cards are neutral and do not contribute to winning the game.


The goal of the game is to have alternating Spymasters indicate both a clue and number that best represents the number of agents to be extracted. Clues cannot:
- contain any part of a word on the board
- cannot be phonetically related (i.e. can't rhyme)

Once given a clue (and number of associated agents) the respective team's operative can work together to discuss which words on the grid should be selected. 
Once ready, operatives may select cards one-by-one - in which case there are four outcomes:
1. Your team has correctly selected an agent for your team - your team gets a point and you may continue to guess **up to the number indicated by your Spymaster + 1** or to end your turn
2. Your team has wrongfully selected a neutral card - your turn ends
3. Your team has wrongfully selected the other teams' agent - your turn ends and the other team gets a point
4. Your team has wrongfully selected the assassin - the game immediately ends and you lose!

The game continues until either the first team has extracted all their agents and has won or if the assassin card is revealed.

***
**Who will use it?**

Anyone interested in playing Codenames with a user interface is welcome to play the game.

***
**Why is this project of interested to me?**

Throughout the pandemic and virtual work environment, I had to opportunity to play Codenames with my coworkers online and thought it was very fun. I wanted to develop my own rudimentary implementation of the game.

***
### User Stories
- As a user, I want to be able to see the current game's score
- As a user, I want to generate a random board of 5x5 cards
- As a user, I want to win the game if my team reaches the requisite number of points
- As a user, I want to add 9 agents for one team, 8 agents for the other team, 1 assassin card and 7 neutral cards
- As a user, I want to be able to quit the game on command
- As a user (Spymaster), I want to be able to see which cards are my teams', the opposing teams' and the assassin card
- As a user (Spymaster), I want to be able to provide a clue and number of agents (call it N)
- As a user (Operative), I want to be able to select up to N+1 guesses
- As a user (Operative), I want to be able to end my turn
- As a user (Operative), I want to receive a point for every correct guess
- As a user (Operative), I want to receive give a point to the opposing team if I incorrectly guess one of their cards
- As a user (Operative), I want my turn to end if I select a neutral card
- As a user (Operative), I want the game to end if I select the assassin card
- As a user, I want to be able to save my gamestate to a file 
- As a user, I want to be able to be able to load my gamestate from a file 
- As a user, when I start the application, I want to be given the option to load my saved gamestate

***
**Phase 4: Task 2**

Tue Nov 23 22:26:08 PST 2021  
An invisible NEUTRAL card 'FALL' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible NEUTRAL card 'HOTEL' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible BLUE card 'TABLET' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible BLUE card 'PAN' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible NEUTRAL card 'AFRICA' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible NEUTRAL card 'PRESS' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible ASSASSIN card 'LOCH NESS' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible NEUTRAL card 'BEACH' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
A visible RED card 'COVER' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible RED card 'RAY' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible RED card 'GREEN' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible RED card 'PARK' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible BLUE card 'LOCK' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
A visible RED card 'GOLD' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible BLUE card 'DINOSAUR' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible RED card 'TUBE' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible BLUE card 'DRAFT' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible RED card 'SEAL' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible NEUTRAL card 'JACK' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible NEUTRAL card 'SOLDIER' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible BLUE card 'BAT' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible RED card 'SPOT' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible RED card 'LAP' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible BLUE card 'ARM' has been added to the game board!

Tue Nov 23 22:26:08 PST 2021  
An invisible BLUE card 'FILM' has been added to the game board!

Tue Nov 23 22:26:17 PST 2021  
The BLUE team selected a BLUE card (PAN)!

Tue Nov 23 22:26:17 PST 2021  
The BLUE team selected a BLUE card (TABLET)!

Tue Nov 23 22:26:17 PST 2021  
The BLUE team selected a BLUE card (LOCK)!

Tue Nov 23 22:26:18 PST 2021  
The BLUE team selected a BLUE card (ARM)!

Tue Nov 23 22:26:18 PST 2021  
The BLUE team selected a BLUE card (FILM)!

Tue Nov 23 22:26:18 PST 2021  
The BLUE team selected a NEUTRAL card (SOLDIER)!

Tue Nov 23 22:26:22 PST 2021  
The RED team selected a RED card (PARK)!

Tue Nov 23 22:26:23 PST 2021  
The RED team selected a RED card (GREEN)!

Tue Nov 23 22:26:23 PST 2021  
The RED team selected a RED card (TUBE)!

Tue Nov 23 22:26:23 PST 2021  
The RED team selected a ASSASSIN card (LOCH NESS)!

Tue Nov 23 22:26:23 PST 2021  
The RED team has selected the assassin! 
The BLUE team wins!

***
**Phase 4: Task 3**
Given more time, the following changes would be made to the design of the game:
- Add a bidirectional relationship between Spymaster and Operative. This allows a RED Operative & Spymaster to be linked (and a BLUE Operative & Spymaster to be linked).
- Refactor the Operative class to remove fields related to the game state (specifically the score-keeping aspect of it) and move it to the Board class.
- Refactor the ui classes to reduce coupling. Specifically, break-up the CodenamesGUI class into discrete classes that handle a specific aspect of the GUI (frame creation, event handling, etc.).
