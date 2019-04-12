<%--
  Created by IntelliJ IDEA.
  User: Sterling
  Date: 4/1/2019
  Time: 5:16 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CIT 261 Fluency Evidence | J. Sterling Wright | BYU-Idaho</title>
    <meta name="description" content="Topic fluency page for Sterling Wright in CIT 261: Mobile Application Development at BYU-Idaho">
    <link rel="stylesheet" href="normalize.css">
    <link rel="stylesheet" href="main.css">
    <link href="https://fonts.googleapis.com/css?family=Bangers" rel="stylesheet">
</head>
<body>
<header class="header">
    <h1>CIT 360 Project</h1>
</header>
<main>
    <div class="game-banner">
        <div>Lightning Trivia</div>
        <div><img src="lightning.png" class="lightning" alt="Lightning bolt"></div>
        <div class="game-small">Running Score
            <p id="running-score"></p>
        </div>
    </div>
    <div class="question-score">
        <form class="qButton" action="Servlet" method="get">
            <button class="question-button"
                    type="submit">
                Next Question
            </button>
        </form>
        <div class="game-small">This Question Worth</div>
        <div class="correct-title">Correct</div>
        <div class="incorrect-title">Incorrect</div>
        <div class="correct-value"><%= request.getAttribute("correct_value")%></div>
        <div class="incorrect-value"><%= request.getAttribute("incorrect_value")%></div>
    </div>
    <div id="category">Category:  <%= request.getAttribute("category")%></div>
    <div id="question">Question:  <%= request.getAttribute("question")%></div>
    Options:<br>
    <canvas></canvas>
    <form action="Servlet" method="get" id="options">
        <p><%= request.getAttribute("options")%></p>
    </form>
    <div class="answer"><%= request.getAttribute("correct_answer")%></div>
</main>
</body>
    <script src="quiz.js"></script>
</html>
