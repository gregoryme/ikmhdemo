/**
* Copyright (C) by kofe.dev
*/


let currentDateUTCTextElement   = document.getElementById("currentDateUTC");
let currentDateLocalTextElement = document.getElementById("currentDateLocal");
let deltaTextElement            = document.getElementById("delta");
/*<![CDATA[*/
// deadline to milliseconds:
let deadlineDate = [[${deadline.deadlineDate}]];
let deadlineTime = [[${deadline.deadlineTime}]];
let bigDeadlineDateString = deadlineDate + ' ' + deadlineTime;
let dateDeadlineFull = new Date(bigDeadlineDateString);
const dateDeadlineMilliseconds = dateDeadlineFull.getTime(); // main
/*]]>*/





const MILLISECONDS_IN_ONE_DAY    = 86400000;
const MILLISECONDS_IN_ONE_HOUR   = 3600000;
const MILLISECONDS_IN_ONE_MINUTE = 60000;
const MILLISECONDS_IN_ONE_SECOND = 1000;
setInterval(function () {
    let currentDate = new Date();
    // get local time:
    let currentDateMilliseconds = currentDate.getTime();
    // convert local time to UTC time:
    currentDateMilliseconds =
    currentDateMilliseconds + currentDate.getTimezoneOffset() * MILLISECONDS_IN_ONE_MINUTE;
    // delta calculation operations:
    let currentDeltaInMilliseconds = dateDeadlineMilliseconds - currentDateMilliseconds; // main
    let currentDeltaInDays =
    Math.floor(currentDeltaInMilliseconds / MILLISECONDS_IN_ONE_DAY);
    let remInMilliseconds =
    currentDeltaInMilliseconds - currentDeltaInDays * MILLISECONDS_IN_ONE_DAY;
    let currentDeltaPartInHours =
    Math.floor(remInMilliseconds / MILLISECONDS_IN_ONE_HOUR);
    let remInMillisecondsAfterHours =
    remInMilliseconds - currentDeltaPartInHours * MILLISECONDS_IN_ONE_HOUR;
    let currentDeltaPartInMinutes =
    Math.floor(remInMillisecondsAfterHours / MILLISECONDS_IN_ONE_MINUTE);
    let remInMillisecondsAfterMinutes =
    remInMillisecondsAfterHours - currentDeltaPartInMinutes * MILLISECONDS_IN_ONE_MINUTE;
    let currentDeltaPartInSeconds =
    Math.floor(remInMillisecondsAfterMinutes / MILLISECONDS_IN_ONE_SECOND);
    // text about time:
    let textAboutUTCTime = 'Server time is (UTC time): ' + currentDate.toUTCString();
    let textAboutLocalTime = 'You local time is: ' + currentDate;
    // put text about time:
    currentDateUTCTextElement.textContent   = textAboutUTCTime;
    currentDateLocalTextElement.textContent = textAboutLocalTime;
    // text about delta (deadline matter):
    let deltaString;
    if (currentDeltaInMilliseconds >= 0) {
            deltaString =
                'Days: ' +  currentDeltaInDays +
                ' | Hours: ' + currentDeltaPartInHours +
                ' | Minutes: '  + currentDeltaPartInMinutes +
                ' | Seconds: ' + currentDeltaPartInSeconds;
            if (currentDeltaPartInHours == 0) {
                    deltaTextElement.setAttribute("class", "text-danger");
            }
        } else {
            deltaTextElement.setAttribute("class", "text-info");
            deltaString = "Sorry, deadline has been missed.";
    }
    // put text about delta (deadline matter):
    deltaTextElement.textContent = deltaString;
}, 100);