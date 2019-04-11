var runningScore = 0; // store and retrieve from local storage
var questionValue = 0; // will be updated to match difficulty
var newValue = 0;
var qButton = document.getElementsByClassName("question-button")[0];
var downloadTimer = null;
var canvas =  document.querySelector("canvas");
var supportLocalStorage;

if (typeof (Storage) === "undefined") {
    alert("Your browser does not support local storage. Your score will not persist between questions.");
    supportLocalStorage = false;
}

if (supportLocalStorage) {
    runningScore = parseInt(localStorage.runningScore);
    if (isNaN(runningScore)) {
        runningScore = 0;
    }
}

updateRunningScore();

function updateRunningScore() {
    if (runningScore <0) runningScore = 0;
    document.querySelector("#running-score").innerHTML = runningScore;
    if (supportLocalStorage) {
        localStorage.setItem("runningScore", runningScore);
    }
}

function getAnswer(answer) {
    var numAnswer = answer.slice(6);
    var selectedLabel = document.querySelector("label[for=option"+numAnswer+"]");
    var optionsID = document.querySelector("#options");
    var style = window.getComputedStyle ? getComputedStyle(optionsID) : optionsID.currentStyle;
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
    if (selectedLabel.innerHTML == document.querySelector(".answer").innerHTML) {
        selectedLabel.style.color = "green";
        ctx.fillStyle = "green";
        ctx.fillText("CORRECT!", canvas.width/2, canvas.height/2);
        runningScore += parseInt(document.querySelector(".correct-value").innerHTML);
    } else {
        selectedLabel.style.color = "red";
        ctx.fillStyle = "red";
        ctx.fillText("INCORRECT!", canvas.width/2, canvas.height/2);
        runningScore += parseInt(document.querySelector(".incorrect-value").innerHTML);
    }
    optionsID.className="hide";
    qButton.innerHTML="New Question";
    updateRunningScore();
}