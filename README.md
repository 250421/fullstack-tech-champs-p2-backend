# NFL Fantasy Draft Game

### Team name: FullStack Tech Champs

### Project Roles

- Front-end: Dharun, Ayotunde
- Back-end: Cesaire
- DevOps: Mark

## Project Description

For project 2 here at Revature, we created an NFL fantasy football game app where the user plays against a bot that uses the Open AI API to choose its players. Users can create a team of real life athletes using data from a fantasy sports API. Each team is then ranked based on how well their players perform on the field. The team with the highest standing for that season wins. The backend is a Java Spring Boot application that utilizes Spring Data JPA, JWT for authentication and Mockito for testing. The frontend is a Vite React Typescript application that utilizes ShadCN components and Tailwind CSS for styling. The data for athletes on each team is stored in an AWS RDS PostreSQL database instance. For deployment, this project utilizes Jenkins CI/CD pipelines and an AWS EC2 instance, which runs the Docker images that are created in the Jenkins pipelines.

## MVP user stories

- User Story 1: Sign up with email to make it easy to sign in
- Create a team with 5 players (user can only have 1 team) (the computer creates an open AI API user), and the open API uses LLM to choose players
- User can add/draft athletes to their team, and this happens by random chance.
- User plays a game against the computer. Start the season, which lasts 18 weeks, there's 10 seconds per week, Every 10 seconds (1 week passes)
- At the end of the season it shows a winner, whoever won gets the money.

<img src="P2-Frontend-Design.png">

## External API you're using

We'll be using the NFL API from SportsDataIO, and the Open AI API

## ERD

<img src="P2-ERD-Design.png">
