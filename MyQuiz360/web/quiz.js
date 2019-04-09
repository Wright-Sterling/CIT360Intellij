var xmlhttp = new XMLHttpRequest();
var url = "https://opentdb.com/api.php?amount=10";
var supportLocalStorage = true;
var quizArray = "";
var upNext = 0;
var question = "";
var shuffledOptions = "";
var correctAnswer = "";
var showingAnswer = false;
var runningScore = 0; // store and retrieve from local storage
var questionValue = 0; // will be updated to match difficulty
var newValue = 0;
var qButton = document.getElementsByClassName("question-button")[0];
var buttonText = ["Ready...", "Set...", "Go!"];
var buttonTexts = buttonText.length;
var textPointer = 0;
var downloadTimer = null;
var canvas =  document.querySelector("canvas");
var ctx = canvas.getContext("2d");

init();
updateRunningScore();

function init() {
    xmlhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            quizArray = JSON.parse(this.responseText);
        }
    };
    xmlhttp.open("GET", url, true);
    xmlhttp.send();

    if (typeof (Storage) === "undefined") {
        alert("Your browser does not support local storage. Your score will not persist between sessions.");
        supportLocalStorage = false;
    }

    if (supportLocalStorage) {
        runningScore = parseInt(localStorage.runningScore);
        if (isNaN(runningScore)) {
            runningScore = 0;
        }
    }
}

function updateRunningScore() {
    if (runningScore <0) runningScore = 0;
    document.querySelector("#running-score").innerHTML = runningScore;
    if (supportLocalStorage) {
        localStorage.setItem("runningScore", runningScore);
    }
}

function showQuestion() {
    question = quizArray.results[upNext];
    switch (question.difficulty) {
        case "easy":
            questionValue = 400;
            break;
        case "medium":
            questionValue = 600;
            break;
        case "hard":
            questionValue = 1000;
            break;
        default:
            questionValue = 200; //should never be this but...
    }
    upNext++;
    if (upNext == quizArray.results.length) {
        upNext = 0; // just loop for now. Go get more questions eventually
        // in fact, this should be a while loop like a normal game
    }
    document.getElementById("question").innerHTML = "";
    document.getElementById("category").innerHTML =
        "Category: " + question.category;
    document.querySelector("#answer").innerHTML = "Answer:";
    document.querySelector(".correct-value").innerHTML = questionValue;
    document.querySelector(".correct-title").classList.add("pulse");
    document.querySelector(".correct-value").classList.add("pulse");

    ctx.clearRect(0,0,canvas.width,canvas.height);
    qButton.innerHTML = buttonText[0];
    var y = 0;
    setTimeout(myTimeout1, 1500);
    setTimeout(myTimeout2, 3000);
    setTimeout(myTimeout3, 3500);
}

function myTimeout1() {
    qButton.innerHTML = buttonText[1];
}
function myTimeout2() {
    qButton.innerHTML = buttonText[2];
}
function myTimeout3() {
    displayQuestion(question);
    /* This is a good place to remove the animation class so the effect will work next time */
    document.querySelector(".correct-title").classList.remove("pulse");
    document.querySelector(".correct-value").classList.remove("pulse");
}

function valueCountdown() {
    newValue = questionValue;
    downloadTimer = setInterval(function(){
        var correctValueElement = document.getElementsByClassName("correct-value")[0];
        var incorrectValueElement = document.getElementsByClassName("incorrect-value")[0];
        newValue -= 50;
        correctValueElement.innerHTML = newValue;
        incorrectValueElement.innerHTML = (questionValue - newValue) * -1;
        if(newValue <= 0){ // testing here rather than at top on purpose
            clearInterval(downloadTimer);
            correctValueElement.innerHTML = 0;
            incorrectValueElement.innerHTML = questionValue * -1;
        }
    }, 1000);
}

function showAnswer() {
    if (showingAnswer === true) {
        document.querySelector("#answer").classList.remove("show");
        showingAnswer = false;
    } else {
        document.querySelector("#answer").classList.add("show");
        showingAnswer = true;
    }
}

function displayQuestion(nextQuestion) {
    var strOptions = "";
    var qQuest = question.question;
    var qOpts = question.incorrect_answers;
    qOpts.push(question.correct_answer);
    shuffledOptions = qOpts.sort(function(a, b){return 0.5 - Math.random()}); // shuffle array

    valueCountdown();
    for (i = 0; i < qOpts.length; i++) {
        var strOptions = strOptions +
            "<p>"+
            "<input type='radio' id='option"+i+"' name='radio-group'"+
            "onclick='getAnswer(this.id)'>"+
            "<label for='option"+i+"'>"+qOpts[i]+"</label>"+
            "</p>"
    }
    document.querySelector("#options").className="show";
    document.getElementById("question").innerHTML = qQuest;
    document.getElementById("options").innerHTML = strOptions;
    document.getElementById("answer").innerHTML = "Answer: " + question.correct_answer;
}

function getAnswer(answer) {
    clearInterval(downloadTimer);
    var numAnswer = answer.slice(6);
    var selectedButton = document.getElementById(answer);
    var selectedLabel = document.querySelector("label[for=option"+numAnswer+"]");
    var feedbackText = document.querySelector("#feedback");
    var optionsID = document.querySelector("#options");
    var style = window.getComputedStyle ? getComputedStyle(optionsID) : optionsID.curentStyle;
    var optionsArea = {
        marginLeft: parseInt(style.marginLeft) || 0,
        offsetTop: optionsID.offsetTop,
        offsetLeft: optionsID.offsetLeft,
        offsetHeight: optionsID.offsetHeight,
        offsetWidth: optionsID.offsetWidth
    };
    canvas.top = optionsArea.offsetTop+"px";
    canvas.style.left = optionsArea.offsetLeft+"px";
    canvas.height = optionsArea.offsetHeight;
    var ctx = canvas.getContext("2d");
    ctx.font = "30px Arial";
    ctx.textAlign = "center";
    if (shuffledOptions[numAnswer] == question.correct_answer) {
        selectedLabel.style.color = "green";
        ctx.fillStyle = "green";
        ctx.fillText("CORRECT!", canvas.width/2, canvas.height/2);
        runningScore += newValue;
    } else {
        selectedLabel.style.color = "red";
        ctx.fillStyle = "red";
        ctx.fillText("INCORRECT!", canvas.width/2, canvas.height/2);
        runningScore -= questionValue - newValue;
    }
    optionsID.className="hide";
    qButton.innerHTML="New Question";
    updateRunningScore();
}

function showDeveloper() {
    var sec=document.querySelector(".developer");
    if(sec.classList.contains("show")) {
        sec.classList.remove("show");
    } else {
        sec.classList.add("show");
    }
}

function showAbout() {
    var sec=document.querySelector(".about");
    if(sec.classList.contains("show")) {
        sec.classList.remove("show");
    } else {
        sec.classList.add("show");
    }
}

/* removed settings option due to lack of time
function showSettings() {
    var sec=document.querySelector(".settings");
    if(sec.classList.contains("show")) {
        sec.classList.remove("show");
    } else {
        sec.classList.add("show");
    }
}
*/

function showRules() {
    var sec=document.querySelector(".rules");
    if(sec.classList.contains("show")) {
        sec.classList.remove("show");
    } else {
        sec.classList.add("show");
    }
}

function clearStorage() {
    localStorage.clear();
}

function showBorder() {
    if(canvas.classList.contains("showBorder")) {
        canvas.classList.remove("showBorder");
    }else {
        canvas.classList.add("showBorder");
    }
}