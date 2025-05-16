# fullstack-tech-champs-p2-backend

## Team name: FullStack Tech Champs

## Project Name

## Project description

For project 2, we will create a fantasy football app where the user plays against a bot that uses the Open AI API to choose its players. Users can create a team of real life athletes using data from a fantasy sports API. Each team is then ranked based on how well their players perform on the field. The team with the highest standing (ranking) for that season wins. The backend will use Java, the Spring Framework and PostgreSQL, and the front-end will use Typescript and React. JUnit and Mockito for testing and AWS.

## Whos working on what (frontend, backend, devOps)

- Front-end: Dharun, Ayotunde
- Back-end: Shanjhitha, Cesaire
- DevOps: Mark
- Scrum Master: Shanjhitha

## MVP user stories

- User Story 1: Sign up with email to make it easy to sign in
- Create a team with 5 players (user can only have 1 team) (the computer creates an open AI API user), and the open API uses LLM to choose players
- User can add/draft athletes to their team, and this happens by random chance.
- User plays a game against the computer. Start the season, which lasts 18 weeks, there's 10 seconds per week, Every 10 seconds (1 week passes)
- At the end of the season it shows a winner, whoever won gets the money.

<img src="P2-Design-Diagrams.png">

## External API you're using

We'll be using the NFL API from SportsDataIO, and the Open AI API

## ERD

- Users table: username, password, role
- Teams: team_id, name, qb_id, kicker_id, running_back_id, tide_end_id, wide_receiver_id
- Player (real life athletes)
